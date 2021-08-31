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
package com.alipay.sofa.isle;

import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/10/26
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = "spring.application.name=IsleSpringComponentTest")
public class IsleSpringComponentTest implements SofaRuntimeContextAware {
    private SofaRuntimeContext sofaRuntimeContext;

    @Test
    public void test() {
        ComponentManager componentManager = sofaRuntimeContext.getComponentManager();
        ComponentName componentName1 = ComponentNameFactory.createComponentName(
            SpringContextComponent.SPRING_COMPONENT_TYPE, "com.alipay.sofa.isle.module1");
        ComponentName componentName2 = ComponentNameFactory.createComponentName(
            SpringContextComponent.SPRING_COMPONENT_TYPE, "com.alipay.sofa.isle.module2");
        Assert.assertNotNull(componentManager.getComponentInfo(componentName1));
        Assert.assertNotNull(componentManager.getComponentInfo(componentName2));
    }

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class IsleSpringComponentTestConfiguration {
        @Bean
        @ConditionalOnMissingBean
        public ModelCreatingStage modelCreatingStage(ApplicationContext applicationContext) {
            return new TestModelCreatingStage((AbstractApplicationContext) applicationContext,
                "module1", "module2");
        }
    }
}
