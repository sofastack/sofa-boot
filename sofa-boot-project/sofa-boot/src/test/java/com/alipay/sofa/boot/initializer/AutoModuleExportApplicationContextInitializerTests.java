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
package com.alipay.sofa.boot.initializer;

import com.alipay.sofa.boot.Initializer.AutoModuleExportApplicationContextInitializer;
import com.alipay.sofa.boot.util.ModuleUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

/**
 * @author huazhongming
 * @since 4.4.0
 */
public class AutoModuleExportApplicationContextInitializerTests {

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
            .withInitializer(new AutoModuleExportApplicationContextInitializer());
    }

    @Test
    void jdkDefaultTrue(){

        try (MockedStatic<ModuleUtil> mockedStatic = mockStatic(ModuleUtil.class)) {
            contextRunner.withPropertyValues().run(applicationContext -> {});
            mockedStatic.verify(ModuleUtil::exportAllJDKModulePackageToAll, times(1));
        }
    }

    @Test
    void allDefaultFalse(){
        try (MockedStatic<ModuleUtil> mockedStatic = mockStatic(ModuleUtil.class)) {
            contextRunner.withPropertyValues().run(applicationContext -> {});
            mockedStatic.verify(ModuleUtil::exportAllModulePackageToAll, times(0));
        }
    }

    @Test
    void jdkDisable(){

        try (MockedStatic<ModuleUtil> mockedStatic = mockStatic(ModuleUtil.class)) {
            contextRunner.withPropertyValues("sofa.boot.auto.module.export.jdk.enable=false").run(applicationContext -> {});
            mockedStatic.verify(ModuleUtil::exportAllJDKModulePackageToAll, times(0));
        }
    }

    @Test
    void allEnable(){
        try (MockedStatic<ModuleUtil> mockedStatic = mockStatic(ModuleUtil.class)) {
            contextRunner.withPropertyValues("sofa.boot.auto.module.export.all.enable=true").run(applicationContext -> {});
            mockedStatic.verify(ModuleUtil::exportAllModulePackageToAll, times(1));
        }
    }
}
