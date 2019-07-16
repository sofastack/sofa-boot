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
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alipay.common.tracer.core.appender.TracerLogRootDaemon;
import com.alipay.common.tracer.core.configuration.SofaTracerConfiguration;
import com.alipay.common.tracer.core.reporter.digest.manager.SofaTracerDigestReporterAsyncManager;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterCycleTimesManager;
import com.alipay.common.tracer.core.reporter.stat.manager.SofaTracerStatisticReporterManager;
import com.alipay.sofa.boot.listener.SofaBootstrapRunListener;
import com.alipay.sofa.tracer.plugins.springmvc.SpringMvcTracer;

/**
 * referenced document: http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
 *
 * @author yangguanchao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringBootWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.properties")
public abstract class AbstractTestBase {

    protected static String    logDirectoryPath = TracerLogRootDaemon.LOG_FILE_DIR;

    @LocalServerPort
    private int                definedPort;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    protected String           urlHttpPrefix;

    @BeforeClass
    public static void beforeClass() throws IOException, NoSuchFieldException,
                                    IllegalAccessException {
        cleanLogDirectory();
        clearSpringCloudMark();
    }

    @Before
    public void setUp() throws Exception {
        urlHttpPrefix = "http://localhost:" + definedPort;
        reflectSpringMVCClear();
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

    protected static void reflectSpringMVCClear() throws NoSuchFieldException,
                                                 IllegalAccessException {
        Field field = SpringMvcTracer.class.getDeclaredField("springMvcTracer");
        field.setAccessible(true);
        field.set(null, null);
        //clear digest
        Field fieldAsync = SofaTracerDigestReporterAsyncManager.class
            .getDeclaredField("asyncCommonDigestAppenderManager");
        fieldAsync.setAccessible(true);
        fieldAsync.set(null, null);

        // clear stat
        SofaTracerStatisticReporterManager statReporterManager = SofaTracerStatisticReporterCycleTimesManager
            .getSofaTracerStatisticReporterManager(1l);
        Field fieldStat = SofaTracerStatisticReporterManager.class
            .getDeclaredField("statReporters");
        fieldStat.setAccessible(true);
        fieldStat.set(statReporterManager, new ConcurrentHashMap<>());
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
