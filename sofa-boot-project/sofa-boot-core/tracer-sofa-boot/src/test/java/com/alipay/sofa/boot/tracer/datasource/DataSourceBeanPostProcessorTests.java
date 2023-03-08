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
package com.alipay.sofa.boot.tracer.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DataSourceBeanPostProcessor}.
 *
 * @author huzijie
 * @version DataSourceBeanPostProcessorTests.java, v 0.1 2023年01月09日 5:37 PM huzijie Exp $
 */
public class DataSourceBeanPostProcessorTests {

    private final String                      testUrl                     = "jdbc:oracle:thin:@localhost:1521:orcl";

    private final DataSourceBeanPostProcessor dataSourceBeanPostProcessor = new DataSourceBeanPostProcessor();

    {
        dataSourceBeanPostProcessor.setAppName("testApp");
    }

    @Test
    public void skipSmartDataSource() {
        SmartDataSource smartDataSource = new SmartDataSource(new EmptyDataSource());
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(smartDataSource,
            "normalBean");
        assertThat(bean).isEqualTo(smartDataSource);
    }

    @Test
    public void skipNoDataSource() {
        Object noDataSource = new Object();
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(noDataSource,
            "normalBean");
        assertThat(bean).isEqualTo(noDataSource);
        assertThat(bean).isNotInstanceOf(SmartDataSource.class);
    }

    @Test
    public void wrapDruidDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }

    @Test
    public void wrapDbcpDataSource() {
        org.apache.commons.dbcp.BasicDataSource dataSource = new org.apache.commons.dbcp.BasicDataSource();
        dataSource.setUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }

    @Test
    public void wrapDbcp2DataSource() {
        org.apache.commons.dbcp2.BasicDataSource dataSource = new org.apache.commons.dbcp2.BasicDataSource();
        dataSource.setUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }

    @Test
    public void wrapTomcatDataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }

    @Test
    public void wrapC3p0DataSource() {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        dataSource.setJdbcUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }

    @Test
    public void wrapHikariDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(testUrl);
        Object bean = dataSourceBeanPostProcessor.postProcessAfterInitialization(dataSource,
            "normalBean");
        assertThat(bean).isNotNull();
        assertThat(bean).isNotEqualTo(dataSource);
        assertThat(bean).isInstanceOf(SmartDataSource.class);
        assertThat(((SmartDataSource) bean).getDelegate()).isEqualTo(dataSource);
    }
}
