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
//package com.alipay.sofa.rpc.boot.test;
//
//import com.alipay.sofa.boot.actuator.health.core.HealthCheckExecutor;
//import com.alipay.sofa.runtime.configure.SofaRuntimeConfigurationProperties;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.alipay.sofa.boot.actuator.health.AfterReadinessCheckCallbackProcessor;
//import com.alipay.sofa.boot.actuator.health.HealthCheckerProcessor;
//import com.alipay.sofa.boot.actuator.health.HealthCheckProperties;
//import com.alipay.sofa.boot.actuator.health.HealthIndicatorProcessor;
//import com.alipay.sofa.boot.actuator.health.ReadinessCheckListener;
//import org.springframework.core.env.Environment;
//
///**
// * @author qilong.zql
// * @since 3.2.0
// */
//@Configuration(proxyBeanMethods = false)
//@EnableConfigurationProperties({ HealthCheckProperties.class,
//                                SofaRuntimeConfigurationProperties.class })
//public class HealthcheckTestConfiguration {
//    @Bean
//    public ReadinessCheckListener readinessCheckListener(Environment environment,
//                                                         HealthCheckerProcessor healthCheckerProcessor,
//                                                         HealthIndicatorProcessor healthIndicatorProcessor,
//                                                         AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor,
//                                                         SofaRuntimeConfigurationProperties sofaRuntimeConfigurationProperties,
//                                                         HealthCheckProperties healthCheckProperties) {
//        return new ReadinessCheckListener(environment, healthCheckerProcessor,
//            healthIndicatorProcessor, afterReadinessCheckCallbackProcessor,
//            sofaRuntimeConfigurationProperties, healthCheckProperties);
//    }
//
//    @Bean
//    public HealthCheckerProcessor healthCheckerProcessor(HealthCheckProperties healthCheckProperties,
//                                                         HealthCheckExecutor healthCheckExecutor) {
//        return new HealthCheckerProcessor(healthCheckProperties, healthCheckExecutor);
//    }
//
//    @Bean
//    public HealthIndicatorProcessor healthIndicatorProcessor(HealthCheckProperties properties,
//                                                             HealthCheckExecutor healthCheckExecutor) {
//        return new HealthIndicatorProcessor(properties, healthCheckExecutor);
//    }
//
//    @Bean
//    public AfterReadinessCheckCallbackProcessor afterReadinessCheckCallbackProcessor() {
//        return new AfterReadinessCheckCallbackProcessor();
//    }
//
//    @Bean
//    public HealthCheckExecutor healthCheckExecutor(HealthCheckProperties properties) {
//        return new HealthCheckExecutor(properties);
//    }
//}
