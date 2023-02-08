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
package com.alipay.sofa.rpc.boot.config;

import com.alipay.sofa.rpc.boot.common.SofaBootRpcParserUtil;
import com.alipay.sofa.rpc.client.aft.FaultToleranceConfig;
import com.alipay.sofa.rpc.client.aft.FaultToleranceConfigManager;

/**
 * 自动故障剔除初始化器
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class FaultToleranceConfigurator {

    private String appName;

    private String regulationEffectiveStr;

    private String degradeEffectiveStr;

    private String timeWindowStr;

    private String leastWindowCountStr;

    private String leastWindowExceptionRateMultipleStr;

    private String weightDegradeRateStr;

    private String weightRecoverRateStr;

    private String degradeLeastWeightStr;

    private String degradeMaxIpCountStr;

    /**
     * 解析并生效自动故障剔除配置参数
     */
    public void startFaultTolerance() {
        Boolean regulationEffective = SofaBootRpcParserUtil.parseBoolean(regulationEffectiveStr);
        Boolean degradeEffective = SofaBootRpcParserUtil.parseBoolean(degradeEffectiveStr);
        Long timeWindow = SofaBootRpcParserUtil.parseLong(timeWindowStr);
        Long leastWindowCount = SofaBootRpcParserUtil.parseLong(leastWindowCountStr);
        Double leastWindowExceptionRateMultiple = SofaBootRpcParserUtil
            .parseDouble(leastWindowExceptionRateMultipleStr);
        Double weightDegradeRate = SofaBootRpcParserUtil.parseDouble(weightDegradeRateStr);
        Double weightRecoverRate = SofaBootRpcParserUtil.parseDouble(weightRecoverRateStr);
        Integer degradeLeastWeight = SofaBootRpcParserUtil.parseInteger(degradeLeastWeightStr);
        Integer degradeMaxIpCount = SofaBootRpcParserUtil.parseInteger(degradeMaxIpCountStr);

        FaultToleranceConfig faultToleranceConfig = new FaultToleranceConfig();
        if (regulationEffective != null) {
            faultToleranceConfig.setRegulationEffective(regulationEffective);
        }
        if (degradeEffective != null) {
            faultToleranceConfig.setDegradeEffective(degradeEffective);
        }
        if (timeWindow != null) {
            faultToleranceConfig.setTimeWindow(timeWindow);
        }
        if (leastWindowCount != null) {
            faultToleranceConfig.setLeastWindowCount(leastWindowCount);
        }
        if (leastWindowExceptionRateMultiple != null) {
            faultToleranceConfig
                .setLeastWindowExceptionRateMultiple(leastWindowExceptionRateMultiple);
        }
        if (weightDegradeRate != null) {
            faultToleranceConfig.setWeightDegradeRate(weightDegradeRate);
        }
        if (weightRecoverRate != null) {
            faultToleranceConfig.setWeightRecoverRate(weightRecoverRate);
        }
        if (degradeLeastWeight != null) {
            faultToleranceConfig.setDegradeLeastWeight(degradeLeastWeight);
        }
        if (degradeMaxIpCount != null) {
            faultToleranceConfig.setDegradeMaxIpCount(degradeMaxIpCount);
        }

        FaultToleranceConfigManager.putAppConfig(appName, faultToleranceConfig);
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setRegulationEffectiveStr(String regulationEffectiveStr) {
        this.regulationEffectiveStr = regulationEffectiveStr;
    }

    public void setDegradeEffectiveStr(String degradeEffectiveStr) {
        this.degradeEffectiveStr = degradeEffectiveStr;
    }

    public void setTimeWindowStr(String timeWindowStr) {
        this.timeWindowStr = timeWindowStr;
    }

    public void setLeastWindowCountStr(String leastWindowCountStr) {
        this.leastWindowCountStr = leastWindowCountStr;
    }

    public void setLeastWindowExceptionRateMultipleStr(String leastWindowExceptionRateMultipleStr) {
        this.leastWindowExceptionRateMultipleStr = leastWindowExceptionRateMultipleStr;
    }

    public void setWeightDegradeRateStr(String weightDegradeRateStr) {
        this.weightDegradeRateStr = weightDegradeRateStr;
    }

    public void setWeightRecoverRateStr(String weightRecoverRateStr) {
        this.weightRecoverRateStr = weightRecoverRateStr;
    }

    public void setDegradeLeastWeightStr(String degradeLeastWeightStr) {
        this.degradeLeastWeightStr = degradeLeastWeightStr;
    }

    public void setDegradeMaxIpCountStr(String degradeMaxIpCountStr) {
        this.degradeMaxIpCountStr = degradeMaxIpCountStr;
    }

}
