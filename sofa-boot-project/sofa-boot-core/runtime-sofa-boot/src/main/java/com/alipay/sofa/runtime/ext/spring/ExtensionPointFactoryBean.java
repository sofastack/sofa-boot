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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.spring.SpringImplementationImpl;

/**
 * Extension point factory bean
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @since 2.6.0
 */
public class ExtensionPointFactoryBean extends AbstractExtFactoryBean {

    /* extension point name */
    private String   name;

    /* contributions for the extension point */
    private String[] contribution;

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
                    if (SofaLogger.isDebugEnabled()) {
                        SofaLogger
                            .debug("target bean ["
                                   + targetBeanName
                                   + "] is a non-lazy singleton; forcing initialization before publishing");
                    }
                    beanFactory.getBean(targetBeanName);
                }
            }
        }

        try {
            publishAsNuxeoExtensionPoint(extensionPointClass);
        } catch (Exception e) {
            SofaLogger.error(e, "Failed to publish extension point.");
            throw e;
        }
    }

    private void publishAsNuxeoExtensionPoint(Class<?> beanClass) throws Exception {
        Assert.notNull(beanClass, "Service must be implement!");

        ExtensionPointBuilder extensionPointBuilder = ExtensionPointBuilder.genericExtensionPoint(
            this.name, applicationContext.getClassLoader());

        if (this.contribution != null && this.contribution.length != 0) {
            for (int i = 0; i < contribution.length; i++) {
                extensionPointBuilder.addContribution(contribution[i]);
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
}
