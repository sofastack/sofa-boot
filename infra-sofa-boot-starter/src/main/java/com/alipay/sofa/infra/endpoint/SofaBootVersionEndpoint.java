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
import org.slf4j.Logger;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;

import static com.alipay.sofa.infra.constants.SofaBootInfraConstants.SOFA_BOOT_VERSION_PROPERTIES;

/**
 * SOFABootVersionEndpoint
 *
 * {@link org.springframework.core.io.support.PropertiesLoaderSupport#loadProperties(java.util.Properties)}

 *
 * @author yangguanchao
 * @author qilong.zql
 * @since 2018/03/26
 */
@Endpoint(id = "versions")
public class SofaBootVersionEndpoint {

    private Logger                              logger                  = InfraHealthCheckLoggerFactory
                                                                            .getLogger(SofaBootVersionEndpoint.class);

    private List<Object>                        endpointResult          = new ArrayList<>();

    private PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    @ReadOperation
    public List<Object> versions() {
        if (endpointResult.isEmpty()) {
            try {
                List<Object> result = new ArrayList<>();
                Resource[] versionResource = resourcePatternResolver
                    .getResources(SOFA_BOOT_VERSION_PROPERTIES);
                for (Resource resource : versionResource) {
                    Properties versionProperties = loadProperties(resource);
                    result.add(versionProperties);
                }
                endpointResult = result;
            } catch (Exception ex) {
                logger.warn("Load properties failed: {}", ex.getMessage());
            }
        }
        return endpointResult;
    }

    /**
     * Load properties into the given sofa.versions.properties resource.
     *
     * @param resourceLocation the resource locations to load
     */
    private Properties loadProperties(Resource resourceLocation) {
        Assert.notNull(resourceLocation, "Properties resource location must not be null.");

        logger.info("Loading properties file from {}", resourceLocation);
        Properties result = new Properties();
        try {
            PropertiesLoaderUtils.fillProperties(result, new EncodedResource(resourceLocation));
        } catch (IOException ex) {
            logger.warn("Error occurred when loading properties from {}: {}", resourceLocation,
                ex.getMessage());
        }
        return result;
    }
}
