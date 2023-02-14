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
package com.alipay.sofa.boot.actuator.beans;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link Endpoint @Endpoint} to expose details of an application's beans, grouped by
 * application context, support expose beans in sofa modules.
 *
 * @author huzijie
 * @version IsleBeansEndpoint.java, v 0.1 2022年03月17日 11:10 AM huzijie Exp $
 */
public class IsleBeansEndpoint extends BeansEndpoint {

    private final ApplicationRuntimeModel applicationRuntimeModel;

    /**
     * Creates a new {@code BeansEndpoint} that will describe the beans in the given
     * {@code context} and all of its ancestors.
     *
     * @param context the application context
     * @param applicationRuntimeModel the application runtime model
     * @see ConfigurableApplicationContext#getParent()
     */
    public IsleBeansEndpoint(ConfigurableApplicationContext context,
                             ApplicationRuntimeModel applicationRuntimeModel) {
        super(context);
        this.applicationRuntimeModel = applicationRuntimeModel;
    }

    @ReadOperation
    @Override
    public BeansEndpoint.BeansDescriptor beans() {
        BeansEndpoint.BeansDescriptor beansDescriptor = super.beans();
        Map<String, BeansEndpoint.ContextBeansDescriptor> moduleApplicationContexts = getModuleApplicationContexts(applicationRuntimeModel);
        beansDescriptor.getContexts().putAll(moduleApplicationContexts);
        return beansDescriptor;
    }

    private Map<String, BeansEndpoint.ContextBeansDescriptor> getModuleApplicationContexts(ApplicationRuntimeModel applicationRuntimeModel) {
        Map<String, BeansEndpoint.ContextBeansDescriptor> contexts = new HashMap<>();
        List<DeploymentDescriptor> installedModules = applicationRuntimeModel.getInstalled();
        installedModules.forEach(descriptor -> {
            ApplicationContext applicationContext = descriptor.getApplicationContext();
            if (applicationContext instanceof ConfigurableApplicationContext) {
                BeansEndpoint.ContextBeansDescriptor contextBeans = describing((ConfigurableApplicationContext) applicationContext,
                        descriptor.getSpringParent());
                if (contextBeans != null) {
                    contexts.put(descriptor.getModuleName(), contextBeans);
                }
            }
        });
        return contexts;
    }

    private BeansEndpoint.ContextBeansDescriptor describing(ConfigurableApplicationContext context,
                                                            String parentModuleName) {
        Map<String, BeanDescriptor> beanDescriptorMap = callContextBeansDescribeBeans(context
            .getBeanFactory());
        return createContextBeans(beanDescriptorMap, parentModuleName);
    }

    // FIXME only can use reflect now
    private Map<String, BeanDescriptor> callContextBeansDescribeBeans(ConfigurableListableBeanFactory beanFactory) {
        try {
            Class<?> clazz = BeansEndpoint.ContextBeansDescriptor.class;
            Method method = clazz.getDeclaredMethod("describeBeans",
                ConfigurableListableBeanFactory.class);
            method.setAccessible(true);
            Object result = method.invoke(null, beanFactory);
            return (Map<String, BeanDescriptor>) result;
        } catch (Throwable e) {
            // ignore
            return new HashMap<>();
        }
    }

    // FIXME only can use reflect now
    private BeansEndpoint.ContextBeansDescriptor createContextBeans(Map<String, BeanDescriptor> beans,
                                                                    String parentId) {
        try {
            Class<?> clazz = BeansEndpoint.ContextBeansDescriptor.class;
            Constructor<?> constructor = clazz.getDeclaredConstructor(Map.class, String.class);
            constructor.setAccessible(true);
            return (BeansEndpoint.ContextBeansDescriptor) constructor.newInstance(beans, parentId);
        } catch (Throwable e) {
            // ignore
            return null;
        }
    }

}
