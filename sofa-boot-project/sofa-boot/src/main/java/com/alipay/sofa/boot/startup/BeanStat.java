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

/**
 * Stat model to record bean init.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/11/23
 */
public class BeanStat extends ChildrenStat<BeanStat> {

    private String type;

    private long   realRefreshElapsedTime;

    @Deprecated
    private String beanClassName;

    @Deprecated
    private long   beanRefreshStartTime;

    @Deprecated
    private long   beanRefreshEndTime;

    @Deprecated
    private long   refreshElapsedTime;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getRealRefreshElapsedTime() {
        return realRefreshElapsedTime;
    }

    public void setRealRefreshElapsedTime(long realRefreshElapsedTime) {
        this.realRefreshElapsedTime = realRefreshElapsedTime;
    }

    @Deprecated
    public String getBeanClassName() {
        return beanClassName;
    }

    @Deprecated
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    @Deprecated
    public long getRefreshElapsedTime() {
        return refreshElapsedTime;
    }

    @Deprecated
    public void setRefreshElapsedTime(long refreshElapsedTime) {
        this.refreshElapsedTime = refreshElapsedTime;
    }

    @Deprecated
    public long getBeanRefreshStartTime() {
        return beanRefreshStartTime;
    }

    @Deprecated
    public void setBeanRefreshStartTime(long beanRefreshStartTime) {
        this.beanRefreshStartTime = beanRefreshStartTime;
    }

    @Deprecated
    public long getBeanRefreshEndTime() {
        return beanRefreshEndTime;
    }

    @Deprecated
    public void setBeanRefreshEndTime(long beanRefreshEndTime) {
        this.beanRefreshEndTime = beanRefreshEndTime;
    }
}
