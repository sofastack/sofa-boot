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
package com.alipay.sofa.infra.log.base;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.LoggerSpaceManager;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;

/**
 * AbstractTestBase
 * <p/>
 * Created by yangguanchao on 18/01/04.
 */
public abstract class AbstractTestBase {

    public static final String restLogLevel = Constants.LOG_LEVEL_PREFIX
                                              + InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;

    public void before() throws Exception {
        System.getProperties().put("logging.path", "./logs");
    }

    public void after() throws Exception {

        System.err.println("\n " + Constants.LOG_ENCODING_PROP_KEY + " : "
                           + System.getProperty("file.encoding"));
        System.err.println("\n " + Constants.LOG_PATH + " : " + System.getProperty("logging.path"));
        String restLogLevel = Constants.LOG_LEVEL_PREFIX
                              + InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE;
        System.err.println("\n " + restLogLevel + " : " + System.getProperty(restLogLevel));

        System.clearProperty(Constants.LOG_PATH);
        System.clearProperty(restLogLevel);
        //关闭禁用开关
        System.clearProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        LoggerSpaceManager
            .removeILoggerFactoryBySpaceName(InfraHealthCheckLoggerFactory.INFRASTRUCTURE_LOG_SPACE);
    }
}
