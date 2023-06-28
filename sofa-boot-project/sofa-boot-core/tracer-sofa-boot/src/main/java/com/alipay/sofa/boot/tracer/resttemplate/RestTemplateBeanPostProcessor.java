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
package com.alipay.sofa.boot.tracer.resttemplate;

import com.alipay.sofa.boot.context.processor.SingletonSofaPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link BeanPostProcessor} to enhance message hook {@link RestTemplate}.
 *
 * @author guolei.sgl (guolei.sgl@antfin.com) 2019/9/11 11:59 PM
 * @author huzijie
 **/
@SingletonSofaPostProcessor
public class RestTemplateBeanPostProcessor implements BeanPostProcessor {

    private final RestTemplateEnhance sofaTracerRestTemplateEnhance;

    public RestTemplateBeanPostProcessor(RestTemplateEnhance sofaTracerRestTemplateEnhance) {
        this.sofaTracerRestTemplateEnhance = sofaTracerRestTemplateEnhance;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        if (bean instanceof RestTemplate restTemplate) {
            sofaTracerRestTemplateEnhance.enhanceRestTemplateWithSofaTracer(restTemplate);
        }
        return bean;
    }
}
