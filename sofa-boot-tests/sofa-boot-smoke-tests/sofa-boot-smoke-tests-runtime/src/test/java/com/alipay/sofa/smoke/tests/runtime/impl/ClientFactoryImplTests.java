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
package com.alipay.sofa.smoke.tests.runtime.impl;

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.impl.ClientFactoryImpl;
import com.alipay.sofa.runtime.service.binding.JvmBindingParam;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link ClientFactoryImpl}.
 * 
 * @author qilong.zql
 * @author huzijie
 * @since 3.2.0
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(ClientFactoryImplTests.ClientFactoryTestConfiguration.class)
public class ClientFactoryImplTests {

    @SofaClientFactory
    private ServiceClient          serviceClient;

    @SofaClientFactory
    private ReferenceClient        referenceClient;

    @SofaClientFactory
    private ClientFactory          clientFactory;

    @Autowired
    private ClientFactoryAwareBean clientFactoryAwareBean;

    @SofaReference(uniqueId = "clientFactory")
    private SampleService          sampleService;

    @Test
    public void clientFactoryAware() {
        assertThat(clientFactoryAwareBean.getClientFactory()).isNotNull();
        assertThat(clientFactory).isEqualTo(clientFactoryAwareBean.getClientFactory());
    }

    @Test
    public void clientFactoryAnnotation() {
        assertThat(serviceClient).isNotNull();
        assertThat(clientFactory).isNotNull();
        assertThat(clientFactory).isNotNull();
        assertThat(serviceClient).isEqualTo(clientFactory.getClient(ServiceClient.class));
        assertThat(referenceClient).isEqualTo(clientFactory.getClient(ReferenceClient.class));
    }

    /**
     * {@link ClientFactoryAwareBean}
     */
    @Test
    public void publishAndReferenceServiceViaClientFactory() {
        assertThat(sampleService).isNotNull();
        assertThat(ClientFactory.class.getName()).isEqualTo(sampleService.service());

        ReferenceParam<SampleService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(SampleService.class);
        referenceParam.setUniqueId("clientFactory");
        assertThat(sampleService).isEqualTo(referenceClient.reference(referenceParam));
    }

    /**
     * test WithBindingParam in ReferenceClientImpl and ServiceClientImpl
     */
    @Test
    public void withBindingParam() {
        JvmBindingParam jvmBindingParam = new JvmBindingParam();
        jvmBindingParam.setSerialize(true);

        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new SampleServiceImpl("WithBindingParam"));
        serviceParam.setInterfaceType(SampleService.class);
        serviceParam.setUniqueId("withBindingParam");
        serviceParam.addBindingParam(jvmBindingParam);

        serviceClient.service(serviceParam);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            //ignore
        }
        ReferenceParam<SampleService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(SampleService.class);
        referenceParam.setUniqueId("withBindingParam");
        referenceParam.setBindingParam(jvmBindingParam);

        SampleService service = referenceClient.reference(referenceParam);
        assertThat(service).isNotNull();
        assertThat("WithBindingParam").isEqualTo(service.service());
    }

    /**
     * test removeService and removeReference methods in ReferenceClientImpl and ServiceClientImpl
     */
    @Test
    public void removeServiceOrRemoveReference() {
        //without unique id
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new PrivateServiceImpl());
        serviceParam.setInterfaceType(PrivateService.class);
        serviceClient.service(serviceParam);
        //with unique id
        serviceParam.setUniqueId("uniqueId");
        serviceClient.service(serviceParam);

        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            //ignore
        }

        //reference without unique id
        ReferenceParam<PrivateService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(PrivateService.class);
        assertThat(PrivateServiceImpl.class.getName()).isEqualTo(
            referenceClient.reference(referenceParam).service());
        //reference with unique id
        referenceParam.setUniqueId("uniqueId");
        assertThat(PrivateServiceImpl.class.getName()).isEqualTo(
            referenceClient.reference(referenceParam).service());

        //remove Reference
        referenceClient.removeReference(PrivateService.class);
        referenceClient.removeReference(referenceParam);

        //remove Service
        try {
            serviceClient.removeService(PrivateService.class, -1);
        } catch (IllegalArgumentException ex) {
            assertThat(ex.getMessage()).contains(
                "Argument delay must be a positive integer or zero");
        }
        serviceClient.removeService(PrivateService.class, "uniqueId", 0);
    }

    static class PrivateServiceImpl implements PrivateService {
        @Override
        public String service() {
            return this.getClass().getName();
        }
    }

    public interface PrivateService {
        String service();
    }

    @TestConfiguration
    static class ClientFactoryTestConfiguration {

        @Bean
        public ClientFactoryAwareBean clientFactoryAwareBean() {
            return new ClientFactoryAwareBean();
        }
    }

}