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
package com.alipay.sofa.startup.stage;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.isle.spring.share.UnshareSofaModulePostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huzijie
 * @version BeanCostBeanPostProcessor.java, v 0.1 2020年12月31日 2:40 下午 huzijie Exp $
 */
@UnshareSofaModulePostProcessor
public class BeanCostBeanPostProcessor implements BeanPostProcessor {
    private final static String         SOFA_CLASS_NAME_PREFIX = "com.alipay.sofa.runtime";
    private final Map<String, BeanStat> beanInitCostMap        = new ConcurrentHashMap<>();
    private final List<BeanStat>        beanStatList           = new ArrayList<>();
    private final long                  beanLoadCost;
    private final boolean               skipSofaBean;

    public BeanCostBeanPostProcessor(long beanInitCostThreshold, boolean skipSofaBean) {
        this.beanLoadCost = beanInitCostThreshold;
        this.skipSofaBean = skipSofaBean;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (!skipSofaBean || isNotSofaBean(bean)) {
            BeanStat beanStat = new BeanStat();
            String beanClassName = getBeanName(bean, beanName);
            beanStat.setBeanClassName(beanClassName);
            beanStat.startRefresh();
            beanInitCostMap.put(beanClassName, beanStat);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        if (!skipSofaBean || isNotSofaBean(bean)) {
            String beanClassName = getBeanName(bean, beanName);
            BeanStat beanStat = beanInitCostMap.remove(beanClassName);
            if (beanStat != null) {
                beanStat.finishRefresh();
                if (beanStat.getRefreshElapsedTime() > beanLoadCost) {
                    beanStat.finishRefresh();
                    beanStatList.add(beanStat);
                }
            }
        }
        return bean;
    }

    public List<BeanStat> getBeanStatList() {
        return this.beanStatList;
    }

    private boolean isNotSofaBean(Object bean) {
        return !bean.getClass().getName().contains(SOFA_CLASS_NAME_PREFIX);
    }

    private String getBeanName(Object bean, String beanName) {
        return bean.getClass().getName() + " (" + beanName + ")";
    }
}
