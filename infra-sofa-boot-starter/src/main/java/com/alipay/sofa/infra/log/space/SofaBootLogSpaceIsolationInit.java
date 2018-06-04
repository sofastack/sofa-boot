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
package com.alipay.sofa.infra.log.space;

import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.common.log.ReportUtil;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * SofaBootLogSpaceIsolationInit
 *
 * @author yangguanchao
 * @since 2018/04/09
 */
public class SofaBootLogSpaceIsolationInit {

    /***
     * Initializer for SOFA Boot Log Space Isolation
     * @param environment   Application Context Environment
     * @param runtimeLogLevelKey sofa-common-tools Runtime Log Level for every SOFABoot starters or plugin,such as logging.level.com.alipay.sofa.runtime
     */
    public static void initSofaBootLogger(Environment environment, String runtimeLogLevelKey) {
        // init logging.path argument
        String loggingPath = environment.getProperty(Constants.LOG_PATH);
        if (!StringUtils.isEmpty(loggingPath)) {
            System.setProperty(Constants.LOG_PATH, environment.getProperty(Constants.LOG_PATH));
            ReportUtil.report("Actual " + Constants.LOG_PATH + " is [ " + loggingPath + " ]");
        }

        //for example : init logging.level.com.alipay.sofa.runtime argument
        String runtimeLogLevelValue = environment.getProperty(runtimeLogLevelKey);
        if (runtimeLogLevelValue != null) {
            System.setProperty(runtimeLogLevelKey, runtimeLogLevelValue);
        }

        // init file.encoding
        String fileEncoding = environment.getProperty(Constants.LOG_ENCODING_PROP_KEY);
        if (!StringUtils.isEmpty(fileEncoding)) {
            System.setProperty(Constants.LOG_ENCODING_PROP_KEY, fileEncoding);
        }
    }
}
