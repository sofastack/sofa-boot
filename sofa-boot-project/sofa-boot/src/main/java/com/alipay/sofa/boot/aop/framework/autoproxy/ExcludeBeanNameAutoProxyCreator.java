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
package com.alipay.sofa.boot.aop.framework.autoproxy;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Extension for {@link BeanNameAutoProxyCreator} to support exclude specify bean names.
 *
 * @author huzijie
 * @version ExcludeBeanNameAutoProxyCreator.java, v 0.1 2024年01月04日 4:24 PM huzijie Exp $
 */
public class ExcludeBeanNameAutoProxyCreator extends BeanNameAutoProxyCreator {

    @Nullable
    private List<String> excludeBeanNames;

    /**
     * Set the names of the beans that should not automatically get wrapped with proxies.
     * A name can specify a prefix to match by ending with "*", e.g. "myBean,tx*"
     * will match the bean named "myBean" and all beans whose name start with "tx".
     * <p><b>NOTE:</b> In case of a FactoryBean, only the objects created by the
     * FactoryBean will get proxied. This default behavior applies as of Spring 2.0.
     * If you intend to proxy a FactoryBean instance itself (a rare use case, but
     * Spring 1.2's default behavior), specify the bean name of the FactoryBean
     * including the factory-bean prefix "&amp;": e.g. "&amp;myFactoryBean".
     * @see org.springframework.beans.factory.FactoryBean
     * @see org.springframework.beans.factory.BeanFactory#FACTORY_BEAN_PREFIX
     */
    public void setExcludeBeanNames(String... beanNames) {
        Assert.notEmpty(beanNames, "'excludeBeanNames' must not be empty");
        this.excludeBeanNames = new ArrayList<>(beanNames.length);
        for (String mappedName : beanNames) {
            this.excludeBeanNames.add(mappedName.strip());
        }
    }

    @Override
    protected boolean isMatch(String beanName, String mappedName) {
        return super.isMatch(beanName, mappedName) && !isExcluded(beanName);
    }

    private boolean isExcluded(String beanName) {
        if (excludeBeanNames != null) {
            for (String mappedName : this.excludeBeanNames) {
                if (PatternMatchUtils.simpleMatch(mappedName, beanName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
