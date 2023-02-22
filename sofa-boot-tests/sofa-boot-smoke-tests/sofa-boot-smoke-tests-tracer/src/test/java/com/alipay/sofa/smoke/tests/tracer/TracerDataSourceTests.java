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

/**
 * @author huzijie
 * @version TracerDataSourceTests.java, v 0.1 2023年02月22日 2:31 PM huzijie Exp $
 */

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration for tracer datasource enhance.
 *
 * @author huzijie
 * @version TracerDataSourceTests.java, v 0.1 2023年02月22日 2:32 PM huzijie Exp $
 */
@SpringBootTest(classes = TracerSofaBootApplication.class)
public class TracerDataSourceTests {

    private final File logFile = new File(System.getProperty("user.dir")
                                          + "/logs/tracelog/datasource-client-digest.log");

    @Autowired
    @Qualifier("druidDataSource")
    private DataSource dataSource;

    @Value("${spring.application.name}")
    private String     appName;

    @Test
    public void checkDruidDataSource() throws IOException {
        Map<String, Object> result = invokeSql(dataSource);
        assertThat(result.get("success")).isEqualTo(true);
        //todo 优化日志校验
        checkLogFile();
    }

    private void checkLogFile() throws IOException {
        List<String> logs = FileUtils.readLines(logFile, StandardCharsets.UTF_8);
        assertThat(logs).anyMatch(log -> log.contains(appName));
    }

    private Map<String, Object> invokeSql(DataSource dataSource) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            Connection cn = dataSource.getConnection();
            Statement st = cn.createStatement();
            st.execute("DROP TABLE IF EXISTS TEST;"
                       + "CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));");
            resultMap.put("success", true);
            resultMap.put("result", "CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255))");
        } catch (Throwable throwable) {
            resultMap.put("success", false);
            resultMap.put("error", throwable.getMessage());
        }
        return resultMap;
    }
}
