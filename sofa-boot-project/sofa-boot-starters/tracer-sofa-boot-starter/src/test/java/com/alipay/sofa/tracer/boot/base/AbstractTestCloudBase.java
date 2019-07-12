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
package com.alipay.sofa.tracer.boot.base;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alipay.common.tracer.core.appender.TracerLogRootDaemon;
import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.sofa.boot.listener.SofaBootstrapRunListener;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/3/28 2:40 PM
 * @since:
 **/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(locations = "classpath:application.properties")
public abstract class AbstractTestCloudBase {
    protected static String logDirectoryPath = TracerLogRootDaemon.LOG_FILE_DIR;

    @BeforeClass
    public static void beforeClass() throws IOException, NoSuchFieldException,
                                    IllegalAccessException {
        cleanLogDirectory();
        clearSpringCloudMark();
    }

    /**
     * clear directory
     *
     * @throws IOException
     */
    public static void cleanLogDirectory() throws IOException {
        File file = new File(logDirectoryPath);
        if (file.exists()) {
            FileUtils.cleanDirectory(file);
        }
    }

    protected static File customFileLog(String fileName) {
        return new File(logDirectoryPath + File.separator + fileName);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        clearTracerProperties();
        clearSpringCloudMark();
    }

    private static void clearTracerProperties() throws Exception {
        Field propertiesField = SofaTracerConfiguration.class.getDeclaredField("properties");
        propertiesField.setAccessible(true);
        propertiesField.set(null, new ConcurrentHashMap<>());
    }

    private static void clearSpringCloudMark() throws NoSuchFieldException, IllegalAccessException {
        Field executed = SofaBootstrapRunListener.class.getDeclaredField("executed");
        executed.setAccessible(true);
        if (Modifier.isStatic(executed.getModifiers())) {
            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
            executed.set(null, atomicBoolean);
        }
    }
}
