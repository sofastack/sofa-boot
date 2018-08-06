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
package com.alipay.sofa.runtime.integration.features;

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.beans.impl.SampleServiceImpl;
import com.alipay.sofa.runtime.beans.service.SampleService;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author qilong.zql
 * @since 2.3.1
 */
@Component
public class AwareTest implements ClientFactoryAware, ApplicationContextAware, InitializingBean {
    private ClientFactory           clientFactoryAware;

    private ApplicationContext      applicationContext;

    @SofaClientFactory
    private ClientFactory           clientFactory;

    @SofaClientFactory
    private ServiceClient           serviceClient;

    @SofaClientFactory
    private ReferenceClient         referenceClient;

    @SofaReference(uniqueId = "annotation")
    private SampleService           sampleServiceAnnotationWithUniqueId;

    @SofaReference(uniqueId = "annotation")
    private ServiceWithoutInterface serviceWithoutInterface;

    private SampleService           sampleServicePublishedByServiceClient;

    private SampleService           sampleServiceAnnotationImplWithMethod;

    @Override
    public void afterPropertiesSet() throws Exception {
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new SampleServiceImpl(
            "SampleServiceImpl published by service client."));
        serviceParam.setInterfaceType(SampleService.class);
        serviceParam.setUniqueId("serviceClientImpl");
        serviceClient.service(serviceParam);

        ReferenceParam<SampleService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(SampleService.class);
        referenceParam.setUniqueId("serviceClientImpl");
        sampleServicePublishedByServiceClient = referenceClient.reference(referenceParam);
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactoryAware = clientFactory;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ClientFactory getClientFactoryAware() {
        return clientFactoryAware;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    public ServiceClient getServiceClient() {
        return serviceClient;
    }

    public ReferenceClient getReferenceClient() {
        return referenceClient;
    }

    public SampleService getSampleServiceAnnotationWithUniqueId() {
        return sampleServiceAnnotationWithUniqueId;
    }

    public SampleService getSampleServicePublishedByServiceClient() {
        return sampleServicePublishedByServiceClient;
    }

    public SampleService getSampleServiceAnnotationImplWithMethod() {
        return sampleServiceAnnotationImplWithMethod;
    }

    public ServiceWithoutInterface getServiceWithoutInterface() {
        return serviceWithoutInterface;
    }

    @SofaReference(uniqueId = "method")
    public void setSampleServiceAnnotationImplWithMethod(SampleService sampleServiceAnnotationImplWithMethod) {
        this.sampleServiceAnnotationImplWithMethod = sampleServiceAnnotationImplWithMethod;
    }

    public SofaRuntimeContext getSofaRuntimeContext() {
        return applicationContext.getBean(
            SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID, SofaRuntimeContext.class);
    }
}