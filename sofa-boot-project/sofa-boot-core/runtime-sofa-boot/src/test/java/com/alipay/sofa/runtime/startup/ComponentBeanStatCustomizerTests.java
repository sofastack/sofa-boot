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
package com.alipay.sofa.runtime.startup;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;
import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ComponentBeanStatCustomizer}.
 *
 * @author huzijie
 * @version ComponentBeanStatCustomizerTests.java, v 0.1 2023年02月22日 12:17 PM huzijie Exp $
 */
public class ComponentBeanStatCustomizerTests {

    private final ComponentBeanStatCustomizer componentBeanStatCustomizer = new ComponentBeanStatCustomizer();

    @Test
    public void updateServiceBeanStats() {
        BeanStat beanStat = new BeanStat();
        ServiceFactoryBean serviceFactoryBean = new ServiceFactoryBean();
        serviceFactoryBean.setInterfaceType("testService");
        BeanStat result = componentBeanStatCustomizer.customize("service", serviceFactoryBean,
            beanStat);
        assertThat(result).isNull();
        assertThat(beanStat.getAttribute("interface")).isEqualTo("testService");
    }

    @Test
    public void updateReferenceBeanStats() {
        BeanStat beanStat = new BeanStat();
        ReferenceFactoryBean referenceFactoryBean = new ReferenceFactoryBean();
        referenceFactoryBean.setInterfaceType("testService");
        BeanStat result = componentBeanStatCustomizer.customize("reference", referenceFactoryBean,
            beanStat);
        assertThat(result).isNull();
        assertThat(beanStat.getAttribute("interface")).isEqualTo("testService");
    }

    @Test
    public void updateExtensionBeanStats() {
        BeanStat beanStat = new BeanStat();
        ExtensionFactoryBean extensionFactoryBean = new ExtensionFactoryBean();
        extensionFactoryBean.setBean("testExtension");
        BeanStat result = componentBeanStatCustomizer.customize("extension", extensionFactoryBean,
            beanStat);
        assertThat(result).isNull();
        assertThat(beanStat.getAttribute("extension")).isEqualTo(
            "ExtensionPointTarget: testExtension");
    }

    @Test
    public void updateExtensionPointBeanStats() {
        BeanStat beanStat = new BeanStat();
        ExtensionPointFactoryBean extensionPointFactoryBean = new ExtensionPointFactoryBean();
        extensionPointFactoryBean.setTargetBeanName("testExtensionPoint");
        BeanStat result = componentBeanStatCustomizer.customize("extensionPoint",
            extensionPointFactoryBean, beanStat);
        assertThat(result).isNull();
        assertThat(beanStat.getAttribute("extension")).isEqualTo(
            "ExtensionPointTarget: testExtensionPoint");
    }

    @Test
    public void updateNormalBeanStats() {
        BeanStat beanStat = new BeanStat();
        Object object = new Object();
        BeanStat result = componentBeanStatCustomizer.customize("normal", object, beanStat);
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(beanStat);
    }
}
