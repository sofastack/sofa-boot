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
package com.alipay.sofa.runtime.util;

/**
 * @author qilong.zql
 * @since 2.4.1
 */
public class StateMessage {
    private static String factoryMessage = "UNDO";

    private static String configMessage  = "UNDO";

    public static void setFactoryMessage(String factoryMessage) {
        StateMessage.factoryMessage = factoryMessage;
    }

    public static String getFactoryMessage() {
        return StateMessage.factoryMessage;
    }

    public static String getConfigMessage() {
        return configMessage;
    }

    public static void setConfigMessage(String configMessage) {
        StateMessage.configMessage = configMessage;
    }
}