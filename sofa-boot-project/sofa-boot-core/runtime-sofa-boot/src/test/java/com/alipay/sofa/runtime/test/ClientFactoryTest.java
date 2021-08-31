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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.service.binding.JvmBindingParam;
import com.alipay.sofa.runtime.test.beans.service.DefaultSampleService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.test.beans.ClientFactoryAwareBean;
import com.alipay.sofa.runtime.test.beans.facade.SampleService;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;

/**
 * @author qilong.zql
 * @since 3.2.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@TestPropertySource(properties = "spring.application.name=ClientFactoryTest")
public class ClientFactoryTest {

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
    public void testClientFactoryAware() {
        Assert.assertNotNull(clientFactoryAwareBean.getClientFactory());
        Assert.assertEquals(clientFactory, clientFactoryAwareBean.getClientFactory());
    }

    @Test
    public void testClientFactoryAnnotation() {
        Assert.assertNotNull(serviceClient);
        Assert.assertNotNull(clientFactory);
        Assert.assertNotNull(clientFactory);
        Assert.assertEquals(serviceClient, clientFactory.getClient(ServiceClient.class));
        Assert.assertEquals(referenceClient, clientFactory.getClient(ReferenceClient.class));
    }

    /**
     * {@link ClientFactoryAwareBean}
     */
    @Test
    public void testPublishAndReferenceServiceViaClientFactory() {
        Assert.assertNotNull(sampleService);
        Assert.assertEquals(ClientFactory.class.getName(), sampleService.service());

        ReferenceParam<SampleService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(SampleService.class);
        referenceParam.setUniqueId("clientFactory");
        Assert.assertEquals(sampleService, referenceClient.reference(referenceParam));
    }

    @Configuration(proxyBeanMethods = false)
    @Import(RuntimeConfiguration.class)
    static class ClientFactoryTestConfiguration {
        @Bean
        public ClientFactoryAwareBean clientFactoryAwareBean() {
            return new ClientFactoryAwareBean();
        }
    }

    /**
     * test WithBindingParam in ReferenceClientImpl and ServiceClientImpl
     */
    @Test
    public void testWithBindingParam() {
        JvmBindingParam jvmBindingParam = new JvmBindingParam();
        jvmBindingParam.setSerialize(true);

        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new DefaultSampleService("WithBindingParam"));
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
        Assert.assertNotNull(service);
        Assert.assertEquals("WithBindingParam", service.service());
    }

    private class PrivateServiceImpl implements PrivateService {
        @Override
        public String service() {
            return this.getClass().getName();
        }
    }

    public interface PrivateService {
        String service();
    }

    /**
     * test removeService and removeReference methods in in ReferenceClientImpl and ServiceClientImpl
     */
    @Test
    public void testRemoveServiceOrRemoveReference() {
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
        Assert.assertEquals(PrivateServiceImpl.class.getName(),
            referenceClient.reference(referenceParam).service());
        //reference with unique id
        referenceParam.setUniqueId("uniqueId");
        Assert.assertEquals(PrivateServiceImpl.class.getName(),
            referenceClient.reference(referenceParam).service());

        //remove Reference
        referenceClient.removeReference(PrivateService.class);
        referenceClient.removeReference(referenceParam);

        //remove Service
        try {
            serviceClient.removeService(PrivateService.class, -1);
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Argument delay must be a positive integer or zero.",
                ex.getMessage());
        }
        serviceClient.removeService(PrivateService.class, "uniqueId", 0);
    }

}