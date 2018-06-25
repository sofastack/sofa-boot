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
package com.alipay.sofa.healthcheck.core;

import com.alipay.sofa.healthcheck.bean.ErrorBean;
import com.alipay.sofa.healthcheck.util.BaseHealthCheckTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author liangen
 * @version $Id: SpringContextCheckProcessorTest.java, v 0.1 2018年03月12日 下午2:38 liangen Exp $
 */
public class SpringContextCheckProcessorTest extends BaseHealthCheckTest {

    private final SpringContextCheckProcessor springContextCheckProcessor = new SpringContextCheckProcessor();

    @Configuration
    static class ErrorBeanConfiguration {

        @Bean
        public ErrorBean errorBean() {
            return new ErrorBean();
        }
    }

    @Test
    public void testSpringContextCheck() {
        try {
            this.applicationContext.register(ErrorBeanConfiguration.class);
            this.applicationContext.refresh();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        boolean result = springContextCheckProcessor.springContextCheck();
        Assert.assertFalse(result);
    }
}