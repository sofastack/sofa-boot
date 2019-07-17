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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;
import com.alipay.sofa.tracer.plugins.datasource.utils.DataSourceUtils;

/**
 * @author qilong.zql
 * @since 2.2.0
 */
public class DataSourceBeanFactoryPostProcessor implements BeanFactoryPostProcessor,
                                               PriorityOrdered, EnvironmentAware {

    public static final String SOFA_TRACER_DATASOURCE = "s_t_d_s_";

    private Environment        environment;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
                                                                                   throws BeansException {
        for (String beanName : getBeanNames(beanFactory, DataSource.class)) {
            if (beanName.startsWith(SOFA_TRACER_DATASOURCE)) {
                continue;
            }
            BeanDefinition dataSource = getBeanDefinition(beanName, beanFactory);
            if (DataSourceUtils.isDruidDataSource(dataSource.getBeanClassName())) {
                createDataSourceProxy(beanFactory, beanName, dataSource,
                    DataSourceUtils.getDruidJdbcUrlKey());
            } else if (DataSourceUtils.isC3p0DataSource(dataSource.getBeanClassName())) {
                createDataSourceProxy(beanFactory, beanName, dataSource,
                    DataSourceUtils.getC3p0JdbcUrlKey());
            } else if (DataSourceUtils.isDbcpDataSource(dataSource.getBeanClassName())) {
                createDataSourceProxy(beanFactory, beanName, dataSource,
                    DataSourceUtils.getDbcpJdbcUrlKey());
            } else if (DataSourceUtils.isTomcatDataSource(dataSource.getBeanClassName())) {
                createDataSourceProxy(beanFactory, beanName, dataSource,
                    DataSourceUtils.getTomcatJdbcUrlKey());
            } else if (DataSourceUtils.isHikariDataSource(dataSource.getBeanClassName())) {
                createDataSourceProxy(beanFactory, beanName, dataSource,
                    DataSourceUtils.getHikariJdbcUrlKey());
            }
        }
    }

    private Iterable<String> getBeanNames(ListableBeanFactory beanFactory, Class clazzType) {
        Set<String> names = new HashSet<>();
        names.addAll(Arrays.asList(BeanFactoryUtils.beanNamesForTypeIncludingAncestors(beanFactory,
            clazzType, true, false)));
        return names;
    }

    private BeanDefinition getBeanDefinition(String beanName,
                                             ConfigurableListableBeanFactory beanFactory) {
        try {
            return beanFactory.getBeanDefinition(beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            BeanFactory parentBeanFactory = beanFactory.getParentBeanFactory();
            if (parentBeanFactory instanceof ConfigurableListableBeanFactory) {
                return getBeanDefinition(beanName,
                    (ConfigurableListableBeanFactory) parentBeanFactory);
            }
            throw ex;
        }
    }

    private void createDataSourceProxy(ConfigurableListableBeanFactory beanFactory,
                                       String beanName, BeanDefinition originDataSource,
                                       String jdbcUrl) {
        // re-register origin datasource bean
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
        beanDefinitionRegistry.removeBeanDefinition(beanName);
        boolean isPrimary = originDataSource.isPrimary();
        originDataSource.setPrimary(false);
        beanDefinitionRegistry.registerBeanDefinition(transformDatasourceBeanName(beanName),
            originDataSource);
        // register proxied datasource
        RootBeanDefinition proxiedBeanDefinition = new RootBeanDefinition(SmartDataSource.class);
        proxiedBeanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
        proxiedBeanDefinition.setPrimary(isPrimary);
        proxiedBeanDefinition.setInitMethodName("init");
        proxiedBeanDefinition.setDependsOn(transformDatasourceBeanName(beanName));
        MutablePropertyValues originValues = originDataSource.getPropertyValues();
        MutablePropertyValues values = new MutablePropertyValues();
        String appName = environment.getProperty(TRACER_APPNAME_KEY);
        Assert.isTrue(!StringUtils.isBlank(appName), TRACER_APPNAME_KEY + " must be configured!");
        values.add("appName", appName);
        values.add("delegate", new RuntimeBeanReference(transformDatasourceBeanName(beanName)));
        values.add("dbType",
            DataSourceUtils.resolveDbTypeFromUrl(unwrapPropertyValue(originValues.get(jdbcUrl))));
        values.add("database",
            DataSourceUtils.resolveDatabaseFromUrl(unwrapPropertyValue(originValues.get(jdbcUrl))));
        proxiedBeanDefinition.setPropertyValues(values);
        beanDefinitionRegistry.registerBeanDefinition(beanName, proxiedBeanDefinition);
    }

    protected String unwrapPropertyValue(Object propertyValue) {
        if (propertyValue instanceof TypedStringValue) {
            return ((TypedStringValue) propertyValue).getValue();
        } else if (propertyValue instanceof String) {
            return (String) propertyValue;
        }
        throw new IllegalArgumentException(
            "The property value of jdbcUrl must be the type of String or TypedStringValue");
    }

    public static String transformDatasourceBeanName(String originName) {
        return SOFA_TRACER_DATASOURCE + originName;
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