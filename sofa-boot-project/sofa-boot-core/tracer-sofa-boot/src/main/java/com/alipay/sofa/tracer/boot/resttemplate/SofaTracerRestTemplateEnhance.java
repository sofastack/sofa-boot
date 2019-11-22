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
package com.alipay.sofa.tracer.boot.resttemplate;

import com.alipay.common.tracer.core.tracer.AbstractTracer;
import com.sofa.alipay.tracer.plugins.rest.SofaTracerRestTemplateBuilder;
import com.sofa.alipay.tracer.plugins.rest.interceptor.RestTemplateInterceptor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/9/12 12:02 AM
 * @since:
 **/
public class SofaTracerRestTemplateEnhance {

    private final RestTemplateInterceptor restTemplateInterceptor;

    public SofaTracerRestTemplateEnhance() {
        AbstractTracer restTemplateTracer = SofaTracerRestTemplateBuilder.getRestTemplateTracer();
        this.restTemplateInterceptor = new RestTemplateInterceptor(restTemplateTracer);
    }

    public void enhanceRestTemplateWithSofaTracer(RestTemplate restTemplate) {
        // check interceptor
        if (checkRestTemplateInterceptor(restTemplate)) {
            return;
        }
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>(
            restTemplate.getInterceptors());
        interceptors.add(0, this.restTemplateInterceptor);
        restTemplate.setInterceptors(interceptors);
    }

    private boolean checkRestTemplateInterceptor(RestTemplate restTemplate) {
        for (ClientHttpRequestInterceptor interceptor : restTemplate.getInterceptors()) {
            if (interceptor instanceof RestTemplateInterceptor) {
                return true;
            }
        }
        return false;
    }
}
