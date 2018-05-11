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

import com.alipay.sofa.isle.constants.SofaIsleFrameworkConstants;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * {@link AbstractPipelineStage} is a common base class for {@link PipelineStage} implementations.
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractPipelineStage implements PipelineStage {
    final ClassLoader                appClassLoader = AbstractPipelineStage.class.getClassLoader();
    final AbstractApplicationContext applicationContext;
    final String                     appName;

    public AbstractPipelineStage(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        appName = applicationContext.getEnvironment().getProperty(
            SofaIsleFrameworkConstants.APPLICATION_NAME);
    }

    @Override
    public void process() throws Exception {
        SofaLogger.info("++++++++++++++++++ {0}'s {1} Start +++++++++++++++++", appName, this
            .getClass().getSimpleName());
        doProcess();
        SofaLogger.info("++++++++++++++++++ {0}'s {1} End +++++++++++++++++", appName, this
            .getClass().getSimpleName());
    }

    /**
     * do process pipeline stage, subclasses should override this method.
     *
     * @throws Exception if a failure occurred
     */
    protected abstract void doProcess() throws Exception;
}
