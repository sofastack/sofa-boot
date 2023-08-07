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
package com.alipay.sofa.boot;

/**
 * Exposes the SOFABoot version.
 *
 * @author huzijie
 * @version SofaBootVersion.java, v 0.1 2023年08月03日 4:36 PM huzijie Exp $
 */
public final class SofaBootVersion {

    private SofaBootVersion() {
    }

    /**
     * Return the full version string of the present SOFABoot codebase.
     * @return the version of SOFABoot
     */
    public static String getVersion() {
        return "3.20.0";
    }
}
