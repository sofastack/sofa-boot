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
package com.alipay.sofa.boot.actuator.isle;

import com.alipay.sofa.boot.isle.ApplicationRuntimeModel;
import com.alipay.sofa.boot.isle.deployment.DeploymentDescriptor;
import org.springframework.boot.actuate.endpoint.OperationResponseBody;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Endpoint @Endpoint} to expose details of modules registered in {@link com.alipay.sofa.boot.isle.ApplicationRuntimeModel}.
 *
 * @author huzijie
 * @version IsleEndpoint.java, v 0.1 2023年10月10日 2:59 PM huzijie Exp $
 */
@Endpoint(id = "isle")
public class IsleEndpoint {

    private final ApplicationRuntimeModel applicationRuntimeModel;

    public IsleEndpoint(ApplicationRuntimeModel applicationRuntimeModel) {
        this.applicationRuntimeModel = applicationRuntimeModel;
    }

    @ReadOperation
    public IsleDescriptor modules() {
        // already installed modules
        List<ModuleDisplayInfo> installedModuleList = convertModuleDisplayInfoList(applicationRuntimeModel
            .getInstalled());

        List<ModuleDisplayInfo> failedModuleList = convertModuleDisplayInfoList(applicationRuntimeModel
            .getFailed());

        List<ModuleDisplayInfo> inactiveModuleList = convertModuleDisplayInfoList(applicationRuntimeModel
            .getAllInactiveDeployments());

        return new IsleDescriptor(installedModuleList, failedModuleList, inactiveModuleList);
    }

    private List<ModuleDisplayInfo> convertModuleDisplayInfoList(List<DeploymentDescriptor> deploymentDescriptors) {
        return deploymentDescriptors.stream().map(this::createBaseModuleInfo).collect(Collectors.toList());
    }

    private ModuleDisplayInfo createBaseModuleInfo(DeploymentDescriptor dd) {
        ModuleDisplayInfo moduleDisplayInfo = new ModuleDisplayInfo();
        moduleDisplayInfo.setName(dd.getModuleName());
        moduleDisplayInfo.setResourceName(dd.getName());
        moduleDisplayInfo.setSpringParent(dd.getSpringParent());
        moduleDisplayInfo.setRequireModules(dd.getRequiredModules());

        ApplicationContext applicationContext = dd.getApplicationContext();
        if (applicationContext != null) {
            moduleDisplayInfo.setInstallSpringXmls(dd.getInstalledSpringXml());
            moduleDisplayInfo.setElapsedTime(dd.getElapsedTime());
            moduleDisplayInfo.setStartupTime(dd.getStartTime());
        }
        return moduleDisplayInfo;
    }

    public static final class IsleDescriptor implements OperationResponseBody {

        /** 刷新成功的模块 **/
        private final List<ModuleDisplayInfo> installedModuleList;

        /** 创建失败的模块 **/
        private final List<ModuleDisplayInfo> failedModuleList;

        /** 未激活的模块 **/

        private final List<ModuleDisplayInfo> inactiveModuleList;

        public IsleDescriptor(List<ModuleDisplayInfo> installedModuleList,
                              List<ModuleDisplayInfo> failedModuleList,
                              List<ModuleDisplayInfo> inactiveModuleList) {
            this.installedModuleList = installedModuleList;
            this.failedModuleList = failedModuleList;
            this.inactiveModuleList = inactiveModuleList;
        }

        public List<ModuleDisplayInfo> getInstalledModuleList() {
            return installedModuleList;
        }

        public List<ModuleDisplayInfo> getFailedModuleList() {
            return failedModuleList;
        }

        public List<ModuleDisplayInfo> getInactiveModuleList() {
            return inactiveModuleList;
        }
    }

    public static final class ModuleDisplayInfo {

        private String       name;

        private String       springParent;

        private List<String> requireModules;

        private String       resourceName;

        private long         startupTime;

        private long         elapsedTime;

        private List<String> installSpringXmls;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSpringParent() {
            return springParent;
        }

        public void setSpringParent(String springParent) {
            this.springParent = springParent;
        }

        public List<String> getRequireModules() {
            return requireModules;
        }

        public void setRequireModules(List<String> requireModules) {
            this.requireModules = requireModules;
        }

        public String getResourceName() {
            return resourceName;
        }

        public void setResourceName(String resourceName) {
            this.resourceName = resourceName;
        }

        public List<String> getInstallSpringXmls() {
            return installSpringXmls;
        }

        public void setInstallSpringXmls(List<String> installSpringXmls) {
            this.installSpringXmls = installSpringXmls;
        }

        public long getStartupTime() {
            return startupTime;
        }

        public void setStartupTime(long startupTime) {
            this.startupTime = startupTime;
        }

        public long getElapsedTime() {
            return elapsedTime;
        }

        public void setElapsedTime(long elapsedTime) {
            this.elapsedTime = elapsedTime;
        }
    }
}
