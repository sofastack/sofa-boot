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
package com.alipay.sofa.smoke.tests.tracer;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration for tracer datasource enhance.
 *
 * @author huzijie
 * @version TracerDataSourceTests.java, v 0.1 2023年02月22日 2:32 PM huzijie Exp $
 */
@SpringBootTest(classes = TracerSofaBootApplication.class)
public class TracerDataSourceTests {

    private static final File logFile = new File(System.getProperty("user.dir")
                                                 + "/logs/tracelog/datasource-client-digest.log");

    @Autowired
    @Qualifier("druidDataSource")
    private DataSource        druidDataSource;

    @Autowired
    @Qualifier("dbcpDataSource")
    private DataSource        dbcpDataSource;

    @Autowired
    @Qualifier("dbcp2DataSource")
    private DataSource        dbcp2DataSource;

    @Autowired
    @Qualifier("tomcatDatasource")
    private DataSource        tomcatDatasource;

    @Autowired
    @Qualifier("hikariDataSource")
    private DataSource        hikariDataSource;

    @Autowired
    @Qualifier("comboPooledDataSource")
    private DataSource        comboPooledDataSource;

    @BeforeAll
    public static void clearFile() {
        FileUtils.deleteQuietly(logFile);
    }

    @Test
    public void checkDruidDataSource() throws IOException {
        invokeSql(druidDataSource);
    }

    @Test
    public void checkDbcpDataSource() throws IOException {
        invokeSql(dbcpDataSource);
    }

    @Test
    public void checkDbcp2DataSource() throws IOException {
        invokeSql(dbcp2DataSource);
    }

    @Test
    public void checkTomcatDatasource() throws IOException {
        invokeSql(tomcatDatasource);
    }

    @Test
    public void checkHikariDataSource() throws IOException {
        invokeSql(hikariDataSource);
    }

    @Test
    public void checkComboPooledDataSource() throws IOException {
        invokeSql(comboPooledDataSource);
    }

    private void invokeSql(DataSource dataSource) throws IOException {
        String dataSourceName = dataSource.getClass().getName();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute("DROP TABLE IF EXISTS TEST;");
        jdbcTemplate.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));");
        jdbcTemplate.execute("INSERT INTO TEST(ID,NAME) VALUES(1, '" + dataSourceName +"');");
        jdbcTemplate.query("SELECT * FROM TEST", rs -> {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            assertThat(id).isEqualTo(1);
            assertThat(name).isEqualTo(dataSourceName);
        });

        List<String> logs = FileUtils.readLines(logFile, StandardCharsets.UTF_8);
        assertThat(logs).anyMatch(log -> log.contains(dataSourceName));
    }
}
