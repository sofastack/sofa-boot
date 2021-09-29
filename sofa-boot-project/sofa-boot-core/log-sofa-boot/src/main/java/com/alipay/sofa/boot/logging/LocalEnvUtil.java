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
package com.alipay.sofa.boot.logging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/14
 */
@Component
public class LocalEnvUtil {
    public final String    SOFA_LOCAL_ENV       = "sofa.local.env";
    private static boolean SOFA_LOCAL_ENV_VALUE = false;

    private static boolean LOCAL_ENV;

    @Value("${" + SOFA_LOCAL_ENV + "}")
    public void setSofaLocalEnvValue(boolean value) {
        LocalEnvUtil.SOFA_LOCAL_ENV_VALUE = value;
    }

    static {
        // Currently supports for detection of IDEA IntelliJ
        try {
            Class.forName("com.intellij.rt.execution.application.AppMainV2");
            LOCAL_ENV = true;
        } catch (ClassNotFoundException e) {
            LOCAL_ENV = false;
        }
    }

    public static boolean isLocalEnv() {
        if (SOFA_LOCAL_ENV_VALUE) {
            return true;
        }
        return LOCAL_ENV;
    }
}
