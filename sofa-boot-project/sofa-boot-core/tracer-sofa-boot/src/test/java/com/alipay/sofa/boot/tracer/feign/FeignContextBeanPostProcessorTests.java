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
package com.alipay.sofa.boot.tracer.feign;

import com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignClientFactory;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignClientFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FeignContextBeanPostProcessor}.
 *
 * @author huzijie
 * @version FeignContextBeanPostProcessorTests.java, v 0.1 2023年01月09日 7:21 PM huzijie Exp $
 */
public class FeignContextBeanPostProcessorTests {

    private final FeignContextBeanPostProcessor feignContextBeanPostProcessor = new FeignContextBeanPostProcessor();

    @Test
    public void wrapFeignClientFactory() {
        FeignClientFactory feignClientFactory = new FeignClientFactory();
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(
            feignClientFactory, "feignClientFactory");
        assertThat(bean).isNotEqualTo(feignClientFactory);
        assertThat(bean).isInstanceOf(SofaTracerFeignClientFactory.class);
    }

    @Test
    public void skipNotFeignClientFactory() {
        Object object = new Object();
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(object,
            "feignContext");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerFeignClientFactory.class);
    }

    @Test
    public void skipTransformedFeignClientFactory() {
        FeignClientFactory feignContext = new FeignClientFactory();
        SofaTracerFeignClientFactory sofaTracerFeignContext = new SofaTracerFeignClientFactory(
            feignContext);
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(
            sofaTracerFeignContext, "feignContext");
        assertThat(bean).isEqualTo(sofaTracerFeignContext);
    }
}
