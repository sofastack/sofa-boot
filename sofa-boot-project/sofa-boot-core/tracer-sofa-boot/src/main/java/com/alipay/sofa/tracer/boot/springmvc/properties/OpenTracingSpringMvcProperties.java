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
package com.alipay.sofa.tracer.boot.springmvc.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

import java.util.ArrayList;
import java.util.List;

/**
 * OpenTracingSpringMvcProperties
 *
 * @author yangguanchao
 * @since 2018/04/30
 */
@ConfigurationProperties("com.alipay.sofa.tracer.springmvc")
public class OpenTracingSpringMvcProperties {

    /***
     * com.alipay.sofa.tracer.springmvc.filter-order
     */
    private int          filterOrder = Ordered.HIGHEST_PRECEDENCE + 1;

    /**
     * Comma-separated list of urlPatterns to create : com.alipay.sofa.tracer.springmvc.url-patterns=/**,
     */
    private List<String> urlPatterns = new ArrayList<String>();

    /**
     * json output : com.alipay.sofa.tracer.springmvc.jsonOutput=true,
     */
    private boolean      jsonOutput  = true;

    public int getFilterOrder() {
        return filterOrder;
    }

    public void setFilterOrder(int filterOrder) {
        this.filterOrder = filterOrder;
    }

    public List<String> getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(List<String> urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public boolean isJsonOutput() {
        return jsonOutput;
    }

    public void setJsonOutput(boolean jsonOutput) {
        this.jsonOutput = jsonOutput;
    }
}