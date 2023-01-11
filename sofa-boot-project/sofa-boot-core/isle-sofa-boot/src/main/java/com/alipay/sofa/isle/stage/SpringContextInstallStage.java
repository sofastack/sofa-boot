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
package com.alipay.sofa.isle.stage;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.error.ErrorCode;
import com.alipay.sofa.boot.log.SofaLogger;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DependencyTree;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.loader.SpringContextLoader;
import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.ComponentManager;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import com.alipay.sofa.runtime.spring.SpringContextImplementation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author linfengqi
 * @author yangyanzhao
 * @author khotyn
 * @version $Id: SpringContextInstallStage.java, v 0.1 2012-3-16 21:17:48 fengqi.lin Exp $
 */
public class SpringContextInstallStage extends AbstractPipelineStage implements InitializingBean {

    public static final String SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME = "sofaModuleRefreshExecutor";

    public static final String SPRING_CONTEXT_INSTALL_STAGE_NAME = "SpringContextInstallStage";

    private SpringContextLoader springContextLoader;

    private boolean moduleStartUpParallel;

    private boolean ignoreModuleInstallFailure;

    private boolean unregisterComponentWhenModuleInstallFailure;

    private ExecutorService moduleRefreshExecutorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (moduleStartUpParallel) {
            moduleRefreshExecutorService = (ExecutorService) applicationContext.getBean(SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME,
                    Supplier.class).get();
        }
    }

    @Override
    protected void doProcess() throws Exception {
        ApplicationRuntimeModel application = applicationContext.getBean(SofaBootConstants.APPLICATION, ApplicationRuntimeModel.class);

        try {
            doProcess(application);
        } catch (Throwable t) {
            SofaLogger.error(ErrorCode.convert("01-11000"), t);
            throw new DeploymentException(ErrorCode.convert("01-11000"), t);
        }

        if (!ignoreModuleInstallFailure) {
            if (!application.getFailed().isEmpty()) {
                List<String> failedModuleNames = application.getFailed().stream().map(DeploymentDescriptor::getModuleName).collect(Collectors.toList());
                throw new DeploymentException(ErrorCode.convert("01-11007", failedModuleNames));
            }
        }
    }

    private void doProcess(ApplicationRuntimeModel application) throws Exception {
        installSpringContext(application, springContextLoader);

        if (moduleStartUpParallel && moduleRefreshExecutorService != null) {
            refreshSpringContextParallel(application);
        } else {
            refreshSpringContext(application);
        }
    }


    protected void installSpringContext(ApplicationRuntimeModel application,
                                        SpringContextLoader springContextLoader) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();

        for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
            if (deployment.isSpringPowered()) {
                SofaLogger.info("Start install " + application.getAppName() + "'s module: "
                                + deployment.getName());
                try {
                    Thread.currentThread().setContextClassLoader(deployment.getClassLoader());
                    springContextLoader.loadSpringContext(deployment, application);
                } catch (Throwable t) {
                    SofaLogger.error(ErrorCode.convert("01-11001", deployment.getName()), t);
                    application.addFailed(deployment);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldClassLoader);
                }
            }
        }
    }

    /**
     * start sofa module serial
     * @param application
     */
    private void refreshSpringContext(ApplicationRuntimeModel application) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
                if (deployment.isSpringPowered() && !application.getFailed().contains(deployment)) {
                    Thread.currentThread().setContextClassLoader(deployment.getClassLoader());
                    doRefreshSpringContext(deployment, application);
                }

                if (!ignoreModuleInstallFailure) {
                    if (!application.getFailed().isEmpty()) {
                        break;
                    }
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    /**
     * start sofa module parallel
     *
     * @param application
     */
    private void refreshSpringContextParallel(ApplicationRuntimeModel application) {
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        List<DeploymentDescriptor> coreRoots = new ArrayList<>();
        try {
            for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
                DependencyTree.Entry entry = application.getDeployRegistry().getEntry(
                    deployment.getModuleName());
                if (entry != null && entry.getDependencies() == null) {
                    coreRoots.add(deployment);
                }
            }
            refreshSpringContextParallel(coreRoots, application.getResolvedDeployments().size(),
                application);

        } finally {
            moduleRefreshExecutorService.shutdown();
            moduleRefreshExecutorService = null;
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private void refreshSpringContextParallel(List<DeploymentDescriptor> rootDeployments,
                                              int totalSize,
                                              final ApplicationRuntimeModel application) {
        if (rootDeployments == null || rootDeployments.size() == 0) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(totalSize);
        List<Future> futures = new CopyOnWriteArrayList<>();

        for (final DeploymentDescriptor deployment : rootDeployments) {
            refreshSpringContextParallel(deployment, application, latch, futures);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(ErrorCode.convert("01-11004"), e);
        }

        for (Future future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                throw new RuntimeException(ErrorCode.convert("01-11005"), e);
            }
        }

    }

    private void refreshSpringContextParallel(final DeploymentDescriptor deployment,
                                              final ApplicationRuntimeModel application,
                                              final CountDownLatch latch, final List<Future> futures) {
        futures.add(moduleRefreshExecutorService.submit(() -> {
            String oldName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(
                    "sofa-module-start-" + deployment.getModuleName());
                Thread.currentThread().setContextClassLoader(deployment.getClassLoader());
                if (deployment.isSpringPowered()
                    && !application.getFailed().contains(deployment)) {
                    doRefreshSpringContext(deployment, application);
                }
                DependencyTree.Entry<String, DeploymentDescriptor> entry = application
                    .getDeployRegistry().getEntry(deployment.getModuleName());
                if (entry != null && entry.getDependsOnMe() != null) {
                    for (final DependencyTree.Entry<String, DeploymentDescriptor> child : entry
                        .getDependsOnMe()) {
                        child.getDependencies().remove(entry);
                        if (child.getDependencies().size() == 0) {
                            refreshSpringContextParallel(child.get(), application,
                                latch, futures);
                        }
                    }
                }
            } catch (Throwable t) {
                SofaLogger.error(ErrorCode.convert("01-11002", deployment.getName()), t);
                throw new RuntimeException(ErrorCode.convert("01-11002", deployment.getName()),
                    t);
            } finally {
                latch.countDown();
                Thread.currentThread().setName(oldName);
            }
        }));
    }

    protected void doRefreshSpringContext(DeploymentDescriptor deployment,
                                          ApplicationRuntimeModel application) {
        SofaLogger.info("Begin refresh Spring Application Context of module {} of application {}.",
            deployment.getName(), application.getAppName());
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) deployment
            .getApplicationContext();
        if (ctx != null) {
            try {
                deployment.startDeploy();
                ctx.refresh();
                publishContextAsSofaComponent(deployment, application, ctx);
                application.addInstalled(deployment);
            } catch (Throwable t) {
                SofaLogger.error(ErrorCode.convert("01-11002", deployment.getName()), t);
                application.addFailed(deployment);
                unRegisterComponent(application, ctx);
            } finally {
                deployment.deployFinish();
            }
        } else {
            String errorMsg = ErrorCode.convert("01-11003", deployment.getName());
            application.addFailed(deployment);
            SofaLogger.error(errorMsg, new RuntimeException(errorMsg));
        }
    }

    private void unRegisterComponent(ApplicationRuntimeModel application,
                                     ConfigurableApplicationContext ctx) {
        if (unregisterComponentWhenModuleInstallFailure) {
            ComponentManager componentManager = application.getSofaRuntimeContext()
                .getComponentManager();
            Collection<ComponentInfo> componentInfos = componentManager
                .getComponentInfosByApplicationContext(ctx);
            for (ComponentInfo componentInfo : componentInfos) {
                try {
                    componentManager.unregister(componentInfo);
                } catch (ServiceRuntimeException e) {
                    SofaLogger.error(ErrorCode.convert("01-03001", componentInfo.getName()), e);
                }
            }
        }
    }

    private void publishContextAsSofaComponent(DeploymentDescriptor deployment,
                                               ApplicationRuntimeModel application,
                                               ApplicationContext context) {
        ComponentName componentName = ComponentNameFactory.createComponentName(
            SpringContextComponent.SPRING_COMPONENT_TYPE, deployment.getModuleName());
        Implementation implementation = new SpringContextImplementation(context);
        ComponentInfo componentInfo = new SpringContextComponent(componentName, implementation,
            application.getSofaRuntimeContext());
        application.getSofaRuntimeContext().getComponentManager().register(componentInfo);
    }

    public SpringContextLoader getSpringContextLoader() {
        return springContextLoader;
    }

    public void setSpringContextLoader(SpringContextLoader springContextLoader) {
        this.springContextLoader = springContextLoader;
    }

    public boolean isIgnoreModuleInstallFailure() {
        return ignoreModuleInstallFailure;
    }

    public void setIgnoreModuleInstallFailure(boolean ignoreModuleInstallFailure) {
        this.ignoreModuleInstallFailure = ignoreModuleInstallFailure;
    }

    public boolean isModuleStartUpParallel() {
        return moduleStartUpParallel;
    }

    public void setModuleStartUpParallel(boolean moduleStartUpParallel) {
        this.moduleStartUpParallel = moduleStartUpParallel;
    }

    public boolean isUnregisterComponentWhenModuleInstallFailure() {
        return unregisterComponentWhenModuleInstallFailure;
    }

    public void setUnregisterComponentWhenModuleInstallFailure(boolean unregisterComponentWhenModuleInstallFailure) {
        this.unregisterComponentWhenModuleInstallFailure = unregisterComponentWhenModuleInstallFailure;
    }

    public ExecutorService getModuleRefreshExecutorService() {
        return moduleRefreshExecutorService;
    }

    public void setModuleRefreshExecutorService(ExecutorService moduleRefreshExecutorService) {
        this.moduleRefreshExecutorService = moduleRefreshExecutorService;
    }

    @Override
    public String getName() {
        return SPRING_CONTEXT_INSTALL_STAGE_NAME;
    }

    @Override
    public int getOrder() {
        return 20000;
    }
}
