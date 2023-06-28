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
@ConfigurationProperties("sofa.boot.startup")
public class StartupProperties {

    /**
     * Startup record threshold in milliseconds.
     */
    private long costThreshold = 100;

    /**
     * Startup report buffer size.
     */
    private int  bufferSize    = 4096;

    public long getCostThreshold() {
        return costThreshold;
    }

    public void setCostThreshold(long costThreshold) {
        this.costThreshold = costThreshold;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }
}
