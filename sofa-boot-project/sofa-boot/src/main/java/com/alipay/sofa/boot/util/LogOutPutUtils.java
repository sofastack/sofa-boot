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
package com.alipay.sofa.boot.util;

import com.alipay.sofa.common.log.CommonLoggingConfigurations;
import com.alipay.sofa.common.log.Constants;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Util to enable console log for specific loggers.
 *
 * @author huzijie
 * @version LogOutPutUtils.java, v 0.1 2023年02月22日 5:53 PM huzijie Exp $
 */
public class LogOutPutUtils {

    public static void openOutPutForLoggers(String... loggers) {
        openOutPutForLoggers(Arrays.asList(loggers));
    }

    public static void openOutPutForLoggers(Class<?> ... classes) {
        openOutPutForLoggers(Arrays.stream(classes).map(Class::getName).collect(Collectors.toList()));
    }

    public static void openOutPutForLoggers(Collection<String> loggers) {
        CommonLoggingConfigurations.loadExternalConfiguration(
            Constants.SOFA_MIDDLEWARE_ALL_LOG_CONSOLE_SWITCH, "true");
        CommonLoggingConfigurations.addAllConsoleLogger(new HashSet<>(loggers));
    }
}
