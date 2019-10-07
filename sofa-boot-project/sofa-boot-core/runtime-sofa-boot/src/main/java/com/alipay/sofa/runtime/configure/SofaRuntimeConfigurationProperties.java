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
package com.alipay.sofa.runtime.configure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.runtime.SofaRuntimeProperties;

/**
 * @author xuanbei 18/5/9
 */
@ConfigurationProperties(SofaBootConstants.PREFIX)
public class SofaRuntimeConfigurationProperties {

    public void setSkipJvmReferenceHealthCheck(boolean skipJvmReferenceHealthCheck) {
        SofaRuntimeProperties.setSkipJvmReferenceHealthCheck(this.getClass().getClassLoader(),
            skipJvmReferenceHealthCheck);
    }

    public void setDisableJvmFirst(boolean disableJvmFirst) {
        SofaRuntimeProperties.setDisableJvmFirst(this.getClass().getClassLoader(), disableJvmFirst);
    }

    public boolean isSkipJvmReferenceHealthCheck() {
        return SofaRuntimeProperties
            .isSkipJvmReferenceHealthCheck(this.getClass().getClassLoader());
    }

    public boolean isDisableJvmFirst() {
        return SofaRuntimeProperties.isDisableJvmFirst(this.getClass().getClassLoader());
    }

    public void setSkipJvmSerialize(boolean skipJvmSerialize) {
        SofaRuntimeProperties.setSkipJvmSerialize(this.getClass().getClassLoader(),
            skipJvmSerialize);
    }

    public boolean isSkipJvmSerialize() {
        return SofaRuntimeProperties.isSkipJvmSerialize(this.getClass().getClassLoader());
    }
}
