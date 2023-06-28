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

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.api.ArkConfigs;
import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.boot.util.SofaBootEnvUtils;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Condition} that checks if running in ark master biz environment.
 *
 * @author caojie.cj@antfin.com
 * @since 2019/10/29
 */
public class OnMasterBizCondition extends SpringBootCondition {

    private static Object masterBiz;

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 非 Ark 环境
        if (!SofaBootEnvUtils.isArkEnv()) {
            return new ConditionOutcome(true, "SOFAArk has not started.");
        }

        if (masterBiz == null) {
            String masterBizName = ArkConfigs.getStringValue(Constants.MASTER_BIZ);
            List<Biz> biz = ArkClient.getBizManagerService().getBiz(masterBizName);
            Assert.isTrue(biz.size() == 1, "master biz should have and only have one.");
            masterBiz = biz.get(0);
        }

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        // is master biz, return directly
        if (contextClassLoader.equals(((Biz) masterBiz).getBizClassLoader())) {
            return new ConditionOutcome(true,
                "Current context classloader equals master biz classloader.");
        }

        // if not master biz, should be determine by extensionCondition
        List<AnnotationAttributes> allAnnotationAttributes = annotationAttributesFromMultiValueMap(metadata
            .getAllAnnotationAttributes(ConditionalOnMasterBiz.class.getName()));
        for (AnnotationAttributes annotationAttributes : allAnnotationAttributes) {
            Spec spec = new Spec(annotationAttributes);
            String extensionCondition = spec.getExtensionCondition();
            String property = context.getEnvironment().getProperty(extensionCondition);
            if ("true".equalsIgnoreCase(property)) {
                return new ConditionOutcome(true,
                    "Current context classloader not equals master biz classloader, but allow by extension condition.");
            }
        }

        return new ConditionOutcome(false,
            "Current context classloader not equals master biz classloader.");
    }

    private List<AnnotationAttributes> annotationAttributesFromMultiValueMap(
            MultiValueMap<String, Object> multiValueMap) {
        List<Map<String, Object>> maps = new ArrayList<>();
        multiValueMap.forEach((key, value) -> {
            for (int i = 0; i < value.size(); i++) {
                Map<String, Object> map;
                if (i < maps.size()) {
                    map = maps.get(i);
                }
                else {
                    map = new HashMap<>();
                    maps.add(map);
                }
                map.put(key, value.get(i));
            }
        });
        List<AnnotationAttributes> annotationAttributes = new ArrayList<>(maps.size());
        for (Map<String, Object> map : maps) {
            annotationAttributes.add(AnnotationAttributes.fromMap(map));
        }
        return annotationAttributes;
    }

    private static class Spec {
        private final String extensionCondition;

        Spec(AnnotationAttributes annotationAttributes) {
            this.extensionCondition = annotationAttributes.getString("extensionCondition");
        }

        public String getExtensionCondition() {
            return extensionCondition;
        }
    }
}
