package com.alipay.sofa.boot.isle;

import com.alipay.sofa.boot.context.SofaDefaultListableBeanFactory;
import com.alipay.sofa.boot.context.SofaGenericApplicationContext;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;

/**
 * @author huzijie
 * @version ApplicationContextPostProcessor.java, v 0.1 2023年01月12日 12:28 PM huzijie Exp $
 */
public interface SofaModulePostProcessor {

    default void postProcessSofaBeanFactory(
            DeploymentDescriptor deploymentDescriptor,
            SofaDefaultListableBeanFactory sofaBeanFactory) throws Exception {
    }


    default void postProcessSofaApplicationContext(
            DeploymentDescriptor deploymentDescriptor,
            SofaGenericApplicationContext sofaApplicationContext) throws Exception {
    }
}
