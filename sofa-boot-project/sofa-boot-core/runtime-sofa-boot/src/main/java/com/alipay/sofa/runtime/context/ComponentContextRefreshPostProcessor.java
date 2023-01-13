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
package com.alipay.sofa.runtime.context;

import com.alipay.sofa.boot.context.ContextRefreshPostProcessor;
import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.boot.log.SofaLogger;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import com.alipay.sofa.runtime.spring.SpringContextImplementation;

import java.util.Collection;

/**
 * @author huzijie
 * @version ComponentContextRefreshPostProcessor.java, v 0.1 2023年01月12日 3:37 PM huzijie Exp $
 */
public class ComponentContextRefreshPostProcessor implements ContextRefreshPostProcessor {

    private boolean                  unregisterComponentWhenContextRefreshFailure;

    private final ComponentManager   componentManager;

    private final SofaRuntimeContext sofaRuntimeContext;                           ;

    public ComponentContextRefreshPostProcessor(ComponentManager componentManager,
                                                SofaRuntimeContext sofaRuntimeContext) {
        this.componentManager = componentManager;
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public void postProcessAfterRefresh(SofaGenericApplicationContext context, Throwable throwable) {
        if (throwable == null) {
            ComponentName componentName = ComponentNameFactory.createComponentName(
                SpringContextComponent.SPRING_COMPONENT_TYPE, context.getId());
            Implementation implementation = new SpringContextImplementation(context);
            ComponentInfo componentInfo = new SpringContextComponent(componentName, implementation,
                sofaRuntimeContext);
            componentManager.register(componentInfo);
        }

        if (throwable != null && unregisterComponentWhenContextRefreshFailure) {
            Collection<ComponentInfo> componentInfos = componentManager
                .getComponentInfosByApplicationContext(context);
            for (ComponentInfo componentInfo : componentInfos) {
                try {
                    componentManager.unregister(componentInfo);
                } catch (ServiceRuntimeException e) {
                    SofaLogger.error(ErrorCode.convert("01-03001", componentInfo.getName()), e);
                }
            }
        }
    }
}
