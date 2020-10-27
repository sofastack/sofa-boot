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
import com.alipay.sofa.boot.util.NamedThreadFactory;
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DependencyTree;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.deployment.DeploymentException;
import com.alipay.sofa.isle.loader.DynamicSpringContextLoader;
import com.alipay.sofa.isle.loader.SpringContextLoader;
import com.alipay.sofa.isle.spring.config.SofaModuleProperties;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.log.SofaLogger;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.Implementation;
import com.alipay.sofa.runtime.spi.util.ComponentNameFactory;
import com.alipay.sofa.runtime.spring.SpringContextComponent;
import com.alipay.sofa.runtime.spring.SpringContextImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author linfengqi
 * @author yangyanzhao
 * @author khotyn
 * @version $Id: SpringContextInstallStage.java, v 0.1 2012-3-16 21:17:48 fengqi.lin Exp $
 */
public class SpringContextInstallStage extends AbstractPipelineStage {
    private static final String  SYMBOLIC1 = "  ├─ ";
    private static final String  SYMBOLIC2 = "  └─ ";

    private static final int     CPU_COUNT = Runtime.getRuntime().availableProcessors(); ;

    @Autowired
    private SofaModuleProperties sofaModuleProperties;

    public SpringContextInstallStage(AbstractApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void doProcess() throws Exception {
        ApplicationRuntimeModel application = applicationContext.getBean(
            SofaBootConstants.APPLICATION, ApplicationRuntimeModel.class);

        try {
            doProcess(application);
        } catch (Throwable t) {
            SofaLogger.error("Install Spring Context got an error.", t);
            throw new DeploymentException("Install Spring Context got an error.", t);
        }
    }

    private void doProcess(ApplicationRuntimeModel application) throws Exception {
        outputModulesMessage(application);
        SpringContextLoader springContextLoader = createSpringContextLoader();
        installSpringContext(application, springContextLoader);

        if (sofaModuleProperties.isModuleStartUpParallel()) {
            refreshSpringContextParallel(application);
        } else {
            refreshSpringContext(application);
        }
    }

    protected SpringContextLoader createSpringContextLoader() {
        return new DynamicSpringContextLoader(applicationContext);
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
                    SofaLogger.error("Install module {} got an error!", deployment.getName(), t);
                    application.addFailed(deployment);
                } finally {
                    Thread.currentThread().setContextClassLoader(oldClassLoader);
                }
            }
        }
    }

    private void outputModulesMessage(ApplicationRuntimeModel application)
                                                                          throws DeploymentException {
        StringBuilder stringBuilder = new StringBuilder();
        if (application.getAllInactiveDeployments().size() > 0) {
            writeMessageToStringBuilder(stringBuilder, application.getAllInactiveDeployments(),
                "All unactivated module list");
        }
        writeMessageToStringBuilder(stringBuilder, application.getAllDeployments(),
            "All activated module list");
        writeMessageToStringBuilder(stringBuilder, application.getResolvedDeployments(),
            "Modules that could install");
        SofaLogger.info(stringBuilder.toString());

        String errorMessage = getErrorMessageByApplicationModule(application);
        if (StringUtils.hasText(errorMessage)) {
            SofaLogger.error(errorMessage);
        }

        if (application.getDeployRegistry().getPendingEntries().size() > 0) {
            throw new DeploymentException(errorMessage.trim());
        }
    }

    private String getErrorMessageByApplicationModule(ApplicationRuntimeModel application) {
        StringBuilder sbError = new StringBuilder(512);
        if (application.getDeployRegistry().getPendingEntries().size() > 0) {
            sbError
                .append(
                    "\nModules that could not install(Mainly due to module dependency not satisfied.)")
                .append("(").append(application.getDeployRegistry().getPendingEntries().size())
                .append(") >>>>>>>>\n");
        }

        for (DependencyTree.Entry<String, DeploymentDescriptor> entry : application
            .getDeployRegistry().getPendingEntries()) {
            if (application.getAllDeployments().contains(entry.get())) {
                sbError.append("[").append(entry.getKey()).append("]").append(" depends on ")
                    .append(entry.getWaitsFor()).append(", but the latter can not be resolved.")
                    .append("\n");
            }
        }

        return sbError.toString();
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
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CPU_COUNT + 1, CPU_COUNT + 1, 60,
            TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new NamedThreadFactory(
                "sofa-module-start"), new ThreadPoolExecutor.CallerRunsPolicy());
        try {
            for (DeploymentDescriptor deployment : application.getResolvedDeployments()) {
                DependencyTree.Entry entry = application.getDeployRegistry().getEntry(
                    deployment.getModuleName());
                if (entry != null && entry.getDependencies() == null) {
                    coreRoots.add(deployment);
                }
            }
            refreshSpringContextParallel(coreRoots, application.getResolvedDeployments().size(),
                application, executor);

        } finally {
            executor.shutdown();
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private void refreshSpringContextParallel(List<DeploymentDescriptor> rootDeployments,
                                              int totalSize,
                                              final ApplicationRuntimeModel application,
                                              final ThreadPoolExecutor executor) {
        if (rootDeployments == null || rootDeployments.size() == 0) {
            return;
        }

        final CountDownLatch latch = new CountDownLatch(totalSize);
        List<Future> futures = new CopyOnWriteArrayList<>();

        for (final DeploymentDescriptor deployment : rootDeployments) {
            refreshSpringContextParallel(deployment, application, executor, latch, futures);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException("Wait for Sofa Module Refresh Fail", e);
        }

        for (Future future : futures) {
            try {
                future.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void refreshSpringContextParallel(final DeploymentDescriptor deployment,
                                              final ApplicationRuntimeModel application,
                                              final ThreadPoolExecutor executor,
                                              final CountDownLatch latch, final List<Future> futures) {
        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
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
                                refreshSpringContextParallel(child.get(), application, executor,
                                    latch, futures);
                            }
                        }
                    }
                } catch (Throwable t) {
                    SofaLogger.error(
                        "Refreshing Spring Application Context of module {} got an error.",
                        deployment.getName(), t);
                    throw new RuntimeException("Refreshing Spring Application Context of module "
                                               + deployment.getName() + " got an error.", t);
                } finally {
                    latch.countDown();
                    Thread.currentThread().setName(oldName);
                }
            }
        }));
    }

    private void doRefreshSpringContext(DeploymentDescriptor deployment,
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
                SofaLogger.error(
                    "Refreshing Spring Application Context of module {} got an error.",
                    deployment.getName(), t);
                application.addFailed(deployment);
            } finally {
                deployment.deployFinish();
            }
        } else {
            String errorMsg = "Spring Application Context of module " + deployment.getName()
                              + " is null!";
            application.addFailed(deployment);
            SofaLogger.error(errorMsg, new RuntimeException(errorMsg));
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

    private void writeMessageToStringBuilder(StringBuilder sb, List<DeploymentDescriptor> deploys,
                                             String info) {
        int size = deploys.size();
        sb.append("\n").append(info).append("(").append(size).append(") >>>>>>>\n");

        for (int i = 0; i < size; ++i) {
            String symbol = i == size - 1 ? SYMBOLIC2 : SYMBOLIC1;
            sb.append(symbol).append(deploys.get(i).getName()).append("\n");
        }
    }

    @Override
    public String getName() {
        return "SpringContextInstallStage";
    }

    @Override
    public int getOrder() {
        return 20000;
    }
}
