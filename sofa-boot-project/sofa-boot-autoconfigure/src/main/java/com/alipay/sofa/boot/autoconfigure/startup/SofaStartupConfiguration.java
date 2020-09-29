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
package com.alipay.sofa.boot.autoconfigure.startup;

import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import com.alipay.sofa.startup.SofaStartupContext;
import com.alipay.sofa.startup.spring.IsleSpringContextAwarer;
import com.alipay.sofa.startup.spring.SpringContextAwarer;
import com.alipay.sofa.startup.stage.StartupSpringContextInstallStage;
import com.alipay.sofa.startup.webserver.StartupJettyServletWebServerFactory;
import com.alipay.sofa.startup.webserver.StartupTomcatServletWebServerFactory;
import com.alipay.sofa.startup.webserver.StartupUndertowServletWebServerFactory;
import io.undertow.Undertow;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Loader;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * @author: Zhijie
 * @since: 2020/7/8
 */
public class SofaStartupConfiguration {

    @Configuration
    @ConditionalOnClass({ Servlet.class, Tomcat.class, UpgradeProtocol.class,
            SofaStartupContext.class })
    @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class StartupTomcat {

        @Bean
        public StartupTomcatServletWebServerFactory startupTomcatServletWebServerFactory() {
            return new StartupTomcatServletWebServerFactory();
        }
    }

    @Configuration
    @ConditionalOnClass({ Servlet.class, Server.class, Loader.class, WebAppContext.class,
            SofaStartupContext.class })
    @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class StartupJetty {

        @Bean
        public StartupJettyServletWebServerFactory startupJettyServletWebServerFactory() {
            return new StartupJettyServletWebServerFactory();
        }
    }

    @Configuration
    @ConditionalOnClass({ Servlet.class, Undertow.class, SslClientAuthMode.class,
            SofaStartupContext.class })
    @ConditionalOnMissingBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
    static class StartupUndertow {
        @Bean
        public StartupUndertowServletWebServerFactory startupUndertowServletWebServerFactory() {
            return new StartupUndertowServletWebServerFactory();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(value = SpringContextAwarer.class, search = SearchStrategy.CURRENT)
    @ConditionalOnClass(SofaStartupContext.class)
    @ConditionalOnMissingClass("com.alipay.sofa.isle.ApplicationRuntimeModel")
    static class SpringContextAware {

        @Bean
        public SpringContextAwarer springContextAwarer() {
            return new SpringContextAwarer();
        }
    }

    @Configuration
    @ConditionalOnMissingBean(value = SpringContextAwarer.class, search = SearchStrategy.CURRENT)
    @ConditionalOnClass({ ApplicationRuntimeModel.class, SofaStartupContext.class })
    static class IsleSpringContextAware {

        @Bean
        public IsleSpringContextAwarer isleSpringContextAwarer(StartupSpringContextInstallStage startupSpringContextInstallStage) {
            return new IsleSpringContextAwarer(startupSpringContextInstallStage);
        }
    }

    @Configuration
    @AutoConfigureBefore(SofaModuleAutoConfiguration.class)
    @ConditionalOnClass({ ApplicationRuntimeModel.class, SofaStartupContext.class })
    @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
    static class InstallStage {

        @Bean
        public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext) {
            return new StartupSpringContextInstallStage(
                (AbstractApplicationContext) applicationContext);
        }
    }
}
