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
///*
// * Licensed to the Apache Software Foundation (ASF) under one or more
// * contributor license agreements.  See the NOTICE file distributed with
// * this work for additional information regarding copyright ownership.
// * The ASF licenses this file to You under the Apache License, Version 2.0
// * (the "License"); you may not use this file except in compliance with
// * the License.  You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package com.alipay.sofa.runtime.test;
//
//import org.aspectj.lang.JoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.aspectj.lang.annotation.Pointcut;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import com.alipay.sofa.runtime.api.annotation.SofaReference;
//import com.alipay.sofa.runtime.api.annotation.SofaService;
//import com.alipay.sofa.runtime.test.beans.ClientFactoryAwareBean;
//import com.alipay.sofa.runtime.test.beans.StateAspect;
//import com.alipay.sofa.runtime.test.beans.facade.SampleService;
//import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
//import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
//
///**
// * @author qilong.zql
// * @since 3.2.0
// */
//@RunWith(SpringRunner.class)
//@TestPropertySource(properties = "spring.application.name=ServiceAopTest")
//@SpringBootTest
//public class ServiceAopTest {
//
//    @SofaReference(uniqueId = "beanMethod")
//    private SampleService sampleService;
//
//    @SofaReference(uniqueId = "clientFactory")
//    private SampleService otherService;
//
//    @Test
//    public void testServiceAop() {
//        Assert.assertNotNull(sampleService);
//        Assert.assertFalse(StateAspect.isAspectInvoked());
//        sampleService.service();
//        Assert.assertTrue(StateAspect.isAspectInvoked());
//
//        Assert.assertNotNull(otherService);
//        Assert.assertFalse(StateAspect.isAspectInvoked());
//        otherService.service();
//        Assert.assertFalse(StateAspect.isAspectInvoked());
//    }
//
//    @Configuration(proxyBeanMethods = false)
//    @Import({ AopAutoConfiguration.class, RuntimeConfiguration.class })
//    @Aspect
//    static class ServiceAopTestConfiguration {
//        @Bean
//        @SofaService(uniqueId = "beanMethod")
//        public SampleService sampleService() {
//            return new DefaultSampleService();
//        }
//
//        @Bean
//        public ClientFactoryAwareBean clientFactoryAwareBean() {
//            return new ClientFactoryAwareBean();
//        }
//
//        @Pointcut("execution(* com.alipay.sofa.runtime.test..*Service.*(..))")
//        public void pointCut() {
//        }
//
//        @Before("pointCut()")
//        public void doBefore(JoinPoint joinPoint) {
//            StateAspect.setAspectInvoked();
//        }
//    }
//}