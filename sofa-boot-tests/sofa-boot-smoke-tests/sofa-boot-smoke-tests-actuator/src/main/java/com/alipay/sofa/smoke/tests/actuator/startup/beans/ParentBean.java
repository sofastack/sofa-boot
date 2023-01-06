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
package com.alipay.sofa.smoke.tests.actuator.startup.beans;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author huzijie
 * @version FatherBean.java, v 0.1 2021年01月04日 9:28 下午 huzijie Exp $
 */
public class ParentBean implements InitializingBean {

    public static final int PARENT_INIT_TIME = 30;

    @Autowired
    private ChildBean       childBean;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(PARENT_INIT_TIME);
    }
}
