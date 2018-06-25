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
package com.alipay.sofa.runtime.ark;

import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.event.BizEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.healthcheck.initializer.HealthcheckInitializer;
import com.alipay.sofa.runtime.SofaFramework;
import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.integration.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.integration.service.SofaEventHandler;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spring.configuration.SofaRuntimeAutoConfiguration;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaEventHandlerTest {

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    @Mocked
    private Biz                                      biz;

    @Before
    public void before() {
        new NonStrictExpectations() {
            {
                biz.getBizClassLoader();
                result = this.getClass().getClassLoader();

                biz.getIdentity();
                result = "MockName:MockVersion";

            }
        };

        new MockUp<DynamicJvmServiceProxyFinder>() {
            @Mock
            public Biz getBiz(SofaRuntimeManager sofaRuntimeManager) {
                return biz;
            }
        };

        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.disableJvmFirst=true");
        EnvironmentTestUtils.addEnvironment(this.applicationContext,
            "com.alipay.sofa.boot.skipJvmReferenceHealthCheck=true");
        this.applicationContext.register(SofaRuntimeAutoConfiguration.class,
            HealthcheckInitializer.class);
        this.applicationContext.refresh();
    }

    @Test
    public void testUninstallEvent() {
        Assert.assertTrue(SofaRuntimeProperties.isDisableJvmFirst(applicationContext
            .getClassLoader()));
        Assert.assertTrue(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        Assert.assertFalse(SofaFramework.getRuntimeSet().isEmpty());
        Assert.assertTrue(applicationContext.isActive());

        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz, Constants.BIZ_EVENT_TOPIC_UNINSTALL));

        Assert.assertFalse(SofaRuntimeProperties.isDisableJvmFirst(applicationContext
            .getClassLoader()));
        Assert.assertFalse(SofaRuntimeProperties.isSkipJvmReferenceHealthCheck(applicationContext
            .getClassLoader()));
        Assert.assertTrue(SofaFramework.getRuntimeSet().isEmpty());

        Assert.assertFalse(applicationContext.isActive());
    }

    @Test
    public void testHealthCheck() {
        SofaEventHandler sofaEventHandler = new SofaEventHandler();
        sofaEventHandler.handleEvent(new BizEvent(biz, Constants.BIZ_EVENT_TOPIC_HEALTH_CHECK));
    }

    @After
    public void closeContext() {
        this.applicationContext.close();
    }
}