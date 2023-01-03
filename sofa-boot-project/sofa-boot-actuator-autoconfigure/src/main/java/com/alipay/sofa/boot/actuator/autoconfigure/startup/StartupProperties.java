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
package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to configure startup.
 *
 * @author Zhijie
 * @since 2020/7/13
 */
@ConfigurationProperties(prefix = "sofa.boot.actuator.startup")
public class StartupProperties {

    /**
     * bean 耗时统计阈值，单位 ms
     */
    private long    beanInitCostThreshold = 100;

    /**
     * 是否跳过 sofa 包名的 bean
     */
    private boolean skipSofaBean          = false;

    public long getBeanInitCostThreshold() {
        return beanInitCostThreshold;
    }

    public void setBeanInitCostThreshold(long beanInitCostThreshold) {
        this.beanInitCostThreshold = beanInitCostThreshold;
    }

    public boolean isSkipSofaBean() {
        return skipSofaBean;
    }

    public void setSkipSofaBean(boolean skipSofaBean) {
        this.skipSofaBean = skipSofaBean;
    }
}
