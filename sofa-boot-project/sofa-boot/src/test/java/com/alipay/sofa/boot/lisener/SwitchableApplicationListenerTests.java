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
package com.alipay.sofa.boot.lisener;

import com.alipay.sofa.boot.listener.SwitchableApplicationListener;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.event.ContextRefreshedEvent;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SwitchableApplicationListener}.
 *
 * @author huzijie
 * @version SwitchableApplicationListenerTests.java, v 0.1 2023年03月02日 7:33 PM huzijie Exp $
 */
public class SwitchableApplicationListenerTests {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
                                                             .withBean(SampleBean.class)
                                                             .withBean(
                                                                 SampleSwitchSpringContextInitializer.class);

    @Test
    void enableFalse() {
        contextRunner.withPropertyValues("sofa.boot.switch.listener.sampleswitchabletest.enabled=false")
                .run(context -> assertThat(context.getBean(SampleBean.class).isTrigger()).isFalse());
    }

    @Test
    void enableTrue() {
        contextRunner.withPropertyValues("sofa.boot.switch.listener.sampleswitchabletest.enabled=true")
                .run(context -> assertThat(context.getBean(SampleBean.class).isTrigger()).isTrue());
    }

    @Test
    void enableDefault() {
        contextRunner.run(context -> assertThat(context.getBean(SampleBean.class).isTrigger()).isTrue());
    }

    static class SampleBean {

        private boolean trigger;

        public boolean isTrigger() {
            return trigger;
        }

        public void setTrigger(boolean trigger) {
            this.trigger = trigger;
        }

    }

    static class SampleSwitchSpringContextInitializer
                                                     extends
                                                     SwitchableApplicationListener<ContextRefreshedEvent> {

        @Override
        protected void doOnApplicationEvent(ContextRefreshedEvent event) {
            SampleBean sampleBean = event.getApplicationContext().getBean(SampleBean.class);
            sampleBean.setTrigger(true);
        }

        @Override
        protected String switchKey() {
            return "sampleswitchabletest";
        }

    }
}
