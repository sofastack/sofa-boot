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
import com.alipay.sofa.isle.ApplicationRuntimeModel;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import com.alipay.sofa.isle.spring.factory.BeanLoadCostBeanFactory;
import com.alipay.sofa.runtime.log.SofaLogger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Iterator;
import java.util.List;

/**
 * @author fengqi.lin
 * @author yangyanzhao
 * @version $Id: ModuleLogOutputStage.java, v 0.1 2012-3-16 18:17:48 fengqi.lin Exp $
 */
public class ModuleLogOutputStage extends AbstractPipelineStage {
    private static final String SYMBOLIC1          = "  ├─";
    private static final String SYMBOLIC2          = "  └─";

    private static final String SYMBOLIC3          = "  │   +---";
    private static final String SYMBOLIC4          = "  │   `---";

    private static final String SYMBOLIC5          = "      +---";
    private static final String SYMBOLIC6          = "      `---";

    private static final String INDENT_PREFIX      = "  │   ";
    private static final String EMPTY_INDEX_PREFIX = "      ";

    public ModuleLogOutputStage(AbstractApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    protected void doProcess() throws Exception {
        ApplicationRuntimeModel application = applicationContext.getBean(
            SofaBootConstants.APPLICATION, ApplicationRuntimeModel.class);

        StringBuilder stringBuilder = new StringBuilder();
        logInstalledModules(stringBuilder, application.getInstalled());
        logFailedModules(stringBuilder, application.getFailed());
        logInfoBeanCost(stringBuilder, application.getInstalled());

        SofaLogger.info(stringBuilder.toString());
    }

    private void logInstalledModules(StringBuilder stringBuilder, List<DeploymentDescriptor> deploys) {
        long totalTime = 0;
        long realStart = 0;
        long realEnd = 0;
        stringBuilder.append("\n").append("Spring context initialize success module list")
            .append("(").append(deploys.size()).append(") >>>>>>>");
        StringBuilder sb = new StringBuilder();
        for (Iterator<DeploymentDescriptor> i = deploys.iterator(); i.hasNext();) {
            DeploymentDescriptor dd = i.next();
            String outTreeSymbol = SYMBOLIC1;
            String innerTreeSymbol1 = SYMBOLIC3;
            String innerTreeSymbol2 = SYMBOLIC4;

            if (!i.hasNext()) {
                outTreeSymbol = SYMBOLIC2;
                innerTreeSymbol1 = SYMBOLIC5;
                innerTreeSymbol2 = SYMBOLIC6;
            }
            sb.append(outTreeSymbol).append(dd.getName()).append(" [").append(dd.getElapsedTime())
                .append(" ms]\n");
            totalTime += dd.getElapsedTime();

            for (Iterator<String> j = dd.getInstalledSpringXml().iterator(); j.hasNext();) {
                String xmlPath = j.next();
                String innerTreeSymbol = innerTreeSymbol1;
                if (!j.hasNext()) {
                    innerTreeSymbol = innerTreeSymbol2;
                }
                sb.append(innerTreeSymbol).append(xmlPath).append("\n");
            }

            if (realStart == 0 || dd.getStartTime() < realStart) {
                realStart = dd.getStartTime();
            }

            if (realEnd == 0 || (dd.getStartTime() + dd.getElapsedTime()) > realEnd) {
                realEnd = dd.getStartTime() + dd.getElapsedTime();
            }
        }
        stringBuilder.append(" [totalTime = ").append(totalTime).append(" ms, realTime = ")
            .append(realEnd - realStart).append(" ms]\n").append(sb);
    }

    private void logFailedModules(StringBuilder stringBuilder, List<DeploymentDescriptor> deploys) {
        stringBuilder.append("\n").append("Spring context initialize failed module list")
            .append("(").append(deploys.size()).append(") >>>>>>>\n");
        for (Iterator<DeploymentDescriptor> i = deploys.iterator(); i.hasNext();) {
            DeploymentDescriptor dd = i.next();
            String treeSymbol = SYMBOLIC1;
            if (!i.hasNext()) {
                treeSymbol = SYMBOLIC2;
            }
            stringBuilder.append(treeSymbol).append(dd.getName()).append("\n");
        }
    }

    private void logInfoBeanCost(StringBuilder stringBuilder, List<DeploymentDescriptor> deploys) {
        long totalTime = 0;
        long realStart = 0;
        long realEnd = 0;
        stringBuilder.append("\n").append("Spring bean load time cost list").append("(")
            .append(deploys.size()).append(") >>>>>>>");
        StringBuilder sb = new StringBuilder();

        int size = deploys.size();
        for (int i = 0; i < size; ++i) {
            String prefix = (i == size - 1) ? SYMBOLIC2 : SYMBOLIC1;
            String indexPrefix = (i == size - 1) ? EMPTY_INDEX_PREFIX : INDENT_PREFIX;

            DeploymentDescriptor dd = deploys.get(i);
            BeanFactory beanFactory = ((ConfigurableApplicationContext) dd.getApplicationContext())
                .getBeanFactory();
            if (realStart == 0 || dd.getStartTime() < realStart) {
                realStart = dd.getStartTime();
            }
            if (realEnd == 0 || (dd.getStartTime() + dd.getElapsedTime()) > realEnd) {
                realEnd = dd.getStartTime() + dd.getElapsedTime();
            }
            totalTime += dd.getElapsedTime();

            if (beanFactory instanceof BeanLoadCostBeanFactory) {
                sb.append(prefix).append("[Module] ").append(dd.getName()).append(" [")
                    .append(dd.getElapsedTime()).append(" ms]\n");
                sb.append(((BeanLoadCostBeanFactory) beanFactory).outputBeanStats(indexPrefix));
            }
        }
        stringBuilder.append(" [totalTime = ").append(totalTime).append(" ms, realTime = ")
            .append(realEnd - realStart).append(" ms]\n").append(sb);
    }

    @Override
    public String getName() {
        return "ModuleLogOutputStage";
    }

    @Override
    public int getOrder() {
        return 30000;
    }
}