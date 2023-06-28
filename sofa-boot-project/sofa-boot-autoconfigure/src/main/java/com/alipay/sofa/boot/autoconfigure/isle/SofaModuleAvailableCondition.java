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
package com.alipay.sofa.boot.autoconfigure.isle;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Condition;

/**
 *  {@link Condition} that determine if isle is available.
 *
 * @author huzijie
 * @version SofaModuleAvailableCondition.java, v 0.1 2023年04月25日 11:39 AM huzijie Exp $
 */
public class SofaModuleAvailableCondition extends AllNestedConditions {

    public SofaModuleAvailableCondition() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnClass(ApplicationRuntimeModel.class)
    static class ApplicationRuntimeModelClassAvailable {

    }

    @ConditionalOnProperty(value = "sofa.boot.isle.enabled", havingValue = "true", matchIfMissing = true)
    static class IslePropertyAvailable {

    }
}
