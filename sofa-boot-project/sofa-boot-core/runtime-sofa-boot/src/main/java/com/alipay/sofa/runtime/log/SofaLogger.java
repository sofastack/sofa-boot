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
package com.alipay.sofa.runtime.log;

import org.slf4j.Logger;

/**
 * @author xuanbei 18/2/28
 */
public class SofaLogger {
    /** SOFA Default Logger */
    private static final Logger DEFAULT_LOG = RuntimeLoggerFactory.getLogger("com.alipay.sofa");

    public static boolean isDebugEnabled() {
        return DEFAULT_LOG.isDebugEnabled();
    }

    public static void debug(String msg) {
        DEFAULT_LOG.debug(msg);
    }

    public static void debug(String format, Object arg) {
        DEFAULT_LOG.debug(format, arg);
    }

    public static void debug(String format, Object arg1, Object arg2) {
        DEFAULT_LOG.debug(format, arg1, arg2);
    }

    public static void debug(String format, Object... arguments) {
        DEFAULT_LOG.debug(format, arguments);
    }

    public static void debug(String msg, Throwable t) {
        DEFAULT_LOG.debug(msg, t);
    }

    public static boolean isInfoEnabled() {
        return DEFAULT_LOG.isInfoEnabled();
    }

    public static void info(String msg) {
        DEFAULT_LOG.info(msg);
    }

    public static void info(String format, Object arg) {
        DEFAULT_LOG.info(format, arg);
    }

    public static void info(String format, Object arg1, Object arg2) {
        DEFAULT_LOG.info(format, arg1, arg2);
    }

    public static void info(String format, Object... arguments) {
        DEFAULT_LOG.info(format, arguments);
    }

    public static void info(String msg, Throwable t) {
        DEFAULT_LOG.info(msg, t);
    }

    public static boolean isWarnEnabled() {
        return DEFAULT_LOG.isWarnEnabled();
    }

    public static void warn(String msg) {
        DEFAULT_LOG.warn(msg);
    }

    public static void warn(String format, Object arg) {
        DEFAULT_LOG.warn(format, arg);
    }

    public static void warn(String format, Object... arguments) {
        DEFAULT_LOG.warn(format, arguments);
    }

    public static void warn(String format, Object arg1, Object arg2) {
        DEFAULT_LOG.warn(format, arg1, arg2);
    }

    public static void warn(String msg, Throwable t) {
        DEFAULT_LOG.warn(msg, t);
    }

    public static boolean isErrorEnabled() {
        return DEFAULT_LOG.isErrorEnabled();
    }

    public static void error(String msg) {
        DEFAULT_LOG.error(msg);
    }

    public static void error(String format, Object arg) {
        DEFAULT_LOG.error(format, arg);
    }

    public static void error(String format, Object arg1, Object arg2) {
        DEFAULT_LOG.error(format, arg1, arg2);
    }

    public static void error(String format, Object... arguments) {
        DEFAULT_LOG.error(format, arguments);
    }

    public static void error(String msg, Throwable t) {
        DEFAULT_LOG.error(msg, t);
    }
}
