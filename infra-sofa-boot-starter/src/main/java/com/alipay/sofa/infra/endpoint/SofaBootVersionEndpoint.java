/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.infra.endpoint;

import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import com.alipay.sofa.infra.standard.AbstractSofaBootMiddlewareVersionFacade;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.*;

/**
 * SOFABootVersionEndpoint
 *
 * {@link org.springframework.core.io.support.PropertiesLoaderSupport#loadProperties(java.util.Properties)}

 *
 * @author yangguanchao
 * @since 2018/03/26
 */
public class SofaBootVersionEndpoint extends AbstractEndpoint<Object> implements ApplicationContextAware {

    public static final String                  SOFA_BOOT_VERSION_PREFIX = "sofaboot/versions";

    private Logger                              logger                   = InfraHealthCheckLoggerFactory
                                                                             .getLogger(SofaBootVersionEndpoint.class);

    private List<Object>                        endpointResult           = null;

    private PathMatchingResourcePatternResolver resourcePatternResolver  = new PathMatchingResourcePatternResolver();

    private ApplicationContext                  applicationContext;

    public SofaBootVersionEndpoint() {
        super(SOFA_BOOT_VERSION_PREFIX, false);
    }

    @Override
    public Object invoke() {
        if (this.endpointResult != null) {
            //cache
            return this.endpointResult;
        }
        List<Object> result = new ArrayList<Object>();
        //first https://stackoverflow.com/questions/9259819/how-to-read-values-from-properties-file
        try {
            List<Properties> gavResult = new LinkedList<Properties>();
            this.generateGavResult(gavResult);
            if (gavResult.size() > 0) {
                result.addAll(gavResult);
            }
        } catch (Exception ex) {
            logger.warn("Load properties failed " + " : " + ex.getMessage());
        }
        //second Interface
        @SuppressWarnings("rawtypes")
        Collection<AbstractSofaBootMiddlewareVersionFacade> sofaBootMiddlewares = BeanFactoryUtils
            .beansOfTypeIncludingAncestors(this.applicationContext, AbstractSofaBootMiddlewareVersionFacade.class)
            .values();

        for (AbstractSofaBootMiddlewareVersionFacade sofaBootMiddleware : sofaBootMiddlewares) {
            if (sofaBootMiddleware == null) {
                continue;
            }
            Map<String, Object> info = this.getVersionInfo(sofaBootMiddleware);
            if (info != null && info.size() > 0) {
                result.add(info);
            }
        }
        //cache
        this.endpointResult = result;
        return this.endpointResult;
    }

    private void generateGavResult(List<Properties> gavResult) throws IOException {
        //read sofa.versions.properties
        this.generateSofaVersionProperties(gavResult);
    }

    private void generateSofaVersionProperties(List<Properties> gavResult) throws IOException {
        List<Resource> pomResourceLocations = getSofaVersionsPropertiesResources();
        if (pomResourceLocations == null || pomResourceLocations.size() <= 0) {
            return;
        }
        for (int i = 0; i < pomResourceLocations.size(); i++) {
            Resource sofaVersionsResource = pomResourceLocations.get(i);
            Properties sofaVersionsProperties = loadProperties(sofaVersionsResource);
            gavResult.add(sofaVersionsProperties);
        }
    }

    /**
     * Load properties into the given instance.
     *
     * @param resourceLocation the Resource locations to load
     * @throws IOException in case of I/O errors
     */
    private Properties loadProperties(Resource resourceLocation) throws IOException {
        Properties result = new Properties();
        if (resourceLocation != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Loading properties file from " + resourceLocation);
            }
            try {
                PropertiesLoaderUtils.fillProperties(
                    result, new EncodedResource(resourceLocation));
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not load properties from " + resourceLocation + ": " + ex.getMessage());
                }
            }
        }
        return result;
    }

    private List<Resource> getSofaVersionsPropertiesResources() throws IOException {
        List<String> paths = new ArrayList<String>();
        String path1 = "classpath*:META-INF/sofa.versions.properties";
        paths.add(path1);
        return getResources(paths);
    }

    private List<Resource> getResources(List<String> paths) throws IOException {
        if (paths == null || paths.size() == 0) {
            return null;
        }
        List<Resource> resultList = new ArrayList<Resource>();
        for (int i = 0; i < paths.size(); i++) {
            Resource[] resources = resourcePatternResolver.getResources(paths.get(i));
            List<Resource> resourceList = Arrays.asList(resources);
            resultList.addAll(resourceList);
        }
        return resultList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private Map<String, Object> getVersionInfo(AbstractSofaBootMiddlewareVersionFacade sofaBootMiddleware) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("name", sofaBootMiddleware.getName());
        result.put("version", sofaBootMiddleware.getVersion());
        result.put("authors", sofaBootMiddleware.getAuthors());
        result.put("docs", sofaBootMiddleware.getDocs());
        Map<String, Object> runtimeInfo = sofaBootMiddleware.getRuntimeInfo();
        result.put("runtimeInfo", runtimeInfo);
        return result;
    }
}
