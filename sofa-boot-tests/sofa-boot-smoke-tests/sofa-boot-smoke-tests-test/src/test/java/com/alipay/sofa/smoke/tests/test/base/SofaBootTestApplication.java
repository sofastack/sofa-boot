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
package com.alipay.sofa.smoke.tests.test.base;

import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author pengym
 * @version SofaBootTestApplication.java, v 0.1 2023年08月08日 11:24 pengym
 */
@SpringBootApplication(scanBasePackages = {
        "com.alipay.sofa.smoke.tests.integration.test",
        "com.alipay.sofa.test"
})
public class SofaBootTestApplication {
    private static final Logger LOGGER = SofaBootLoggerFactory.getLogger(SofaBootTestApplication.class);

    public static void main(String[] args) {
        try {
            SpringApplication.run(SofaBootTestApplication.class, args);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("SOFABoot Test Application Start!!!");
            }
        } catch (Throwable t) {
            LOGGER.error("SOFABoot Test App Start Fail!!! More logs can be found on 1) logs/sofa-runtime/common-error.log"
                    + " 2) logs/spring/spring.log 3) logs/health-check/common-error.log", t);
            throw t;
        }
    }
}
