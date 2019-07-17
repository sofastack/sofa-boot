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
package com.alipay.sofa.boot.logging.log4j2;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.xml.XmlConfiguration;

/**
 * https://logging.apache.org/log4j/log4j-2.6.1/manual/plugins.html
 * Processed by {@literal org.apache.logging.log4j.core.config.plugins.processor.PluginProcessor}
 *
 * @author qilong.zql
 * @since 1.0.15
 */
@Plugin(name = "SOFAConfigurationFactory", category = ConfigurationFactory.CATEGORY)
@Order(Integer.MAX_VALUE)
public class SOFAConfigurationFactory extends ConfigurationFactory {

    private final String[] TYPES = { "log4j2/log-conf.xml", "log4j2/log-conf-custom.xml" };

    @Override
    protected String[] getSupportedTypes() {
        return TYPES;
    }

    @Override
    public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
        if (source != null && source != ConfigurationSource.NULL_SOURCE) {
            return loggerContext.getExternalContext() != null ? new SOFAConfiguration()
                : new XmlConfiguration(loggerContext, source);
        }
        return null;
    }

    public static final class SOFAConfiguration extends DefaultConfiguration {
        private SOFAConfiguration() {
            this.isShutdownHookEnabled = false;
            String levelName = System.getProperty(DefaultConfiguration.DEFAULT_LEVEL,
                Level.INFO.name());
            Level level = Level.valueOf(levelName);
            getRootLogger().setLevel(level != null ? level : Level.INFO);
        }
    }

}