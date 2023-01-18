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
package com.alipay.sofa.runtime.ext.component;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.ClassUtils;

import com.alipay.sofa.common.xmap.XMap;

/**
 * ExtensionPoint Implementation.
 *
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public class ExtensionPointImpl implements ExtensionPointInternal, Serializable {

    @Serial
    private static final long          serialVersionUID = 3939941819263075106L;

    protected String                   name;

    protected String                   documentation;

    protected transient List<Class<?>> contributions    = new ArrayList<>(2);

    protected ClassLoader              beanClassLoader;

    public ExtensionPointImpl(String name, Class<?> contributionClass) {
        this.name = name;
        if (contributionClass != null) {
            this.contributions.add(contributionClass);
        }
    }

    @Override
    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    @Override
    public List<Class<?>> getContributions() {
        return contributions;
    }

    @Override
    public boolean hasContribution() {
        return contributions.size() > 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDocumentation() {
        return documentation;
    }

    @Override
    public void addContribution(Class<?> javaClass) {
        this.contributions.add(javaClass);
    }

    @Override
    public void addContribution(String className) {
        this.addContribution(ClassUtils.resolveClassName(className, beanClassLoader));
    }

    @Override
    public Object[] loadContributions(ExtensionInternal extension) throws Exception {
        if (contributions != null) {
            XMap xmap = new XMap();
            for (Class<?> contrib : contributions) {
                xmap.register(contrib);
            }
            Object[] contributions = xmap.loadAll(new XMapContext(extension.getAppClassLoader()),
                extension.getElement());
            extension.setContributions(contributions);
            return contributions;
        }

        return null;
    }
}
