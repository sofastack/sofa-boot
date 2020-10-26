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

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.springframework.beans.factory.BeanCreationException;
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
    private static final long                   DEFAULT_BEAN_LOAD_COST = 100;

    private final List<BeanNode>                beanCostList           = new ArrayList<>();

    private long                                beanLoadCost           = DEFAULT_BEAN_LOAD_COST;

    private String                              moduleName;

    private static ThreadLocal<Stack<BeanNode>> parentStackThreadLocal = new ThreadLocal<>();

    public BeanLoadCostBeanFactory(long beanCost, String moduleName) {
        this.beanLoadCost = beanCost;
        this.moduleName = moduleName;
    }

    @Override
    protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
                                                                                                 throws BeanCreationException {
        Stack<BeanNode> parentStack = parentStackThreadLocal.get();
        BeanNode bn = new BeanNode();
        if (parentStack == null) {
            parentStack = new Stack<>();
            parentStackThreadLocal.set(parentStack);
        }
        if (!parentStack.empty()) {
            parentStack.peek().addChild(bn);
        }
        parentStack.push(bn);

        long begin = System.currentTimeMillis();
        Object object = super.createBean(beanName, mbd, args);

        if (mbd.getBeanClassName() == null) {
            bn.setBeanClassName("Factory (" + mbd.getFactoryBeanName() + ")");
        } else {
            if (object instanceof ServiceFactoryBean) {
                bn.setBeanClassName(mbd.getBeanClassName() + " ("
                                    + ((ServiceFactoryBean) object).getBeanId() + ")");
            } else {
                bn.beanClassName = mbd.getBeanClassName() + " (" + beanName + ")";
                if (beanName.contains(mbd.getBeanClassName())) {
                    bn.setBeanClassName(mbd.getBeanClassName());
                }
            }
        }
        bn.setCostTime(System.currentTimeMillis() - begin);

        parentStack.pop();
        if (parentStack.empty() && bn.getCostTime() > beanLoadCost) {
            beanCostList.add(bn);
        }

        return object;
    }

    public List<BeanNode> getBeanLoadList() {
        return beanCostList;
    }

    public String getModuleName() {
        return moduleName;
    }

    public static class BeanNode {
        private static final String  LAST_PREFIX   = "└─";
        private static final String  MIDDLE_PREFIX = "├─";
        private static final String  INDENT_PREFIX = "│   ";

        private String               beanClassName;

        // costTime includes all bean refreshing time-consumption incurred by this bean
        private long                 costTime;

        private final List<BeanNode> children      = new ArrayList<>();

        public String getBeanClassName() {
            return beanClassName;
        }

        public void setCostTime(long time) {
            costTime = time;
        }

        public long getCostTime() {
            return costTime;
        }

        public void setBeanClassName(String beanClassName) {
            this.beanClassName = beanClassName;
        }

        public void addChild(BeanNode bn) {
            children.add(bn);
        }

        public List<BeanNode> getChildren() {
            return children;
        }

        public String toString() {
            return toString("", false);
        }

        private String toString(String indent, boolean last) {
            StringBuilder rtn = new StringBuilder();
            rtn.append(indent).append(last ? LAST_PREFIX : MIDDLE_PREFIX).append(beanClassName)
                .append("  [").append(costTime).append("ms]");

            int size = children.size();

            for (int i = 0; i < children.size(); ++i) {
                rtn.append("\n").append(
                    children.get(i).toString(indent + INDENT_PREFIX, i == size - 1));
            }
            return rtn.toString();
        }
    }

    public String outputBeanLoadCost(String indent) {
        StringBuilder rtn = new StringBuilder();

        beanCostList.sort((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 != null && o2 == null) {
                return 1;
            } else if (o1 == null) {
                return -1;
            }
            return Long.compare(o2.getCostTime(), o1.getCostTime());
        });

        int size = beanCostList.size();
        for (int i = 0; i < size; ++i) {
            rtn.append(beanCostList.get(i).toString(indent, i == size - 1));
            rtn.append("\n");
        }
        return rtn.toString();
    }
}
