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
package com.alipay.sofa.boot.env;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuanxuan
 * @version : ScenesEnvironmentPostProcessor.java, v 0.1 2023年03月03日 15:41 yuanxuan Exp $
 */
public class ScenesEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String SCENES_FILE_DIR = "sofa-boot/scenes";

    private static final String SCENE_ENABLE = "sofa.boot.scenes.enable";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        ResourceLoader resourceLoader = application.getResourceLoader();
        resourceLoader = (resourceLoader != null) ? resourceLoader : new DefaultResourceLoader();
        List<PropertySourceLoader> propertySourceLoaders = SpringFactoriesLoader.loadFactories(PropertySourceLoader.class,
                getClass().getClassLoader());
        String scenes = environment.getProperty(SCENE_ENABLE);
        if (!StringUtils.hasText(scenes)) {
            return;
        }
        List<SceneConfigDataReference> sceneConfigDataReferences = scenesResources(resourceLoader, propertySourceLoaders,
                List.of(scenes.split(",")));
        processAndApply(sceneConfigDataReferences, environment);

    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 100;
    }

    private List<SceneConfigDataReference> scenesResources(ResourceLoader resourceLoader, List<PropertySourceLoader> propertySourceLoaders,
                                                           List<String> scenes) {
        List<SceneConfigDataReference> resources = new ArrayList<>();
        if (scenes != null && !scenes.isEmpty()) {
            scenes.forEach(scene -> propertySourceLoaders.forEach(psl -> {
                for (String extension : psl.getFileExtensions()) {
                    String location =
                            "classpath:/" + SCENES_FILE_DIR + File.separator + scene + "." + extension;
                    Resource resource = resourceLoader.getResource(location);
                    if (resource.exists()) {
                        resources.add(new SceneConfigDataReference(location, resource, psl));
                    }
                }
            }));
        }
        return resources;
    }

    /**
     * Process all scene config  property sources to the
     * {@link org.springframework.core.env.Environment}.
     */
    private void processAndApply(List<SceneConfigDataReference> sceneConfigDataReferences, ConfigurableEnvironment environment) {
        if (sceneConfigDataReferences != null && !sceneConfigDataReferences.isEmpty()) {
            for (SceneConfigDataReference sceneConfigDataReference :
                    sceneConfigDataReferences) {
                try {
                    List<PropertySource<?>> propertySources = sceneConfigDataReference.propertySourceLoader.load(
                            sceneConfigDataReference.getName(),
                            sceneConfigDataReference.getResource());
                    if (propertySources != null) {
                        propertySources.forEach(environment.getPropertySources()::addLast);
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("IO error on loading scene config data from " + sceneConfigDataReference.name, e);
                }
            }
        }
    }

    private static class SceneConfigDataReference {

        private String               name;
        private Resource             resource;
        private PropertySourceLoader propertySourceLoader;

        public SceneConfigDataReference(String name, Resource resource, PropertySourceLoader propertySourceLoader) {
            this.name = name;
            this.resource = resource;
            this.propertySourceLoader = propertySourceLoader;
        }

        /**
         * Getter method for property <tt>resource</tt>.
         *
         * @return property value of resource
         */
        public Resource getResource() {
            return resource;
        }

        /**
         * Setter method for property <tt>resource</tt>.
         *
         * @param resource value to be assigned to property resource
         */
        public void setResource(Resource resource) {
            this.resource = resource;
        }

        /**
         * Getter method for property <tt>propertySourceLoader</tt>.
         *
         * @return property value of propertySourceLoader
         */
        public PropertySourceLoader getPropertySourceLoader() {
            return propertySourceLoader;
        }

        /**
         * Setter method for property <tt>propertySourceLoader</tt>.
         *
         * @param propertySourceLoader value to be assigned to property propertySourceLoader
         */
        public void setPropertySourceLoader(PropertySourceLoader propertySourceLoader) {
            this.propertySourceLoader = propertySourceLoader;
        }

        /**
         * Getter method for property <tt>name</tt>.
         *
         * @return property value of name
         */
        public String getName() {
            return name;
        }

        /**
         * Setter method for property <tt>name</tt>.
         *
         * @param name value to be assigned to property name
         */
        public void setName(String name) {
            this.name = name;
        }
    }
}
