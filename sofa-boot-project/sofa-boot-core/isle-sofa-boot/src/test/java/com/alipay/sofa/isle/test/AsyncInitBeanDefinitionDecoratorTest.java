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
package com.alipay.sofa.isle.test;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.runtime.spring.parser.AsyncInitBeanDefinitionDecorator;

/**
 * @author qilong.zql
 * @author ruoshan
 * @since 2.6.0
 */
public class AsyncInitBeanDefinitionDecoratorTest {

    @Test
    public void testIsleModule() {
        String moduleName = "testModule";
        BeanLoadCostBeanFactory beanFactory = new BeanLoadCostBeanFactory(10, moduleName);
        Assert.assertTrue(AsyncInitBeanDefinitionDecorator.isBeanLoadCostBeanFactory(beanFactory
            .getClass()));
        Assert.assertEquals(moduleName,
            AsyncInitBeanDefinitionDecorator.getModuleNameFromBeanFactory(beanFactory));
    }

    @Test
    public void testNoIsleModule() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        Assert.assertFalse(AsyncInitBeanDefinitionDecorator.isBeanLoadCostBeanFactory(beanFactory
            .getClass()));
        Assert.assertEquals(SofaBootConstants.ROOT_APPLICATION_CONTEXT,
            AsyncInitBeanDefinitionDecorator.getModuleNameFromBeanFactory(beanFactory));
    }

}
