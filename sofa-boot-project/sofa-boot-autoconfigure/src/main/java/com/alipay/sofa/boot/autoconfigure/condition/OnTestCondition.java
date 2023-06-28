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
package com.alipay.sofa.boot.autoconfigure.condition;

import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * {@link Condition} that checks if running in test environment.
 *
 * @author huzijie
 * @version OnTestCondition.java, v 0.1 2022年10月17日 2:54 PM huzijie Exp $
 */
public class OnTestCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage matchMessage = ConditionMessage.empty();
        if (metadata.isAnnotated(ConditionalOnNotTest.class.getName())) {
            if (SofaBootEnvUtils.isSpringTestEnv()) {
                return ConditionOutcome.noMatch(ConditionMessage.forCondition(
                    ConditionalOnNotTest.class).notAvailable("spring test environment"));
            }
            matchMessage = matchMessage.andCondition(ConditionalOnNotTest.class).available(
                "none spring test environment");
        }
        return ConditionOutcome.match(matchMessage);
    }
}
