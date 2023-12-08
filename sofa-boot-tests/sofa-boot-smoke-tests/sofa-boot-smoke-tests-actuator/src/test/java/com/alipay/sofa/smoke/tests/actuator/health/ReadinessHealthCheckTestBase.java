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
package com.alipay.sofa.smoke.tests.actuator.health;

import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
import com.alipay.sofa.boot.context.processor.UnshareSofaPostProcessor;
import com.alipay.sofa.smoke.tests.actuator.ActuatorSofaBootApplication;
import com.alipay.sofa.smoke.tests.actuator.sample.readiness.SampleHealthChecker;
import com.alipay.sofa.smoke.tests.actuator.sample.readiness.SampleHealthIndicate;
import com.alipay.sofa.smoke.tests.actuator.sample.readiness.SampleReadinessCheckCallback;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base integration tests for multi components.
 *
 * @author huzijie
 * @version ReadinessHealthCheckTestBase.java, v 0.1 2023年11月24日 4:06 PM huzijie Exp $
 */
@SpringBootTest(classes = ActuatorSofaBootApplication.class)
@Import(ReadinessHealthCheckTestBase.ReadinessBeanDefinitionRegistryPostProcessor.class)
public class ReadinessHealthCheckTestBase {

    @Value("${bean.count}")
    private int                    beanCount;

    @Autowired
    private ReadinessCheckListener readinessCheckListener;

    @Test
    public void checkReadinessResult() {
        assertThat(readinessCheckListener.getReadinessState()).isEqualTo(
            ReadinessState.ACCEPTING_TRAFFIC);
        assertThat(readinessCheckListener.getHealthCheckerDetails().size()).isEqualTo(beanCount);
        assertThat(readinessCheckListener.getHealthIndicatorDetails().size()).isEqualTo(beanCount);
        assertThat(readinessCheckListener.getHealthCallbackDetails().size()).isEqualTo(beanCount);
    }

    @UnshareSofaPostProcessor
    static class ReadinessBeanDefinitionRegistryPostProcessor implements
                                                             BeanDefinitionRegistryPostProcessor,
                                                             EnvironmentAware {

        private int beanCount;

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
                                                                                      throws BeansException {
            if (registry.containsBeanDefinition("sofaModuleHealthChecker")) {
                registry.removeBeanDefinition("sofaModuleHealthChecker");
            }
            if (registry.containsBeanDefinition("sofaComponentHealthChecker")) {
                registry.removeBeanDefinition("sofaComponentHealthChecker");
            }
            if (registry.containsBeanDefinition("diskSpaceHealthIndicator")) {
                registry.removeBeanDefinition("diskSpaceHealthIndicator");
            }
            if (registry.containsBeanDefinition("pingHealthContributor")) {
                registry.removeBeanDefinition("pingHealthContributor");
            }
            if (registry.containsBeanDefinition("rpcAfterHealthCheckCallback")) {
                registry.removeBeanDefinition("rpcAfterHealthCheckCallback");
            }

            registerRandomBeanDefinitions(registry, SampleHealthChecker.class);
            registerRandomBeanDefinitions(registry, SampleHealthIndicate.class);
            registerRandomBeanDefinitions(registry, SampleReadinessCheckCallback.class);
        }

        private void registerRandomBeanDefinitions(BeanDefinitionRegistry registry, Class<?> clazz) {
            Set<Integer> numbers = new HashSet<>();
            Random random = new Random();

            while (numbers.size() < beanCount) {
                int number = random.nextInt(1000);
                numbers.add(number);
            }

            numbers.forEach(sleep -> register(registry, clazz, sleep));
        }

        private void register(BeanDefinitionRegistry registry, Class<?> clazz, long sleep) {
            GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
            beanDefinition.setBeanClass(clazz);
            ConstructorArgumentValues values = new ConstructorArgumentValues();
            values.addIndexedArgumentValue(0, sleep);
            beanDefinition.setConstructorArgumentValues(values);
            registry.registerBeanDefinition(clazz.getSimpleName() + sleep, beanDefinition);
        }

        @Override
        public void setEnvironment(Environment environment) {
            this.beanCount = Integer.parseInt(Objects.requireNonNull(environment
                .getProperty("bean.count")));
        }
    }

}
