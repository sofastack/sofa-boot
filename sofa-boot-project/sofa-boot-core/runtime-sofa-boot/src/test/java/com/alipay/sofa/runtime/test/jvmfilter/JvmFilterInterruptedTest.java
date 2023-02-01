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
//package com.alipay.sofa.runtime.test.jvmfilter;
//
//import com.alipay.sofa.runtime.filter.JvmFilterHolder;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
///**
// * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
// * Created on 2020/8/18
// */
//@SpringBootTest(classes = JvmFilterInterruptedConfiguration.class, properties = {
//                                                                                 "spring.application.name=filterTest",
//                                                                                 "com.alipay.sofa.boot.jvm-filter-enable=true" })
//public class JvmFilterInterruptedTest extends RuntimeTestBase {
//    @Autowired
//    private Service myService;
//
//    @BeforeClass
//    public static void before() {
//        JvmFilterHolder.clearJvmFilters();
//    }
//
//    @Test
//    public void test() {
//        Assert.assertEquals("interrupted", myService.say());
//        Assert.assertEquals(3, JvmFilterHolder.getJvmFilters().size());
//        Assert.assertEquals(1, JvmFilterInterruptedConfiguration.beforeCount);
//        Assert.assertEquals(0, JvmFilterInterruptedConfiguration.afterCount);
//    }
//}
