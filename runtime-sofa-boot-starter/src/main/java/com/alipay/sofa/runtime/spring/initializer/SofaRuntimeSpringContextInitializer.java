/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:

 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spring.initializer;

import com.alipay.boot.sofarpc.configuration.Slite2Configuration;
import com.alipay.sofa.common.log.Constants;
import com.alipay.sofa.runtime.initializer.SofaFrameworkInitializer;
import com.alipay.sofa.runtime.spi.log.SofaRuntimeLoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author xuanbei 18/3/13
 */
public class SofaRuntimeSpringContextInitializer
                                                implements
                                                ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (Slite2Configuration.getEnvironment() == null) {
            Slite2Configuration.setEnvironment(applicationContext.getEnvironment());
        }
        initLogger();
        SofaFrameworkInitializer.initialize(Slite2Configuration.getAppName(), applicationContext);
    }

    private void initLogger() {
        // init logging.path argument
        if (Slite2Configuration.containsKey(Constants.LOG_PATH)) {
            System.setProperty(Constants.LOG_PATH,
                Slite2Configuration.getProperty(Constants.LOG_PATH));
        }

        // init logging.level.com.alipay.sofa.runtime argument
        String runtimeLogLevelKey = Constants.LOG_LEVEL_PREFIX
            + SofaRuntimeLoggerFactory.SOFA_RUNTIME_LOG_SPACE;
        String runtimeLogLevelValue = Slite2Configuration.getProperty(runtimeLogLevelKey);
        if (runtimeLogLevelValue != null) {
            System.setProperty(runtimeLogLevelKey, runtimeLogLevelValue);
        }

        // init file.encoding
        String fileEncoding = Slite2Configuration.getProperty(Constants.LOG_ENCODING_PROP_KEY);
        if (fileEncoding != null) {
            System.setProperty(Constants.LOG_ENCODING_PROP_KEY,
                fileEncoding);
        }
    }
}
