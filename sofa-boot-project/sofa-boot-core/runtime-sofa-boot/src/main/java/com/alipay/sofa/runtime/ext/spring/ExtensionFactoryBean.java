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

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.ext.component.ExtensionPointComponent;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;

/**
 *
 * @author xi.hux@alipay.com
 * @author yangyanzhao@alipay.com
 * @author khotyn
 * @since 2.6.0
 */
public class ExtensionFactoryBean extends AbstractExtFactoryBean {

    /* extension bean */
    private String      bean;

    /* extension point name */
    private String      point;

    /* content need to be parsed with XMap */
    private Element     content;

    private String[]    require;

    private ClassLoader classLoader;

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(applicationContext,
            "required property 'applicationContext' has not been set");
        Assert.notNull(getPoint(), "required property 'point' has not been set for extension");
        // checked
        if (!StringUtils.hasText(getPoint())) {
            throw new IllegalArgumentException("'point' have to be specified");
        }

        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }

        try {
            publishAsNuxeoExtension();
        } catch (Exception e) {
            SofaLogger.error(e, "failed to publish extension");
            throw e;
        }
    }

    private void publishAsNuxeoExtension() throws Exception {
        ExtensionBuilder extensionBuilder = ExtensionBuilder.genericExtension(this.getPoint(),
            this.getContent(), applicationContext, classLoader);
        extensionBuilder.setExtensionPoint(getExtensionPointComponentName());

        ComponentInfo componentInfo = new ExtensionComponent(extensionBuilder.getExtension(),
            sofaRuntimeContext);
        sofaRuntimeContext.getComponentManager().register(componentInfo);
    }

    private ComponentName getExtensionPointComponentName() {
        return ComponentNameFactory.createComponentName(
            ExtensionPointComponent.EXTENSION_POINT_COMPONENT_TYPE, this.getBean() + LINK_SYMBOL
                                                                    + this.getPoint());
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
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
}
