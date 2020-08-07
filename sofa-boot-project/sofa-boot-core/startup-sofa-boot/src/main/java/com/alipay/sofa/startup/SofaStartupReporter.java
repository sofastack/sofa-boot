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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * Collect and report the costs
 *
 * @author: Zhijie
 * @since: 2020/7/8
 */
public class SofaStartupReporter {
    protected final SofaStartupContext sofaStartupContext;

    public SofaStartupReporter(SofaStartupContext sofaStartupContext) {
        this.sofaStartupContext = sofaStartupContext;
    }

    /**
     * Build the com.alipay.sofa.startup.SofaStartupReporter.SofaStartupCostModel
     * @return the time cost model
     */
    public SofaStartupCostModel report() {
        SofaStartupCostModel sofaStartupCostModel = new SofaStartupCostModel();
        sofaStartupCostModel.setTotalCost(sofaStartupContext.getAppStartupTime());

        Map<String, Long> baseCosts = new LinkedHashMap<>();
        baseCosts.put("web_server_init_start_up_cost", sofaStartupContext.getWebServerInitCost());
        baseCosts.put("isle_install_stage_start_up_cost", sofaStartupContext.getIsleInstallCost());
        baseCosts.put("component_start_up_total_cost", sofaStartupContext.getComponentCost());
        baseCosts.put("bean_init_start_up_total_cost", sofaStartupContext.getBeanInitCost());
        sofaStartupCostModel.setBaseCosts(baseCosts);

        Map<String, Map<String, Long>> detailCosts = new TreeMap<>();
        detailCosts.put("component_start_up_costs", sofaStartupContext.getComponentDetail());
        detailCosts.put("bean_init_start_up_costs", sofaStartupContext.getBeanInitDetail());
        sofaStartupCostModel.setDetailCosts(detailCosts);
        return sofaStartupCostModel;
    }

    public static class SofaStartupCostModel {
        private long                           totalCost;
        private Map<String, Long>              baseCosts;
        private Map<String, Map<String, Long>> detailCosts;

        public long getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(long totalCost) {
            this.totalCost = totalCost;
        }

        public Map<String, Long> getBaseCosts() {
            return baseCosts;
        }

        public void setBaseCosts(Map<String, Long> baseCosts) {
            this.baseCosts = baseCosts;
        }

        public Map<String, Map<String, Long>> getDetailCosts() {
            return detailCosts;
        }

        public void setDetailCosts(Map<String, Map<String, Long>> detailCosts) {
            this.detailCosts = detailCosts;
        }
    }
}
