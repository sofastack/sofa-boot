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
package com.alipay.sofa.tracer.boot.flexible.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.AbstractAdvisingBeanPostProcessor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/10 11:51 AM
 * @since:
 **/
public class SofaTracerAdvisingBeanPostProcessor extends AbstractAdvisingBeanPostProcessor
                                                                                          implements
                                                                                          BeanFactoryAware {

    private MethodInterceptor interceptor;

    public SofaTracerAdvisingBeanPostProcessor(MethodInterceptor interceptor) {
        this.interceptor = interceptor;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        setBeforeExistingAdvisors(true);
        setExposeProxy(true);
        this.advisor = new TracerAnnotationClassAdvisor(this.interceptor);
    }
}
