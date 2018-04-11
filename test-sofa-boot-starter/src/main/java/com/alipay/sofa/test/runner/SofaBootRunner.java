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
package com.alipay.sofa.test.runner;

/**
 * Corresponding to {@linkplain org.springframework.test.context.junit4.SpringRunner}
 *
 * @author qilong.zql
 * @since 2.3.0
 */
public class SofaBootRunner extends SofaJUnit4Runner {

    private static final String DEFAULT_SPRING_BOOT_RUNNER = "org.springframework.test.context.junit4.SpringRunner";

    private static final String SOFA_ARK_BOOT_RUNNER       = "com.alipay.sofa.ark.springboot.runner.ArkBootRunner";

    public SofaBootRunner(Class<?> klazz) {
        super(klazz);
    }

    @Override
    public String getArkModeRunner() {
        return SOFA_ARK_BOOT_RUNNER;
    }

    @Override
    public String getDefaultRunner() {
        return DEFAULT_SPRING_BOOT_RUNNER;
    }
}