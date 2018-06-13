/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.infra.endpoint;

import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import com.alipay.sofa.infra.standard.AbstractSofaBootMiddlewareVersionFacade;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "com.alipay.sofa.versions")
public class SofaBootVersionEndpoint extends AbstractEndpoint<Object> implements
                                                                     ApplicationContextAware {
    public static final String                  SOFA_BOOT_VERSION_PREFIX     = "sofaboot_versions";
    public static final String                  SOFA_BOOT_VERSION_PROPERTIES = "classpath*:META-INF/sofa.versions.properties";

    private Logger                              logger                       = InfraHealthCheckLoggerFactory
                                                                                 .getLogger(SofaBootVersionEndpoint.class);

    private List<Object>                        endpointResult               = null;

    private PathMatchingResourcePatternResolver resourcePatternResolver      = new PathMatchingResourcePatternResolver();

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
        List<Object> result = new ArrayList<>();
        //first https://stackoverflow.com/questions/9259819/how-to-read-values-from-properties-file
        try {
            List<Properties> gavResult = new LinkedList<>();
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
            .beansOfTypeIncludingAncestors(this.applicationContext,
                AbstractSofaBootMiddlewareVersionFacade.class).values();

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
        for (Resource sofaVersionsResource : pomResourceLocations) {
            Properties sofaVersionsProperties = loadProperties(sofaVersionsResource);
            gavResult.add(sofaVersionsProperties);
        }
    }

    /**
     * Load properties into the given instance.
     *
     * @param resourceLocation the Resource locations to load
     */
    private Properties loadProperties(Resource resourceLocation) {
        Properties result = new Properties();
        if (resourceLocation != null) {
            if (logger.isInfoEnabled()) {
                logger.info("Loading properties file from " + resourceLocation);
            }
            try {
                PropertiesLoaderUtils.fillProperties(result, new EncodedResource(resourceLocation));
            } catch (IOException ex) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Could not load properties from " + resourceLocation + ": "
                                + ex.getMessage());
                }
            }
        }
        return result;
    }

    private List<Resource> getSofaVersionsPropertiesResources() throws IOException {
        List<String> paths = Collections.singletonList(SOFA_BOOT_VERSION_PROPERTIES);
        return getResources(paths);
    }

    private List<Resource> getResources(List<String> paths) throws IOException {
        if (paths == null || paths.size() == 0) {
            return null;
        }
        List<Resource> resultList = new ArrayList<>();
        for (String path : paths) {
            Resource[] resources = resourcePatternResolver.getResources(path);
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
        Map<String, Object> result = new HashMap<>();
        result.put("name", sofaBootMiddleware.getName());
        result.put("version", sofaBootMiddleware.getVersion());
        result.put("authors", sofaBootMiddleware.getAuthors());
        result.put("docs", sofaBootMiddleware.getDocs());
        Map<String, Object> runtimeInfo = sofaBootMiddleware.getRuntimeInfo();
        result.put("runtimeInfo", runtimeInfo);
        return result;
    }
}
