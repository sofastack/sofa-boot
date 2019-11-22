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
package com.alipay.sofa.boot.autoconfigure.tracer;

import com.alipay.sofa.tracer.boot.flexible.aop.SofaTracerAdvisingBeanPostProcessor;
import com.alipay.sofa.tracer.boot.flexible.processor.MethodInvocationProcessor;
import com.alipay.sofa.tracer.boot.flexible.processor.SofaTracerIntroductionInterceptor;
import com.alipay.sofa.tracer.boot.flexible.processor.SofaTracerMethodInvocationProcessor;
import io.opentracing.Tracer;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/9 3:20 PM
 * @since:
 **/
@Configuration
@ConditionalOnProperty(prefix = "com.alipay.sofa.tracer.flexible", value = "enable", matchIfMissing = true)
@AutoConfigureAfter(SofaTracerAutoConfiguration.class)
@ConditionalOnBean(Tracer.class)
@ConditionalOnClass(SofaTracerIntroductionInterceptor.class)
public class TracerAnnotationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    MethodInvocationProcessor sofaMethodInvocationProcessor(Tracer tracer) {
        return new SofaTracerMethodInvocationProcessor(tracer);
    }

    @Bean
    @ConditionalOnMissingBean
    SofaTracerIntroductionInterceptor sofaTracerIntroductionInterceptor(MethodInvocationProcessor methodInvocationProcessor) {
        return new SofaTracerIntroductionInterceptor(methodInvocationProcessor);
    }

    @Bean
    @ConditionalOnMissingBean
    SofaTracerAdvisingBeanPostProcessor tracerAnnotationBeanPostProcessor(SofaTracerIntroductionInterceptor methodInterceptor) {
        return new SofaTracerAdvisingBeanPostProcessor(methodInterceptor);
    }

}
