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
package com.alipay.sofa.isle.spring.parser;

import com.alipay.sofa.infra.constants.CommonMiddlewareConstants;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.runtime.spring.parser.AsyncInitBeanDefinitionDecorator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.lang.reflect.Method;

/**
 * @author qilong.zql
 * @author ruoshan
 * @since 2.6.0
 */
public class AsyncInitBeanDefinitionDecoratorTest {

    private AsyncInitBeanDefinitionDecorator asyncInitBeanDefinitionDecorator = new AsyncInitBeanDefinitionDecorator();

    @Test
    public void testIsleModule() throws Exception {
        String moduleName = "testModule";
        BeanLoadCostBeanFactory beanFactory = new BeanLoadCostBeanFactory(10, moduleName);
        Assert.assertTrue(invokeIsBeanLoadCostBeanFactory(beanFactory.getClass()));
        Assert.assertEquals(moduleName, invokeGetModuleName(beanFactory));
    }

    @Test
    public void testNoIsleModule() throws Exception {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        Assert.assertFalse(invokeIsBeanLoadCostBeanFactory(beanFactory.getClass()));
        Assert.assertEquals(CommonMiddlewareConstants.ROOT_APPLICATION_CONTEXT,
            invokeGetModuleName(beanFactory));
    }

    private boolean invokeIsBeanLoadCostBeanFactory(Class factoryClass) throws Exception {
        Method method = asyncInitBeanDefinitionDecorator.getClass().getDeclaredMethod(
            "isBeanLoadCostBeanFactory", Class.class);
        method.setAccessible(true);
        return (Boolean) method.invoke(asyncInitBeanDefinitionDecorator, factoryClass);
    }

    private String invokeGetModuleName(Object factory) throws Exception {
        Method method = asyncInitBeanDefinitionDecorator.getClass().getDeclaredMethod(
            "getModuleNameFromBeanFactory", Object.class);
        method.setAccessible(true);
        return (String) method.invoke(asyncInitBeanDefinitionDecorator, factory);
    }
}
