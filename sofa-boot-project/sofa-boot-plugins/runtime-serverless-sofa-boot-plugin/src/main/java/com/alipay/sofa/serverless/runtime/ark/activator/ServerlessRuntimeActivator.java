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
package com.alipay.sofa.serverless.runtime.ark.activator;

import com.alipay.sofa.ark.api.ArkConfigs;
import com.alipay.sofa.ark.common.log.ArkLoggerFactory;
import com.alipay.sofa.ark.spi.model.PluginContext;
import com.alipay.sofa.ark.spi.service.PluginActivator;
import com.alipay.sofa.ark.spi.service.event.EventAdminService;
import com.alipay.sofa.ark.spi.web.EmbeddedServerService;
import com.alipay.sofa.ark.web.embed.tomcat.EmbeddedServerServiceImpl;
import com.alipay.sofa.runtime.SofaBizHealthCheckEventHandler;
import com.alipay.sofa.runtime.SofaBizUninstallEventHandler;
import com.alipay.sofa.runtime.invoke.DynamicJvmServiceProxyFinder;
import com.alipay.sofa.runtime.spring.FinishStartupEventHandler;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author guolei.sgl
 * @author qilong.zql
 * @since 3.4.7
 */
public class ServerlessRuntimeActivator implements PluginActivator {
    private static final String   LOGGING_PATH          = "logging.path";
    private EmbeddedServerService embeddedServerService = new EmbeddedServerServiceImpl();

    @Override
    public void start(PluginContext context) {
        try {
            LoggerContext loggerContext = initPluginLogger();
            Logger logger = loggerContext.getLogger(ServerlessRuntimeActivator.class.getName());
            registerEventHandler(context);
            context.publishService(DynamicJvmServiceProxyFinder.class,
                DynamicJvmServiceProxyFinder.getDynamicJvmServiceProxyFinder());
            context.publishService(EmbeddedServerService.class, embeddedServerService);
            logger.info("start serverless runtime plugin success.");
        } catch (Throwable t) {
            new RuntimeException("start serverless runtime plugin error", t);
        }
    }

    private LoggerContext initPluginLogger() throws Exception {
        // set Thread Context, reuse ark config
        ThreadContext.put(
            LOGGING_PATH,
            ArkConfigs.getStringValue(LOGGING_PATH, System.getProperty("user.home")
                                                    + File.separator + "logs"));
        for (String key : ArkConfigs.keySet()) {
            ThreadContext.put(key, ArkConfigs.getStringValue(key));
        }

        // initialize logger context
        List<URI> configurations = new ArrayList<>();
        ClassLoader pluginClassLoader = ServerlessRuntimeActivator.class.getClassLoader();
        configurations.add(pluginClassLoader.getResource("serverless-runtime-log4j2.xml").toURI());
        Enumeration<URL> log4j2ConfigurationFragments = pluginClassLoader
            .getResources("META-INF/sofa-log4j2-configuration-fragment.xml");
        while (log4j2ConfigurationFragments.hasMoreElements()) {
            configurations.add(log4j2ConfigurationFragments.nextElement().toURI());
        }
        LoggerContext loggerContext = Configurator.initialize("serverless-runtime-log4j2",
            pluginClassLoader, configurations, null);
        return loggerContext;
    }

    private void registerEventHandler(final PluginContext context) {
        EventAdminService eventAdminService = context.referenceService(EventAdminService.class)
            .getService();
        eventAdminService.register(new SofaBizUninstallEventHandler());
        eventAdminService.register(new SofaBizHealthCheckEventHandler());
        eventAdminService.register(new FinishStartupEventHandler());
    }

    @Override
    public void stop(PluginContext context) {
        Tomcat webServer = null;
        if (embeddedServerService.getEmbedServer() instanceof Tomcat) {
            webServer = (Tomcat) embeddedServerService.getEmbedServer();
        }
        if (webServer != null) {
            try {
                webServer.destroy();
            } catch (Exception ex) {
                ArkLoggerFactory.getDefaultLogger().error("Unable to stop embedded Tomcat", ex);
            }
        }
    }
}