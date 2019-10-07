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
package com.alipay.sofa.tracer.boot.datasource;

import java.lang.reflect.Method;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alipay.common.tracer.core.utils.ReflectionUtils;
import com.alipay.sofa.tracer.plugins.datasource.utils.DataSourceUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author qilong.zql
 * @since 2.3.2
 */
public class DataSourceUrlTest {
    @Test
    public void testGetDataSourceUrl() throws Throwable {
        // Druid
        DruidDataSource druidDataSource = new DruidDataSource();
        druidDataSource.setUrl("test-url");
        Method method = ReflectionUtils.findMethod(druidDataSource.getClass(),
            DataSourceUtils.METHOD_GET_URL);
        Assert.assertNotNull(method);
        Assert.assertEquals("test-url", method.invoke(druidDataSource));

        // dbcp
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl("test-url");
        method = ReflectionUtils.findMethod(basicDataSource.getClass(),
            DataSourceUtils.METHOD_GET_URL);
        Assert.assertNotNull(method);
        Assert.assertEquals("test-url", method.invoke(basicDataSource));

        // tomcat datasource
        DataSource dataSource = new DataSource();
        dataSource.setUrl("test-url");
        method = ReflectionUtils.findMethod(dataSource.getClass(), DataSourceUtils.METHOD_GET_URL);
        Assert.assertNotNull(method);
        Assert.assertEquals("test-url", method.invoke(dataSource));

        // c3p0
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        comboPooledDataSource.setJdbcUrl("test-url");
        method = ReflectionUtils.findMethod(comboPooledDataSource.getClass(),
            DataSourceUtils.METHOD_GET_JDBC_URL);
        Assert.assertNotNull(method);
        Assert.assertEquals("test-url", method.invoke(comboPooledDataSource));

        // hikari
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl("test-url");
        method = ReflectionUtils.findMethod(hikariDataSource.getClass(),
            DataSourceUtils.METHOD_GET_JDBC_URL);
        Assert.assertNotNull(method);
        Assert.assertEquals("test-url", method.invoke(hikariDataSource));
    }
}