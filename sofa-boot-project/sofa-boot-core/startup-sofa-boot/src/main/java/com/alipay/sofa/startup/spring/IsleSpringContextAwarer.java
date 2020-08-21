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
package com.alipay.sofa.startup.spring;

import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.startup.stage.StartupSpringContextInstallStage;

/**
 * Support class to aware present bean factory in isle model
 *
 * @author: Zhijie
 * @since: 2020/7/10
 */
public class IsleSpringContextAwarer extends SpringContextAwarer {
    private final StartupSpringContextInstallStage startupSpringContextInstallStage;

    public IsleSpringContextAwarer(StartupSpringContextInstallStage startupSpringContextInstallStage) {
        super();
        this.startupSpringContextInstallStage = startupSpringContextInstallStage;
    }

    @Override
    public long getIsleContextInstallCost() {
        return startupSpringContextInstallStage.getEndTime()
               - startupSpringContextInstallStage.getStartTime();
    }

    @Override
    public String getModuleName() {
        if (beanFactory instanceof BeanLoadCostBeanFactory) {
            return ((BeanLoadCostBeanFactory) beanFactory).getModuleName();
        }
        return super.getModuleName();
    }
}
