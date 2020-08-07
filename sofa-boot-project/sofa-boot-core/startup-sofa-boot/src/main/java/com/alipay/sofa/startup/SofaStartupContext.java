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
package com.alipay.sofa.startup;

import com.alipay.sofa.boot.startup.CommonStartupCost;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.startup.spring.SpringContextAwarer;
import com.alipay.sofa.startup.webserver.StartupJettyServletWebServerFactory;
import com.alipay.sofa.startup.webserver.StartupTomcatServletWebServerFactory;
import com.alipay.sofa.startup.webserver.StartupUndertowServletWebServerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The context to store the startup time costs
 *
 * @author: Zhijie
 * @since: 2020/7/7
 */
public class SofaStartupContext implements BeanPostProcessor, SofaRuntimeContextAware {

    private final static String              SOFA_CLASS_NAME_PREFIX = "com.alipay.sofa.runtime";
    protected long                           appStartupTime;
    protected List<CommonStartupCost>        beanInitCostList       = new ArrayList<>();
    protected Map<String, CommonStartupCost> beanInitCostMap        = new ConcurrentHashMap<>();
    private final SpringContextAwarer        springContextAwarer;
    private final long                       beanLoadCost;
    protected SofaRuntimeContext             sofaRuntimeContext;

    public SofaStartupContext(SpringContextAwarer springContextAwarer,
                              SofaStartupProperties sofaStartupProperties) {
        this.springContextAwarer = springContextAwarer;
        this.beanLoadCost = sofaStartupProperties.getBeanInitCostThreshold();
    }

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        if (isNotSofaBean(bean)) {
            String name = getModuleName() + "_" + beanName;
            CommonStartupCost commonStartupCost = new CommonStartupCost();
            commonStartupCost.setName(name);
            commonStartupCost.setBeginTime(System.currentTimeMillis());
            beanInitCostMap.put(name, commonStartupCost);
            beanInitCostList.add(commonStartupCost);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        if (beanInitCostList.size() > 0 && isNotSofaBean(bean)) {
            String name = getModuleName() + "_" + beanName;
            CommonStartupCost commonStartupCost = beanInitCostMap.get(name);
            if (commonStartupCost != null) {
                commonStartupCost.setEndTime(System.currentTimeMillis());
            }
        }
        return bean;
    }

    public long getComponentCost() {
        return sofaRuntimeContext.getComponentManager().getComponentCostList().stream()
                .mapToLong(commonStartupCost -> commonStartupCost.getEndTime() - commonStartupCost.getBeginTime()).sum();
    }

    public Map<String, Long> getComponentDetail() {
        return sofaRuntimeContext.getComponentManager().getComponentCostList().stream()
                .collect(Collectors.toMap(CommonStartupCost::getName,
                        commonStartupCost -> commonStartupCost.getEndTime() - commonStartupCost.getBeginTime(),
                        (oldValue, newValue) -> oldValue, TreeMap::new));
    }

    public long getIsleInstallCost() {
        return springContextAwarer.getIsleContextInstallCost();
    }

    public long getWebServerInitCost() {
        ServletWebServerFactory servletWebServerFactory = springContextAwarer
            .getServletWebServerFactory();
        if (servletWebServerFactory instanceof TomcatServletWebServerFactory) {
            return StartupTomcatServletWebServerFactory.getEndTime()
                   - StartupTomcatServletWebServerFactory.getBeginTime();
        } else if (servletWebServerFactory instanceof JettyServletWebServerFactory) {
            return StartupJettyServletWebServerFactory.getEndTime()
                   - StartupJettyServletWebServerFactory.getBeginTime();
        } else if (servletWebServerFactory instanceof UndertowServletWebServerFactory) {
            return StartupUndertowServletWebServerFactory.getEndTime()
                   - StartupUndertowServletWebServerFactory.getBeginTime();
        }
        return -1L;
    }

    public long getBeanInitCost() {
        return beanInitCostList.stream()
                .filter(commonStartupCost -> commonStartupCost.getEndTime() != -1L)
                .mapToLong(commonStartupCost -> commonStartupCost.getEndTime() - commonStartupCost.getBeginTime()).sum();
    }

    public Map<String, Long> getBeanInitDetail() {
        return beanInitCostList.stream()
                .filter(commonStartupCost -> commonStartupCost.getEndTime() != -1L
                        && commonStartupCost.getEndTime() - commonStartupCost.getBeginTime() > beanLoadCost)
                .collect(Collectors.toMap(CommonStartupCost::getName,
                        commonStartupCost -> commonStartupCost.getEndTime() - commonStartupCost.getBeginTime(),
                        (oldValue, newValue) -> oldValue, TreeMap::new));
    }

    public String getModuleName() {
        return springContextAwarer.getModuleName();
    }

    private boolean isNotSofaBean(Object bean) {
        return !bean.getClass().getName().contains(SOFA_CLASS_NAME_PREFIX);
    }

    public void setAppStartupTime(long appStartupTime) {
        this.appStartupTime = appStartupTime;
    }

    public long getAppStartupTime() {
        return this.appStartupTime;
    }
}
