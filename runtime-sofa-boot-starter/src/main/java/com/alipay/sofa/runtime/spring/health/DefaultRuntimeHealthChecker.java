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
package com.alipay.sofa.runtime.spring.health;

import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.health.RuntimeHealthChecker;
import org.springframework.beans.BeansException;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class DefaultRuntimeHealthChecker implements RuntimeHealthChecker, ApplicationContextAware {

    private Map<String, HealthIndicator> indicatorMap;

    private ApplicationContext           cxt;

    public DefaultRuntimeHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
        sofaRuntimeContext.getSofaRuntimeManager().registerRuntimeHealthChecker(this);
    }

    @Override
    public boolean isHealth() {
        if (indicatorMap == null) {
            indicatorMap = cxt.getBeansOfType(HealthIndicator.class);
        }

        for (HealthIndicator healthIndicator : indicatorMap.values()) {
            if (healthIndicator instanceof MultiApplicationHealthChecker) {
                continue;
            }

            if (healthIndicator.health().getStatus().equals(Status.DOWN)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        cxt = applicationContext;
    }
}