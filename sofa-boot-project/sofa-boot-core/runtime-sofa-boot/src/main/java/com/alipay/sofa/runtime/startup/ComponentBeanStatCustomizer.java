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
import com.alipay.sofa.boot.startup.BeanStatCustomizer;
import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;
import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;

/**
 * Implementation of {@link BeanStatCustomizer} to update component information.
 *
 * @author huzijie
 * @version ComponentBeanStatCustomizer.java, v 0.1 2023年01月12日 7:47 PM huzijie Exp $
 * @since 4.0.0
 */
public class ComponentBeanStatCustomizer implements BeanStatCustomizer {

    @Override
    public BeanStat customize(String beanName, Object bean, BeanStat bs) {
        if (bean instanceof ServiceFactoryBean) {
            bs.putAttribute("interface", ((ServiceFactoryBean) bean).getInterfaceType());
            bs.putAttribute("uniqueId", ((ServiceFactoryBean) bean).getUniqueId());
            return null;
        } else if (bean instanceof ReferenceFactoryBean) {
            bs.putAttribute("interface", ((ReferenceFactoryBean) bean).getInterfaceType());
            bs.putAttribute("uniqueId", ((ReferenceFactoryBean) bean).getUniqueId());
            return null;
        }
        if (bean instanceof ExtensionFactoryBean) {
            bs.putAttribute("extension", bean.toString());
            return null;
        }
        if (bean instanceof ExtensionPointFactoryBean) {
            bs.putAttribute("extension", bean.toString());
            return null;
        }
        return bs;
    }
}
