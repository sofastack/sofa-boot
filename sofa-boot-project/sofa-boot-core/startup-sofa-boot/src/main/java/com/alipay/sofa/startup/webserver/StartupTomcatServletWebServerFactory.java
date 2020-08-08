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
package com.alipay.sofa.startup.webserver;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.ServletContextInitializer;

/**
 * Wrapper for TomcatServletWebServerFactory to calculate WebServer start time cost
 *
 * @author: Zhijie
 * @since: 2020/7/8
 */
public class StartupTomcatServletWebServerFactory extends TomcatServletWebServerFactory {
    private static long beginTime = 0L;
    private static long endTime   = -1L;

    @Override
    public WebServer getWebServer(ServletContextInitializer... initializers) {
        beginTime = System.currentTimeMillis();
        WebServer webServer = super.getWebServer(initializers);
        endTime = System.currentTimeMillis();
        return webServer;
    }

    public static long getBeginTime() {
        return beginTime;
    }

    public static long getEndTime() {
        return endTime;
    }
}
