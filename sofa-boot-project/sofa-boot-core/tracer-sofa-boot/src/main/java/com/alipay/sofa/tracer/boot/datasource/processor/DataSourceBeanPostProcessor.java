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
package com.alipay.sofa.tracer.boot.datasource.processor;

import static com.alipay.common.tracer.core.configuration.SofaTracerConfiguration.TRACER_APPNAME_KEY;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.alipay.common.tracer.core.utils.ReflectionUtils;
import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;
import com.alipay.sofa.tracer.plugins.datasource.utils.DataSourceUtils;

/**
 * @author qilong.zql
 * @since 2.3.2
 */
public class DataSourceBeanPostProcessor implements BeanPostProcessor, EnvironmentAware,
                                        PriorityOrdered {

    private Environment environment;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
                                                                               throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
                                                                              throws BeansException {
        /**
         * filter transformed datasource {@link DataSourceBeanFactoryPostProcessor}
         * filter bean which is type of {@link SmartDataSource}
         * filter bean which is not type of {@link DataSource}
         */
        if (beanName.startsWith(DataSourceBeanFactoryPostProcessor.SOFA_TRACER_DATASOURCE)
            || bean instanceof SmartDataSource || !(bean instanceof DataSource)) {
            return bean;
        }

        String getUrlMethodName;
        String url;
        /**
         * Now DataSource Tracer only support the following type: Druid, C3p0, Dbcp, tomcat datasource, hikari
         */
        if (DataSourceUtils.isDruidDataSource(bean) || DataSourceUtils.isDbcpDataSource(bean)
            || DataSourceUtils.isTomcatDataSource(bean)) {
            getUrlMethodName = DataSourceUtils.METHOD_GET_URL;
        } else if (DataSourceUtils.isC3p0DataSource(bean)
                   || DataSourceUtils.isHikariDataSource(bean)) {
            getUrlMethodName = DataSourceUtils.METHOD_GET_JDBC_URL;
        } else {
            return bean;
        }

        try {
            Method urlMethod = ReflectionUtils.findMethod(bean.getClass(), getUrlMethodName);
            urlMethod.setAccessible(true);
            url = (String) urlMethod.invoke(bean);
        } catch (Throwable throwable) {
            throw new BeanCreationException(String.format("Can not find method: %s in class %s.",
                getUrlMethodName, bean.getClass().getCanonicalName()), throwable);
        }

        SmartDataSource proxiedDataSource = new SmartDataSource((DataSource) bean);
        String appName = environment.getProperty(TRACER_APPNAME_KEY);
        Assert.isTrue(!StringUtils.isBlank(appName), TRACER_APPNAME_KEY + " must be configured!");
        proxiedDataSource.setAppName(appName);
        proxiedDataSource.setDbType(DataSourceUtils.resolveDbTypeFromUrl(url));
        proxiedDataSource.setDatabase(DataSourceUtils.resolveDatabaseFromUrl(url));

        // execute proxied datasource init-method
        proxiedDataSource.init();
        return proxiedDataSource;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}