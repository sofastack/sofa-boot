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

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/14
 */
public class LocalEnvUtil {
    private static boolean LOCAL_ENV;

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
        return LOCAL_ENV;
    }
}
