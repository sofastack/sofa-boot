package com.alipay.sofa.isle.test;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentDescriptorConfiguration;
import com.alipay.sofa.isle.deployment.impl.DefaultModuleDeploymentValidator;
import com.alipay.sofa.isle.deployment.impl.FileDeploymentDescriptor;
import com.alipay.sofa.isle.deployment.impl.JarDeploymentDescriptor;
import com.alipay.sofa.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.isle.loader.SpringContextLoader;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.boot.web.servlet.context.XmlServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/10/15
 */
public class BeanHierarchyTest {
    @Test
    public void test() throws Exception {
        ApplicationRuntimeModel application = new ApplicationRuntimeModel();
        application.setAppName(this.getClass().getName());
        application.setModuleDeploymentValidator(new DefaultModuleDeploymentValidator());

        DeploymentDescriptorConfiguration deploymentDescriptorConfiguration = new DeploymentDescriptorConfiguration(
                Collections.singletonList(SofaBootConstants.MODULE_NAME),
                Collections.singletonList(SofaBootConstants.REQUIRE_MODULE));

        Properties props = new Properties();
        props.setProperty(SofaBootConstants.MODULE_NAME, "com.alipay.module");
        File moduleDirectory = new File("target/test-classes/module/sofa-module.properties");
        DeploymentDescriptor dd = DeploymentBuilder.build(moduleDirectory.toURI().toURL(), props, deploymentDescriptorConfiguration, this.getClass().getClassLoader());
        Assert.assertTrue(dd instanceof FileDeploymentDescriptor);
        Assert.assertTrue(application.isModuleDeployment(dd));
        application.addDeployment(dd);

        refreshApplication(application);
        BeanFactory beanFactory = ((ConfigurableApplicationContext) dd.getApplicationContext()).getBeanFactory();
        for (BeanLoadCostBeanFactory.BeanNode bn: ((BeanLoadCostBeanFactory) beanFactory).getBeanLoadList()) {
            if (bn.getBeanClassName().contains("testService")) {
                Assert.assertEquals(3, bn.getChildren().size());
                for (BeanLoadCostBeanFactory.BeanNode cbn: bn.getChildren()) {
                    if (cbn.getChildren().size() > 1) {
                        Assert.assertEquals(3, cbn.getChildren().size());
                    }
                }
            }
        }

    }

    private void refreshApplication(ApplicationRuntimeModel application) throws Exception {
        DefaultListableBeanFactory rootBeanFactory = new DefaultListableBeanFactory();
        ConfigurableApplicationContext rootApplicationContext = new GenericApplicationContext(rootBeanFactory);
        rootApplicationContext.refresh();
        SofaModuleProperties sofaModuleProperties = new SofaModuleProperties();
        sofaModuleProperties.setBeanLoadCost(1);
        rootBeanFactory.registerSingleton("sofaModuleProperties", sofaModuleProperties);
        rootBeanFactory.registerSingleton(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT, new HashMap<>());
        SpringContextLoader springContextLoader = new DynamicSpringContextLoader(rootApplicationContext);

        for (DeploymentDescriptor dd: application.getResolvedDeployments()) {
            if (dd.isSpringPowered()) {
                springContextLoader.loadSpringContext(dd, application);

                ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) dd.getApplicationContext();
                dd.startDeploy();
                ctx.refresh();
                dd.deployFinish();
            }
        }
    }
}
