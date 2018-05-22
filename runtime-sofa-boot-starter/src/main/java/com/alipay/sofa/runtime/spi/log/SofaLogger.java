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
package com.alipay.sofa.runtime.spi.log;

import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * @author xuanbei 18/2/28
 */
public class SofaLogger {
    /** SOFA Default Logger */
    private static final Logger DEFAULT_LOG = SofaRuntimeLoggerFactory.getLogger("com.alipay.sofa");

    public static void debug(String format, Object... args) {
        if (DEFAULT_LOG.isDebugEnabled()) {
            DEFAULT_LOG.debug(getMessage(format, args));
        }
    }

    public static void info(String format, Object... args) {
        if (DEFAULT_LOG.isInfoEnabled()) {
            DEFAULT_LOG.info(getMessage(format, args));
        }
    }

    public static void warn(String format, Object... args) {
        if (DEFAULT_LOG.isWarnEnabled()) {
            DEFAULT_LOG.warn(getMessage(format, args));
        }
    }

    public static void error(String format, Object... args) {
        DEFAULT_LOG.error(getMessage(format, args));
    }

    public static void error(Throwable t, String format, Object... args) {
        DEFAULT_LOG.error(getMessage(format, args), t);
    }

    private static String getMessage(String format, Object... args) {
        return MessageFormat.format(format, args);
    }

    public static boolean isDebugEnabled() {
        return DEFAULT_LOG.isDebugEnabled();
    }
}
