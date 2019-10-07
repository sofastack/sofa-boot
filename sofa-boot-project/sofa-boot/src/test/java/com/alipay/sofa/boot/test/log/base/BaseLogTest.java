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
package com.alipay.sofa.boot.test.log.base;

import java.lang.reflect.Field;
import java.util.Map;

import com.alipay.sofa.boot.log.InfraLoggerFactory;
import com.alipay.sofa.boot.logging.CommonLoggingApplicationListener;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.MultiAppLoggerSpaceManager;
import com.alipay.sofa.common.log.SpaceId;
import com.alipay.sofa.common.log.env.LogEnvUtils;

/**
 * LogTestBase
 * <p/>
 * Created by yangguanchao on 18/01/04.
 */
public abstract class BaseLogTest {

    public static final String restLogLevel = Constants.LOG_LEVEL_PREFIX
                                              + InfraLoggerFactory.INFRASTRUCTURE_LOG_SPACE;
    private static Map         SPACES_MAP;
    private static Field       globalSystemProperties;

    static {
        try {
            Field field = MultiAppLoggerSpaceManager.class.getDeclaredField("SPACES_MAP");
            field.setAccessible(true);
            SPACES_MAP = (Map) field.get(null);

            globalSystemProperties = LogEnvUtils.class.getDeclaredField("globalSystemProperties");
            globalSystemProperties.setAccessible(true);
        } catch (Throwable throwable) {
            // ignore
        }
    }

    public void before() throws Exception {
        SPACES_MAP.remove(new SpaceId(InfraLoggerFactory.INFRASTRUCTURE_LOG_SPACE));
        new CommonLoggingApplicationListener().setReInitialize(true);
    }

    public void after() throws Exception {
        //关闭禁用开关
        System.clearProperty(Constants.LOGBACK_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J2_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
        System.clearProperty(Constants.LOG4J_MIDDLEWARE_LOG_DISABLE_PROP_KEY);
    }
}
