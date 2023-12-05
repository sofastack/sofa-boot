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
package com.alipay.sofa.smoke.tests.isle;

import com.alipay.sofa.boot.isle.stage.SpringContextInstallStage;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.test.context.TestPropertySource;

/**
 * Integration tests for {@link SpringContextInstallStage} in parallel when use virtual threads.
 *
 * @author huzijie
 * @version ParallelVirtualThreadSpringContextInstallStageTests.java, v 0.1 2023年11月24日 4:03 PM huzijie Exp $
 */
@TestPropertySource(properties = { "sofa.boot.startup.threads.virtual.enabled=true" })
@EnabledOnJre(JRE.JAVA_21)
public class ParallelVirtualThreadSpringContextInstallStageTests extends
                                                                ParallelSpringContextInstallStageTests {
}
