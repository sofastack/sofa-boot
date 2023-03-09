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
package com.alipay.sofa.smoke.tests.boot;

import com.alipay.sofa.boot.autoconfigure.condition.ConditionalOnSwitch;
import com.alipay.sofa.smoke.tests.boot.SofaConditionOnSwitchTests.BeanTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.catchException;

/**
 * @author yuanxuan
 * @version : SofaConditionOnSwitchTests.java, v 0.1 2023年02月22日 10:09 yuanxuan Exp $
 */
@SpringBootTest(classes = { BootSofaBootApplication.class, BeanTestConfiguration.class }, properties = { "sofa.boot.scenes=function" })
public class SofaConditionOnSwitchTests {

    @Autowired
    private ApplicationContext context;

    @Test
    void beanSwitchOnScene() {
        assertThat(catchException(() -> {
            context.getBean(FunctionBean.class);
        })).hasMessage("No qualifying bean of type 'com.alipay.sofa.smoke.tests.boot.SofaConditionOnSwitchTests$FunctionBean' "
                + "available");
        assertThatNoException().isThrownBy(() -> {
            context.getBean(FunctionFeatureBean.class);
        });
    }

    @Test
    void beanSwitchOnSceneWithYml() {
        assertThat(catchException(() -> {
            context.getBean(FunctionFeature2Bean.class);
        })).hasMessage("No qualifying bean of type 'com.alipay.sofa.smoke.tests.boot.SofaConditionOnSwitchTests$FunctionFeature2Bean' "
                + "available");
        assertThatNoException().isThrownBy(() -> {
            context.getBean(FunctionFeature3Bean.class);
        });
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnSwitch
    static class BeanTestConfiguration {

        @Bean
        @ConditionalOnSwitch
        public FunctionBean functionBean() {
            return new FunctionBean();
        }

        @Bean
        @ConditionalOnSwitch
        public FunctionFeatureBean functionFeatureBean() {
            return new FunctionFeatureBean();
        }

        @Bean
        @ConditionalOnSwitch
        public FunctionFeature2Bean functionFeature2Bean() {
            return new FunctionFeature2Bean();
        }

        @Bean
        @ConditionalOnSwitch
        public FunctionFeature3Bean functionFeature3Bean() {
            return new FunctionFeature3Bean();
        }
    }

    static class FunctionBean {

    }

    static class FunctionFeatureBean {

    }

    static class FunctionFeature2Bean {

    }

    static class FunctionFeature3Bean {

    }
}
