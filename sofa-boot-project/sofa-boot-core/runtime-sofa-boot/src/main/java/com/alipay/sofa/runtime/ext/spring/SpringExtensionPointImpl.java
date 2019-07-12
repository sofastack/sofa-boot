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

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;

import com.alipay.sofa.common.xmap.annotation.spring.XMapSpring;
import com.alipay.sofa.runtime.ext.component.ExtensionInternal;
import com.alipay.sofa.runtime.ext.component.ExtensionPointImpl;
import com.alipay.sofa.runtime.ext.component.XMapContext;

/**
 * Extension point implement in spring env
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */

public class SpringExtensionPointImpl extends ExtensionPointImpl {

    private static final long serialVersionUID = -7891847787889605861L;

    private XMapSpring        xmapSpring;

    public SpringExtensionPointImpl(String name, Class<?> contributionClass) {
        super(name, contributionClass);
    }

    @Override
    public Object[] loadContributions(ExtensionInternal extension) throws Exception {

        ApplicationContext applicationContext = null;

        if (extension instanceof SpringExtensionImpl) {
            applicationContext = ((SpringExtensionImpl) extension).getApplicationContext();
        }
        if (contributions != null) {
            xmapSpring = new XMapSpring();
            for (Class<?> contrib : contributions) {
                xmapSpring.register(contrib, applicationContext);
            }

            Object[] contribs = xmapSpring.loadAll(new XMapContext(extension.getAppClassLoader()),
                extension.getElement());
            for (Object o : contribs) {
                if (applicationContext != null && o instanceof BeanFactoryAware) {
                    ((BeanFactoryAware) o).setBeanFactory(applicationContext
                        .getAutowireCapableBeanFactory());
                }
            }
            extension.setContributions(contribs);

            return contribs;
        }
        return null;
    }

}
