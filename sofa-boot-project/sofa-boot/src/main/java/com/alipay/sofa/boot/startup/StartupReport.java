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
package com.alipay.sofa.boot.startup;

import java.util.ArrayList;
import java.util.List;

/**
 * Startup optimization analysis report.
 *
 * @author OpenAI
 */
public class StartupReport {

    private List<BeanInitInfo>          sequentialBeans = new ArrayList<>();

    private List<BeanInitInfo>          slowestBeans    = new ArrayList<>();

    private List<StartupRecommendation> recommendations = new ArrayList<>();

    public List<BeanInitInfo> getSequentialBeans() {
        return sequentialBeans;
    }

    public void setSequentialBeans(List<BeanInitInfo> sequentialBeans) {
        this.sequentialBeans = sequentialBeans;
    }

    public List<BeanInitInfo> getSlowestBeans() {
        return slowestBeans;
    }

    public void setSlowestBeans(List<BeanInitInfo> slowestBeans) {
        this.slowestBeans = slowestBeans;
    }

    public List<StartupRecommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<StartupRecommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
