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
 * Common unit to record startup time cost
 *
 * @author Zhijie
 * @since 2020/7/7
 */
public class StageStat {
    private String stageName;
    private long   stageStartTime;
    private long   stageEndTime;
    private long   elapsedTime;

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public long getStageStartTime() {
        return stageStartTime;
    }

    public void setStageStartTime(long stageStartTime) {
        this.stageStartTime = stageStartTime;
    }

    public long getStageEndTime() {
        return stageEndTime;
    }

    public void setStageEndTime(long stageEndTime) {
        this.stageEndTime = stageEndTime;
        this.elapsedTime = this.stageEndTime - this.stageStartTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
