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

import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * beanFactory which can get bean load time
 *
 * @author xiangxing.deng 2012-11-20
 */
public class BeanLoadCostBeanFactory extends DefaultListableBeanFactory {
    private static final long    DEFAULT_BEAN_LOAD_COST = 100;

    private final List<BeanNode> beanCostList           = new ArrayList<>();

    private long                 beanLoadCost           = DEFAULT_BEAN_LOAD_COST;

    public BeanLoadCostBeanFactory(long beanCost) {
        this.beanLoadCost = beanCost;
    }

    protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args)
                                                                                       throws BeanCreationException {
        long begin = System.currentTimeMillis();
        Object object = super.createBean(beanName, mbd, args);

        BeanNode beanNameNode = new BeanNode();
        if (mbd.getBeanClassName() == null) {
            beanNameNode.beanClassName = "Factory (" + mbd.getFactoryBeanName() + ")";
        } else {
            if (object instanceof ServiceFactoryBean) {
                beanNameNode.beanClassName = mbd.getBeanClassName() + " ("
                                             + ((ServiceFactoryBean) object).getBeanId() + ")";
            } else {
                beanNameNode.beanClassName = mbd.getBeanClassName() + " (" + beanName + ")";
                if (beanName.contains(mbd.getBeanClassName())) {
                    beanNameNode.beanClassName = mbd.getBeanClassName();
                }
            }
        }
        beanNameNode.costTime = System.currentTimeMillis() - begin;
        if (beanNameNode.costTime >= beanLoadCost) {
            beanCostList.add(beanNameNode);
        }
        return object;
    }

    public List<BeanNode> getBeanLoadList() {
        return beanCostList;
    }

    public class BeanNode {
        public String beanClassName;
        public long   costTime;

        public String toString() {
            return beanClassName + "\t[" + costTime + " ms]";
        }
    }
}
