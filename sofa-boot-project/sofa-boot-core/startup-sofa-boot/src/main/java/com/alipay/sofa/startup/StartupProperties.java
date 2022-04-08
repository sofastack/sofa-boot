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
package com.alipay.sofa.startup;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Zhijie
 * @since 2020/7/13
 */
@ConfigurationProperties(prefix = "com.alipay.sofa.boot.startup")
public class StartupProperties {
    private long    beanInitCostThreshold = 100;
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
