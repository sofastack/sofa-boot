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
package com.alipay.sofa.startup.stage;

import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.loader.SpringContextLoader;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Wrapper for SpringContextInstallStage to calculate time cost by install spring context
 *
 * @author: Zhijie
 * @since: 2020/7/8
 */
public class StartupSpringContextInstallStage extends SpringContextInstallStage {
    private long startTime = -1L;
    private long endTime   = -1L;

    public StartupSpringContextInstallStage(AbstractApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void installSpringContext(ApplicationRuntimeModel application,
                                        SpringContextLoader springContextLoader) {
        startTime = System.currentTimeMillis();
        super.installSpringContext(application, springContextLoader);
        endTime = System.currentTimeMillis();
    }

    @Override
    public int getOrder() {
        return 20000;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}
