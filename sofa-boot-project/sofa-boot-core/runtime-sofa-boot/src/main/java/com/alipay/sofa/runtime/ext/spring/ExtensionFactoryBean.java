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
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.api.component.Property;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import org.slf4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.BEAN_ID;
import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.EXTENSION_POINT_NAME;
import static com.alipay.sofa.runtime.spi.component.ComponentDefinitionInfo.SOURCE;

/**
 * Implementation of {@link org.springframework.beans.factory.FactoryBean} to register extension.
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @author khotyn
 * @since 2.6.0
 */
public class ExtensionFactoryBean extends AbstractExtFactoryBean {

    private static final Logger LOGGER = SofaBootLoggerFactory
                                           .getLogger(ExtensionFactoryBean.class);

    /* extension bean */
    private String              bean;

    /* extension point name */
    private String              point;

    /* content need to be parsed with XMap */
    private Element             content;

    private String[]            require;

    private ClassLoader         classLoader;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(applicationContext,
            "required property 'applicationContext' has not been set");
        Assert.notNull(getPoint(), "required property 'point' has not been set for extension");
        // checked
        if (!StringUtils.hasText(getPoint())) {
            throw new IllegalArgumentException("'point' have to be specified");
        }

        if (getBeanClassLoaderWrapper() == null
            || getBeanClassLoaderWrapper().getInnerClassLoader() == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        } else {
            classLoader = getBeanClassLoaderWrapper().getInnerClassLoader();
        }

        try {
            publishAsNuxeoExtension();
        } catch (Exception e) {
            LOGGER.error("failed to publish extension", e);
            throw e;
        }
    }

    private void publishAsNuxeoExtension() throws Exception {
        ExtensionBuilder extensionBuilder = ExtensionBuilder.genericExtension(this.getPoint(),
            this.getContent(), applicationContext, classLoader);
        extensionBuilder.setExtensionPoint(getExtensionPointComponentName());

        ComponentInfo componentInfo = new ExtensionComponent(extensionBuilder.getExtension(),
            sofaRuntimeContext);
        ComponentDefinitionInfo extensionDefinitionInfo = new ComponentDefinitionInfo();
        extensionDefinitionInfo.setInterfaceMode(InterfaceMode.spring);
        extensionDefinitionInfo.putInfo(BEAN_ID, bean);
        extensionDefinitionInfo.putInfo(EXTENSION_POINT_NAME, point);
        Property property = new Property();
        property.setName(SOURCE);
        property.setValue(extensionDefinitionInfo);
        componentInfo.getProperties().put(SOURCE, property);
        componentInfo.setApplicationContext(applicationContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    private ComponentName getExtensionPointComponentName() {
        return ComponentNameFactory.createComponentName(
            ExtensionPointComponent.EXTENSION_POINT_COMPONENT_TYPE, this.getBean() + LINK_SYMBOL
                                                                    + this.getPoint());
    }

    public void setContribution(String[] contribution) throws Exception {
        if (contribution != null && contribution.length != 0) {
            Class<?>[] contrClass = new Class[contribution.length];
            int i = 0;
            for (String cntr : contribution) {
                contrClass[i] = classLoader.loadClass(cntr);
                i++;
            }
        }
    }

    public void setRequire(String[] require) {
        this.require = require;
    }

    public String[] getRequire() {
        return require;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public String getBean() {
        return bean;
    }

    public void setContent(Element content) {
        this.content = content;
    }

    public Element getContent() {
        return content;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPoint() {
        return point;
    }

    @Override
    public String toString() {
        return "ExtensionPointTarget: " + bean;
    }
}
