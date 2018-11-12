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

import com.alipay.sofa.infra.constants.SofaBootInfraConstants;
import com.alipay.sofa.runtime.spi.log.SofaLogger;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.Ordered;

/**
 * {@link AbstractPipelineStage} is a common base class for {@link PipelineStage} implementations.
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractPipelineStage implements PipelineStage, Ordered {
    protected final ClassLoader                appClassLoader = AbstractPipelineStage.class
                                                                  .getClassLoader();
    protected final AbstractApplicationContext applicationContext;
    protected final String                     appName;

    public AbstractPipelineStage(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        appName = applicationContext.getEnvironment().getProperty(
            SofaBootInfraConstants.APP_NAME_KEY);
    }

    @Override
    public void process() throws Exception {
        SofaLogger.info("++++++++++++++++++ {0} of {1} Start +++++++++++++++++", this.getClass()
            .getSimpleName(), appName);
        doProcess();
        SofaLogger.info("++++++++++++++++++ {0} of {1} End +++++++++++++++++", this.getClass()
            .getSimpleName(), appName);
    }

    @Override
    public int getPriority() {
        return 0;
    }

    /**
     * do process pipeline stage, subclasses should override this method.
     *
     * @throws Exception if a failure occurred
     */
    protected abstract void doProcess() throws Exception;
}
