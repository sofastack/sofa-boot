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
package com.alipay.sofa.boot.context;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BeanStatCustomizer;
import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Default Implementation of {@link DefaultListableBeanFactory} in SOFABoot framework.
 *
 * <p>Support record bean create cost and init methods cost.
 *
 * @author huzijie
 * @version SofaBeanFactory.java, v 0.1 2023年01月12日 12:35 PM huzijie Exp $
 * @since 4.0.0
 */
public class SofaDefaultListableBeanFactory extends DefaultListableBeanFactory {

    private static final ThreadLocal<Stack<BeanStat>> PARENT_STACK_THREAD_LOCAL = new ThreadLocal<>();

    private final List<BeanStat>                      beanStats                 = new ArrayList<>();

    private final List<BeanStatCustomizer>            beanStatCustomizers       = new ArrayList<>();

    private int                                       costThreshold;

    @Override
    protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd)
                                                                                                throws Throwable {
        long start = System.currentTimeMillis();
        super.invokeInitMethods(beanName, bean, mbd);
        long end = System.currentTimeMillis();
        PARENT_STACK_THREAD_LOCAL.get().peek().setInitMethodTime(end - start);
    }

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
                                                                                                 throws BeanCreationException {
        Stack<BeanStat> parentStack = PARENT_STACK_THREAD_LOCAL.get();
        BeanStat bs = new BeanStat();
        bs.setName(beanName);
        Class<?> beanClass = BeanDefinitionUtil.resolveBeanClassType(mbd);
        if (beanClass != null) {
            bs.setBeanClassName(beanClass.getName());
        }
        if (parentStack == null) {
            parentStack = new Stack<>();
            PARENT_STACK_THREAD_LOCAL.set(parentStack);
        }
        if (!parentStack.empty()) {
            parentStack.peek().addChild(bs);
        }
        parentStack.push(bs);

        bs.startRefresh();
        Object object = super.createBean(beanName, mbd, args);
        bs.finishRefresh();

        bs = customBeanStat(beanName, object, bs);

        parentStack.pop();
        if (parentStack.empty() && bs.getCost() >= costThreshold) {
            beanStats.add(bs);
        }
        return object;
    }

    @Override
    public void destroySingletons() {
        super.destroySingletons();
        PARENT_STACK_THREAD_LOCAL.remove();
        beanStats.clear();
        beanStatCustomizers.clear();
    }

    private BeanStat customBeanStat(String beanName, Object bean, BeanStat beanStat) {
        if (!CollectionUtils.isEmpty(beanStatCustomizers)) {
            BeanStat result = beanStat;
            for (BeanStatCustomizer customizer : beanStatCustomizers) {
                BeanStat current = customizer.customize(beanName, bean, result);
                if (current == null) {
                    return result;
                }
                result = current;
            }
            return result;
        }
        return beanStat;
    }

    public void addBeanStatCustomizer(BeanStatCustomizer beanStatCustomizer) {
        this.beanStatCustomizers.add(beanStatCustomizer);
    }

    public List<BeanStat> getBeanStats() {
        return beanStats;
    }

    public void setCostThreshold(int costThreshold) {
        this.costThreshold = costThreshold;
    }
}
