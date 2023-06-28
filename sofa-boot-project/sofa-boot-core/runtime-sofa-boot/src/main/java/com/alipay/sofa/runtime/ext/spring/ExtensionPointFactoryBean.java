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
package com.alipay.sofa.runtime.ext.spring;

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.Implementation;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.BEAN_ID;
import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.EXTENSION_POINT_NAME;
import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.SOURCE;

/**
 * Implementation of {@link org.springframework.beans.factory.FactoryBean} to register extension point.
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @since 2.6.0
 */
public class ExtensionPointFactoryBean extends AbstractExtFactoryBean {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(ExtensionPointFactoryBean.class);

    /* extension point name */
    private String              name;

    /* contributions for the extension point */
    private String[]            contribution;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(beanFactory, "required property 'beanFactory' has not been set");
        Assert.notNull(name, "required property 'name' has not been set for extension point");

        if (!StringUtils.hasText(targetBeanName) && target == null
            || (StringUtils.hasText(targetBeanName) && target != null)) {
            throw new IllegalArgumentException(
                "either 'target' or 'targetBeanName' have to be specified");
        }

        // determine serviceClass (can still be null if using a FactoryBean
        // which doesn't declare its product type)
        Class<?> extensionPointClass = (target != null ? target.getClass() : beanFactory
            .getType(targetBeanName));

        // check if there is a reference to a non-lazy bean
        if (StringUtils.hasText(targetBeanName)) {
            if (beanFactory instanceof ConfigurableListableBeanFactory) {
                // in case the target is non-lazy, singleton bean, initialize it
                BeanDefinition beanDef = ((ConfigurableListableBeanFactory) beanFactory)
                    .getBeanDefinition(targetBeanName);

                if (beanDef.isSingleton() && !beanDef.isLazyInit()) {
                    LOGGER
                        .atDebug()
                        .log(
                            "target bean [{}] is a non-lazy singleton; forcing initialization before publishing",
                            targetBeanName);
                    beanFactory.getBean(targetBeanName);
                }
            }
        }

        try {
            publishAsNuxeoExtensionPoint(extensionPointClass);
        } catch (Exception e) {
            LOGGER.error("Failed to publish extension point.", e);
            throw e;
        }
    }

    private void publishAsNuxeoExtensionPoint(Class<?> beanClass) throws Exception {
        Assert.notNull(beanClass, "Service must be implement!");

        ExtensionPointBuilder extensionPointBuilder = ExtensionPointBuilder.genericExtensionPoint(
            this.name, applicationContext.getClassLoader());

        if (this.contribution != null && this.contribution.length != 0) {
            for (String s : contribution) {
                extensionPointBuilder.addContribution(s);
            }
        }
        Assert.hasLength(beanName,
            "required property 'beanName' has not been set for creating implementation");
        Assert.notNull(applicationContext,
            "required property 'applicationContext' has not been set for creating implementation");
        Implementation implementation = new SpringImplementationImpl(targetBeanName,
            applicationContext);
        ComponentInfo extensionPointComponent = new ExtensionPointComponent(
            extensionPointBuilder.getExtensionPoint(), sofaRuntimeContext, implementation);
        ComponentDefinitionInfo definitionInfo = new ComponentDefinitionInfo();
        definitionInfo.setInterfaceMode(InterfaceMode.spring);
        definitionInfo.putInfo(EXTENSION_POINT_NAME, name);
        definitionInfo.putInfo(BEAN_ID, targetBeanName);
        Property property = new Property();
        property.setName(SOURCE);
        property.setValue(definitionInfo);
        extensionPointComponent.getProperties().put(SOURCE, property);
        extensionPointComponent.setApplicationContext(applicationContext);
        sofaRuntimeContext.getComponentManager().register(extensionPointComponent);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContribution(String[] contribution) throws Exception {
        this.contribution = contribution;
    }

    @Override
    public String toString() {
        return "ExtensionPointTarget: " + targetBeanName;
    }
}
