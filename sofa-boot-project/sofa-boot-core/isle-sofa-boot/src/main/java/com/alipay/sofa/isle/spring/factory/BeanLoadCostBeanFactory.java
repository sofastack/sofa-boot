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
package com.alipay.sofa.isle.spring.factory;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BeanStatExtension;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import org.springframework.lang.Nullable;

/**
 * beanFactory which can get bean load time
 *
 * @author xiangxing.deng 2012-11-20
 */
public class BeanLoadCostBeanFactory extends DefaultListableBeanFactory {
    private final List<BeanStat>                beanStats              = new ArrayList<>();

    private long                                beanLoadCost;

    private String                              moduleName;

    private static ThreadLocal<Stack<BeanStat>> parentStackThreadLocal = new ThreadLocal<>();

    private BeanStatExtension                   beanStatExtension;

    public BeanLoadCostBeanFactory(long beanCost, String moduleName) {
        this.beanLoadCost = beanCost;
        this.moduleName = moduleName;
    }

    public BeanLoadCostBeanFactory(long beanCost, String moduleName,
                                   BeanStatExtension beanStatExtension) {
        this.beanLoadCost = beanCost;
        this.moduleName = moduleName;
        this.beanStatExtension = beanStatExtension;
    }

    @Override
    protected void invokeInitMethods(String beanName, final Object bean, RootBeanDefinition mbd)
            throws Throwable {
        boolean isInitializingBean = (bean instanceof InitializingBean);
        if (isInitializingBean
                && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
            if (System.getSecurityManager() != null) {
                try {
                    AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                        ((InitializingBean) bean).afterPropertiesSet();
                        return null;
                    }, getAccessControlContext());
                } catch (PrivilegedActionException pae) {
                    throw pae.getException();
                }
            } else {
                long start = System.currentTimeMillis();
                ((InitializingBean) bean).afterPropertiesSet();
                parentStackThreadLocal.get().peek()
                        .setAfterPropertiesSetTime(System.currentTimeMillis() - start);
            }
        }

        if (mbd != null) {
            String initMethodName = mbd.getInitMethodName();
            if (initMethodName != null
                    && !(isInitializingBean && "afterPropertiesSet".equals(initMethodName))
                    && !mbd.isExternallyManagedInitMethod(initMethodName)) {
                long start = System.currentTimeMillis();
                invokeCustomInitMethod(beanName, bean, mbd);
                parentStackThreadLocal.get().peek().setInitTime(System.currentTimeMillis() - start);
            }
        }
    }

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
                                                                                                 throws BeanCreationException {
        Stack<BeanStat> parentStack = parentStackThreadLocal.get();
        BeanStat bs = new BeanStat();
        if (parentStack == null) {
            parentStack = new Stack<>();
            parentStackThreadLocal.set(parentStack);
        }
        if (!parentStack.empty()) {
            parentStack.peek().addChild(bs);
        }
        parentStack.push(bs);

        bs.startRefresh();
        Object object = super.createBean(beanName, mbd, args);
        bs.finishRefresh();

        if (mbd.getBeanClassName() == null) {
            bs.setBeanClassName("Factory (" + mbd.getFactoryBeanName() + ")");
        } else {
            if (mbd.getBeanClassName().contains("ExtensionPointFactoryBean")
                || mbd.getBeanClassName().contains("ExtensionFactoryBean")) {
                bs.setExtensionProperty(object.toString());
            }

            if (object instanceof ServiceFactoryBean) {
                bs.setBeanClassName(mbd.getBeanClassName() + " ("
                                    + ((ServiceFactoryBean) object).getBeanId() + ")");
                bs.setInterfaceType(((ServiceFactoryBean) object).getInterfaceType());
            } else if (object instanceof ReferenceFactoryBean) {
                bs.setBeanClassName(mbd.getBeanClassName() + " (" + beanName + ")");
                bs.setInterfaceType(((ReferenceFactoryBean) object).getInterfaceType());
            } else {
                bs.setBeanClassName(mbd.getBeanClassName() + " (" + beanName + ")");
                if (beanName.contains(mbd.getBeanClassName())) {
                    bs.setBeanClassName(mbd.getBeanClassName());
                }
            }
        }

        if (beanStatExtension != null) {
            beanStatExtension.customBeanStat(beanName, mbd, args, bs);
        }

        parentStack.pop();
        if (parentStack.empty() && bs.getRefreshElapsedTime() > beanLoadCost) {
            beanStats.add(bs);
        }

        return object;
    }

    public List<BeanStat> getBeanStats() {
        return beanStats;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String outputBeanStats(String indent) {
        StringBuilder rtn = new StringBuilder();
        beanStats.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 == null) {
                return -1;
            }
            return o2.getRealRefreshElapsedTime() > o1.getRealRefreshElapsedTime() ? 1 : -1;
        });
        int size = beanStats.size();
        for (int i = 0; i < size; ++i) {
            rtn.append(beanStats.get(i).toString(indent, i == size - 1));
            rtn.append("\n");
        }
        return rtn.toString();
    }
}
