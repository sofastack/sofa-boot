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

import java.util.List;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/23
 */
public class ModuleStat {
    private long           moduleStartTime;
    private long           moduleEndTime;
    private long           elapsedTime;
    private String         moduleName;
    private String         threadName;

    private List<BeanStat> beanStats;

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public long getModuleStartTime() {
        return moduleStartTime;
    }

    public void setModuleStartTime(long moduleStartTime) {
        this.moduleStartTime = moduleStartTime;
    }

    public long getModuleEndTime() {
        return moduleEndTime;
    }

    public void setModuleEndTime(long moduleEndTime) {
        this.moduleEndTime = moduleEndTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public List<BeanStat> getBeanStats() {
        return beanStats;
    }

    public void setBeanStats(List<BeanStat> beanStats) {
        this.beanStats = beanStats;
    }
}
