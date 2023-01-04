package com.alipay.sofa.boot.actuator.autoconfigure.startup;

import com.alipay.sofa.boot.actuator.startup.StartupEndPoint;
import com.alipay.sofa.boot.actuator.startup.StartupReporter;
import com.alipay.sofa.boot.actuator.startup.isle.StartupModelCreatingStage;
import com.alipay.sofa.boot.actuator.startup.isle.StartupSpringContextInstallStage;
import com.alipay.sofa.boot.autoconfigure.isle.SofaModuleAutoConfiguration;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.profile.SofaModuleProfileChecker;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.stage.ModelCreatingStage;
import com.alipay.sofa.isle.stage.SpringContextInstallStage;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for startup isle components.
 *
 * @author huzijie
 * @version StartupIsleAutoConfiguration.java, v 0.1 2023年01月04日 2:40 PM huzijie Exp $
 */
@AutoConfiguration(before = SofaModuleAutoConfiguration.class)
@ConditionalOnClass({ApplicationRuntimeModel.class})
@ConditionalOnProperty(value = "sofa.boot.isle.enable", matchIfMissing = true)
@ConditionalOnAvailableEndpoint(endpoint = StartupEndPoint.class)
public class StartupIsleAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = SpringContextInstallStage.class, search = SearchStrategy.CURRENT)
    public StartupSpringContextInstallStage startupSpringContextInstallStage(ApplicationContext applicationContext,
                                                                             SofaModuleProperties sofaModuleProperties,
                                                                             StartupReporter startupReporter) {
        return new StartupSpringContextInstallStage(
                (AbstractApplicationContext) applicationContext, sofaModuleProperties,
                startupReporter);
    }

    @Bean
    @ConditionalOnMissingBean(value = ModelCreatingStage.class, search = SearchStrategy.CURRENT)
    public StartupModelCreatingStage startupModelCreatingStage(ApplicationContext applicationContext,
                                                               SofaModuleProperties sofaModuleProperties,
                                                               SofaModuleProfileChecker sofaModuleProfileChecker,
                                                               StartupReporter startupReporter) {
        return new StartupModelCreatingStage((AbstractApplicationContext) applicationContext,
                sofaModuleProperties, sofaModuleProfileChecker, startupReporter);
    }
}
