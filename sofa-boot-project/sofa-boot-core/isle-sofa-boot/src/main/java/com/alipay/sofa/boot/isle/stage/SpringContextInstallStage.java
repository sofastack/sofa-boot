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
package com.alipay.sofa.boot.isle.stage;

import com.alipay.sofa.boot.isle.deployment.DependencyTree;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.boot.isle.deployment.DeploymentException;
import com.alipay.sofa.boot.isle.loader.SpringContextLoader;
import com.alipay.sofa.boot.log.ErrorCode;
import com.alipay.sofa.boot.log.SofaBootLoggerFactory;
import com.alipay.sofa.boot.startup.BaseStat;
import com.alipay.sofa.boot.startup.ChildrenStat;
import com.alipay.sofa.boot.startup.ModuleStat;
import com.alipay.sofa.boot.util.ClassLoaderContextUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Stage to create and refresh {@link ApplicationContext} for sofa modules.
 *
 * @author linfengqi
 * @author yangyanzhao
 * @author khotyn
 * @author huzijie
 * @version $Id: SpringContextInstallStage.java, v 0.1 2012-3-16 21:17:48 fengqi.lin Exp $
 */
public class SpringContextInstallStage extends AbstractPipelineStage implements InitializingBean {

    private static final Logger   LOGGER                                 = SofaBootLoggerFactory
                                                                             .getLogger(SpringContextInstallStage.class);

    public static final String    SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME = "sofaModuleRefreshExecutor";

    public static final String    SPRING_CONTEXT_INSTALL_STAGE_NAME      = "SpringContextInstallStage";

    protected SpringContextLoader springContextLoader;

    protected boolean             moduleStartUpParallel;

    protected boolean             ignoreModuleInstallFailure;

    protected ExecutorService     moduleRefreshExecutorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.notNull(springContextLoader, "springContextLoader must not be null");
        if (moduleStartUpParallel) {
            moduleRefreshExecutorService = (ExecutorService) applicationContext.getBean(
                SOFA_MODULE_REFRESH_EXECUTOR_BEAN_NAME, Supplier.class).get();
            Assert.notNull(moduleRefreshExecutorService,
                "moduleRefreshExecutorService must not be null");
        }
    }

    @Override
    protected void doProcess() throws Exception {
        try {
            installSpringContext();
            refreshSpringContext();
        } catch (Throwable t) {
            LOGGER.error(ErrorCode.convert("01-11000"), t);
            throw new DeploymentException(ErrorCode.convert("01-11000"), t);
        }

        if (ignoreModuleInstallFailure || application.getFailed().isEmpty()) {
            return;
        }
        throwModuleInstallFailure();
    }

    @Override
    protected BaseStat createBaseStat() {
        return new ChildrenStat<ModuleStat>();
    }

    /**
     * Create {@link ApplicationContext} for each {@link DeploymentDescriptor}
     */
    protected void installSpringContext() {
        for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
            if (deployment.isSpringPowered()) {
                LOGGER.info("Start install ApplicationContext for module {}.", deployment.getName());
                ClassLoaderContextUtils.runWithTemporaryContextClassloader(() -> {
                    try {
                        springContextLoader.loadSpringContext(deployment, application);
                    } catch (Throwable t) {
                        LOGGER.error(ErrorCode.convert("01-11001", deployment.getName()), t);
                        application.addFailed(deployment);
                    }
                }, deployment.getClassLoader());
            }
        }
    }

    /**
     * Refresh {@link ApplicationContext} for each {@link DeploymentDescriptor}
     */
    private void refreshSpringContext() {
        // 并行刷新
        if (moduleStartUpParallel && moduleRefreshExecutorService != null) {
            doRefreshSpringContextParallel();
        } else {
            doRefreshSpringContextSerial();
        }
    }

    private void doRefreshSpringContextSerial() {
        for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
            if (deployment.isSpringPowered() && !application.getFailed().contains(deployment)) {
                refreshAndCollectCost(deployment);
            }
            // 模块刷新异常时提前中止
            if (!ignoreModuleInstallFailure && !application.getFailed().isEmpty()) {
                break;
            }
        }
    }

    private void doRefreshSpringContextParallel() {
        try {
            List<DeploymentDescriptor> rootDescriptors = application.getResolvedDeployments().stream()
                    .filter(descriptor -> {
                        DependencyTree.Entry<String, DeploymentDescriptor> entry = application.getDeployRegistry()
                                .getEntry(descriptor.getModuleName());
                        return entry != null && CollectionUtils.isEmpty(entry.getDependencies());
                    }).toList();
            if (rootDescriptors == null || rootDescriptors.size() == 0) {
                return;
            }
            final CountDownLatch latch = new CountDownLatch(application.getResolvedDeployments().size());
            List<Future<?>> futures = new CopyOnWriteArrayList<>();

            for (final DeploymentDescriptor deployment : rootDescriptors) {
                refreshRecursively(deployment, latch, futures);
            }
            
            try {
                latch.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(ErrorCode.convert("01-11004"), e);
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Throwable e) {
                    throw new RuntimeException(ErrorCode.convert("01-11005"), e);
                }
            }
        } finally {
            moduleRefreshExecutorService.shutdown();
            moduleRefreshExecutorService = null;
        }

    }

    /**
     * Refresh all {@link ApplicationContext} recursively
     */
    private void refreshRecursively(DeploymentDescriptor deployment,
                                  CountDownLatch latch, List<Future<?>> futures) {
        futures.add(moduleRefreshExecutorService.submit(() -> {
            String oldName = Thread.currentThread().getName();
            try {
                Thread.currentThread().setName(
                        "sofa-module-refresh-" + deployment.getModuleName());
                if (deployment.isSpringPowered() && !application.getFailed().contains(deployment)) {
                    refreshAndCollectCost(deployment);
                }
                DependencyTree.Entry<String, DeploymentDescriptor> entry = application
                        .getDeployRegistry().getEntry(deployment.getModuleName());
                if (entry != null && entry.getDependsOnMe() != null) {
                    for (DependencyTree.Entry<String, DeploymentDescriptor> child : entry
                            .getDependsOnMe()) {
                        child.getDependencies().remove(entry);
                        if (child.getDependencies().size() == 0) {
                            refreshRecursively(child.get(), latch, futures);
                        }
                    }
                }
            } catch (Throwable t) {
                LOGGER.error(ErrorCode.convert("01-11002", deployment.getName()), t);
                throw new RuntimeException(ErrorCode.convert("01-11002", deployment.getName()),
                        t);
            } finally {
                latch.countDown();
                Thread.currentThread().setName(oldName);
            }
        }));
    }

    protected void refreshAndCollectCost(DeploymentDescriptor deployment) {
        ModuleStat moduleStat = new ModuleStat();
        moduleStat.setName(deployment.getModuleName());
        moduleStat.setStartTime(System.currentTimeMillis());

        doRefreshSpringContext(deployment);

        moduleStat.setEndTime(System.currentTimeMillis());
        moduleStat.setThreadName(Thread.currentThread().getName());
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) deployment
            .getApplicationContext();
        if (startupReporter != null && baseStat != null) {
            moduleStat.setChildren(startupReporter.generateBeanStats(ctx));
            ((ChildrenStat<ModuleStat>) baseStat).addChild(moduleStat);
        }
    }

    protected void doRefreshSpringContext(DeploymentDescriptor deployment) {
        LOGGER.info("Start refresh ApplicationContext for module {}.", deployment.getName());
        ConfigurableApplicationContext ctx = (ConfigurableApplicationContext) deployment.getApplicationContext();
        if (ctx != null) {
            try {
                deployment.startDeploy();
                ClassLoaderContextUtils.runWithTemporaryContextClassloader(ctx::refresh, deployment.getClassLoader());
                application.addInstalled(deployment);
            } catch (Throwable t) {
                LOGGER.error(ErrorCode.convert("01-11002", deployment.getName()), t);
                application.addFailed(deployment);
            } finally {
                deployment.deployFinish();
            }
        } else {
            String errorMsg = ErrorCode.convert("01-11003", deployment.getName());
            application.addFailed(deployment);
            LOGGER.error(errorMsg, new RuntimeException(errorMsg));
        }
    }

    private void throwModuleInstallFailure() throws DeploymentException {
        List<String> failedModuleNames = application.getFailed().stream().map(DeploymentDescriptor::getModuleName).collect(Collectors.toList());
        throw new DeploymentException(ErrorCode.convert("01-11007", failedModuleNames));
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

    @Override
    public String getName() {
        return SPRING_CONTEXT_INSTALL_STAGE_NAME;
    }

    @Override
    public int getOrder() {
        return 20000;
    }
}
