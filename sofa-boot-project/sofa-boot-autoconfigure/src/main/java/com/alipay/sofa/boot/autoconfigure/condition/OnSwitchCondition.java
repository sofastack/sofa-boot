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

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.util.StringUtils;

/**
 * {@link Condition} that checks if switch properties are defined in environment.
 *
 * @author yuanxuan
 * @version : OnSwitchCondition.java, v 0.1 2023年02月09日 09:53 yuanxuan Exp $
 */
public class OnSwitchCondition extends SpringBootCondition {

    private static final String CONFIG_KEY_PREFIX = "sofa.boot.switch.bean";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return getMatchOutcome(context.getEnvironment(), metadata);
    }

    private ConditionOutcome getMatchOutcome(Environment environment, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition(ConditionalOnSwitch.class);
        String key = getKey(metadata);
        Boolean userDefinedEnabled = environment.getProperty(key, Boolean.class);
        if (userDefinedEnabled != null) {
            return new ConditionOutcome(userDefinedEnabled, message.because("found property " + key
                                                                            + " with value "
                                                                            + userDefinedEnabled));
        }

        MergedAnnotation<ConditionalOnSwitch> conditionAnnotation = metadata.getAnnotations().get(
            ConditionalOnSwitch.class);
        Boolean matchIfMissing = conditionAnnotation.getBoolean("matchIfMissing");
        return new ConditionOutcome(matchIfMissing, message.because("matchIfMissing " + key
                                                                    + " with value "
                                                                    + matchIfMissing));
    }

    private String getKey(AnnotatedTypeMetadata metadata) {
        MergedAnnotation<ConditionalOnSwitch> conditionAnnotation = metadata.getAnnotations().get(
            ConditionalOnSwitch.class);
        String key = conditionAnnotation.getString("value");
        if (StringUtils.hasText(key)) {
            return CONFIG_KEY_PREFIX.concat(".").concat(key).concat(".enabled");
        } else {
            return CONFIG_KEY_PREFIX.concat(".").concat(getClassOrMethodName(metadata))
                .concat(".enabled");
        }
    }

    private String getClassOrMethodName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof ClassMetadata classMetadata) {
            return classMetadata.getClassName();
        }
        MethodMetadata methodMetadata = (MethodMetadata) metadata;
        return methodMetadata.getMethodName();
    }
}
