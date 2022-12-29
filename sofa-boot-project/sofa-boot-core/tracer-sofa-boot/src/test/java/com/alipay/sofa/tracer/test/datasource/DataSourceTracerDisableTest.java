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
package com.alipay.sofa.tracer.test.datasource;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.alipay.sofa.tracer.test.base.AbstractTestBase;
import com.zaxxer.hikari.HikariDataSource;

/**DataSourceUtils
 * @author qilong.zql
 * @sicne 2.2.0
 */
@ActiveProfiles("datasource")
public class DataSourceTracerDisableTest extends AbstractTestBase {

    @Autowired
    private DataSource simpleDataSource;

    @Test
    public void testDataSource() {
        Assert.assertTrue(simpleDataSource instanceof HikariDataSource);
    }
}