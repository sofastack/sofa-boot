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
package com.alipay.sofa.runtime.integration.postprocessor;

import com.alipay.sofa.runtime.api.event.ApplicationShutdownCallback;
import com.alipay.sofa.runtime.integration.bootstrap.SofaBootTestApplication;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
public class BeanPostProcessorOrderTest {

    @Autowired
    private SofaRuntimeContext           sofaRuntimeContext;

    @Autowired
    private HigherOrderBeanPostProcessor higherOrderBeanPostProcessor;

    @Autowired
    private BeanPostProcessorTestBean    beanPostProcessorTestBean;

    @Test
    public void testClientFactoryInject() {
        Assert.assertNotNull(higherOrderBeanPostProcessor.getClientFactory());
    }

    @Test
    public void testSofaRuntimeContextInject() {
        Assert.assertNotNull(higherOrderBeanPostProcessor.getSofaRuntimeContext());
    }

    @Test
    public void testShutDownHook() throws Exception {
        List<ApplicationShutdownCallback> applicationShutdownCallbacks = getApplicationShutdownCallbacks();
        Assert.assertNotNull(applicationShutdownCallbacks);
        Assert.assertTrue(applicationShutdownCallbacks.contains(higherOrderBeanPostProcessor));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testShutDownHookReordered() throws Exception {
        List<ApplicationShutdownCallback> applicationShutdownCallbacks = getApplicationShutdownCallbacks();
        BeanPostProcessorTestBean testBeanCallBack = null;
        for (ApplicationShutdownCallback applicationShutdownCallback : applicationShutdownCallbacks) {
            if (BeanPostProcessorTestBean.class.getName().equals(
                applicationShutdownCallback.getClass().getName())) {
                testBeanCallBack = (BeanPostProcessorTestBean) applicationShutdownCallback;
            }
        }
        Assert.assertNotNull(testBeanCallBack);
        Assert.assertEquals(testBeanCallBack, beanPostProcessorTestBean);
        Assert.assertTrue(testBeanCallBack.isEnhancedByLowOrderPostProcessor());
    }

    @Test
    public void testSofaReference() throws Exception {
        Assert.assertNotNull(higherOrderBeanPostProcessor.getSampleService());
    }

    @SuppressWarnings("unchecked")
    private List<ApplicationShutdownCallback> getApplicationShutdownCallbacks() throws Exception {
        SofaRuntimeManager sofaRuntimeManager = sofaRuntimeContext.getSofaRuntimeManager();
        Field applicationShutdownCallbacksField = sofaRuntimeManager.getClass().getDeclaredField(
            "applicationShutdownCallbacks");
        applicationShutdownCallbacksField.setAccessible(true);
        return (List<ApplicationShutdownCallback>) applicationShutdownCallbacksField
            .get(sofaRuntimeManager);
    }
}