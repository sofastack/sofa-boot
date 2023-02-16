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
package com.alipay.sofa.smoke.tests.rpc.boot;

import com.alipay.hessian.generic.model.GenericObject;
import com.alipay.sofa.rpc.api.GenericService;
import com.alipay.sofa.rpc.api.future.SofaResponseFuture;
import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.param.RestBindingParam;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.core.exception.SofaTimeOutException;
import com.alipay.sofa.rpc.module.Module;
import com.alipay.sofa.rpc.module.ModuleFactory;
import com.alipay.sofa.runtime.api.annotation.SofaClientFactory;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import com.alipay.sofa.runtime.api.client.ServiceClient;
import com.alipay.sofa.runtime.api.client.param.BindingParam;
import com.alipay.sofa.runtime.api.client.param.ServiceParam;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.smoke.tests.rpc.bean.annotation.AnnotationService;
import com.alipay.sofa.smoke.tests.rpc.bean.connectionnum.ConnectionNumService;
import com.alipay.sofa.smoke.tests.rpc.bean.direct.DirectService;
import com.alipay.sofa.smoke.tests.rpc.bean.filter.FilterService;
import com.alipay.sofa.smoke.tests.rpc.bean.globalfilter.GlobalFilterService;
import com.alipay.sofa.smoke.tests.rpc.bean.invoke.CallbackImpl;
import com.alipay.sofa.smoke.tests.rpc.bean.invoke.HelloCallbackService;
import com.alipay.sofa.smoke.tests.rpc.bean.invoke.HelloFutureService;
import com.alipay.sofa.smoke.tests.rpc.bean.invoke.HelloSyncService;
import com.alipay.sofa.smoke.tests.rpc.bean.lazy.LazyService;
import com.alipay.sofa.smoke.tests.rpc.bean.rest.AddService;
import com.alipay.sofa.smoke.tests.rpc.bean.rest.RestService;
import com.alipay.sofa.smoke.tests.rpc.bean.retry.RetriesService;
import com.alipay.sofa.smoke.tests.rpc.bean.retry.RetriesServiceImpl;
import com.alipay.sofa.smoke.tests.rpc.bean.threadpool.ThreadPoolService;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author QilongZhang
 * @author yuanxuan
 * @version : SofaBootRpcApplicationTests.java, v 0.1 15:19 yuanxuan Exp $
 */
@SpringBootTest(properties = { "sofa.boot.rpc.rest-swagger=false",
                              "sofa.boot.rpc.enable-swagger=true",
                              "sofa.boot.rpc" + ".defaultTracer=" })
@ActiveProfiles("test")
public class SofaBootRpcApplicationTests {

    @Autowired
    private HelloSyncService        helloSyncService;

    @Autowired
    private HelloFutureService      helloFutureService;

    @Autowired
    private HelloCallbackService    helloCallbackService;

    @Autowired
    private FilterService           filterService;

    @Autowired
    private GlobalFilterService     globalFilterService;

    @Autowired
    private DirectService           directService;

    @Autowired
    private GenericService          genericService;

    @Autowired
    private ThreadPoolService       threadPoolService;

    @Autowired
    private RestService             restService;

    /*@Autowired
    private DubboService dubboService;*/

    @Autowired
    private RetriesService          retriesServiceBolt;

    /* @Autowired
     private RetriesService retriesServiceDubbo;*/

    @Autowired
    @Qualifier(value = "lazyServiceBolt")
    private LazyService             lazyServiceBolt;

    /*@Autowired
    private LazyService lazyServiceDubbo;*/

    @Autowired
    private ConnectionNumService    connectionNumService;

    /*@Autowired
    @Qualifier("sofaGreeterTripleRef")
    private SofaGreeterTriple.IGreeter sofaGreeterTripleRef;*/

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt"), jvmFirst = false, uniqueId = "bolt")
    private AnnotationService       annotationService;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", serializeType = "protobuf"), jvmFirst = false, uniqueId = "pb")
    private AnnotationService       annotationServicePb;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", loadBalancer = "roundRobin"), uniqueId = "loadbalancer")
    private AnnotationService       annotationLoadBalancerService;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt"), jvmFirst = false, uniqueId = "timeout")
    private AnnotationService       annotationProviderTimeoutService;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "bolt", timeout = 1000), jvmFirst = false, uniqueId = "timeout")
    private AnnotationService       annotationConsumerTimeoutService;

    @SofaReference(binding = @SofaReferenceBinding(bindingType = "rest", connectionNum = 100), jvmFirst = false, uniqueId = "connectionNum")
    private AnnotationService       annotationConsumerConnectionNumService;

    @SofaClientFactory
    private ClientFactory           clientFactory;

    @Autowired
    private ConsumerConfigContainer consumerConfigContainer;

    @Test
    public void testTimeoutPriority() throws InterruptedException {

        //If all timeout configuration is not configured, the default timeout time 3000ms will take effect.The interface is ok.
        assertThat(annotationService.testTimeout(2000)).isEqualTo("sleep 2000 ms");

        try {
            //If all timeout configuration is not configured, the default timeout time 3000ms will take effect.The call will be time out.
            annotationService.testTimeout(4000);
            fail();
        } catch (SofaTimeOutException e) {

        }
        //Only configure the provider side timeout 5000ms, and the default timeout time 3000ms will be invalid.
        //Assert.assertEquals("sleep 4000 ms", annotationProviderTimeoutService.testTimeout(4000));
        try {
            //Configured the consumer side timeout time of 1000ms, the provider side timeout time of 5000ms and the default timeout time
            // of 3000ms are invalid.
            annotationConsumerTimeoutService.testTimeout(2000);

            fail();
        } catch (SofaTimeOutException e) {

        }

    }

    @Test
    public void testInvoke() throws InterruptedException {
        assertThat(helloSyncService.saySync("sync")).isEqualTo("sync");

        helloFutureService.sayFuture("future");
        assertThat(SofaResponseFuture.getResponse(1000, true)).isEqualTo("future");

        helloCallbackService.sayCallback("callback");
        Thread.sleep(1000);
        assertThat(CallbackImpl.result);
    }

    @Test
    public void testGlobalFilter() {
        assertThat(globalFilterService.sayGlobalFilter("globalFilter")).isEqualTo(
            "globalFilter_change");
    }

    @Test
    public void testDirect() throws InterruptedException {

        Thread.sleep(5000);

        assertThat(directService.sayDirect("direct")).isEqualTo("direct");

    }

    @Test
    public void testFilter() {
        assertThat(filterService.sayFilter("filter")).isEqualTo("filter_change");
    }

    @Test
    public void testGeneric() {
        GenericObject genericObject = new GenericObject(
            "com.alipay.sofa.tests.rpc.bean.generic.GenericParamModel");
        genericObject.putField("name", "Bible");

        GenericObject result = (GenericObject) genericService.$genericInvoke("sayGeneric",
            new String[] { "com.alipay.sofa.tests.rpc.bean.generic.GenericParamModel" },
            new Object[] { genericObject });

        assertThat(result.getType()).isEqualTo(
            "com.alipay.sofa.tests.rpc.bean.generic.GenericResultModel");
        assertThat(result.getField("name")).isEqualTo("Bible");
        assertThat(result.getField("value")).isEqualTo("sample generic value");
    }

    @Test
    public void testThreadPool() {
        assertThat(threadPoolService.sayThreadPool("threadPool")).startsWith(
            "threadPool[SOFA-customerThreadPool_name");
    }

    @Test
    public void testRest() {
        assertThat(restService.sayRest("rest")).isEqualTo("rest");
    }

    /*@Test
    public void testDubbo() {
        Assert.assertEquals("dubbo", dubboService.sayDubbo("dubbo"));
    }
    */
    @Test
    public void testRetries() throws InterruptedException {
        assertThat(retriesServiceBolt.sayRetry("retries_bolt")).isEqualTo("retries_bolt");
        //TODO need dubbo version upgrade later 3.0.6 for support jdk17
        //assertThat(retriesServiceDubbo.sayRetry("retries_dubbo")).isEqualTo("retries_dubbo");
        //TODO add dubbo expected 6
        assertThat(RetriesServiceImpl.count.get()).isEqualTo(3);
    }

    @Test
    public void testLazy() {
        assertThat(lazyServiceBolt.sayLazy("lazy_bolt")).isEqualTo("lazy_bolt");
        //TODO need dubbo version upgrade later 3.0.6 for support jdk17
        //assertThat(lazyServiceDubbo.sayLazy("lazy_dubbo")).isEqualTo("lazy_dubbo");
    }

    @Test
    public void testAnnotation() {
        assertThat(annotationService.hello()).isEqualTo("Hello, Annotation");
    }

    @Test
    public void testLoadBalancerAnnotation() throws NoSuchFieldException, IllegalAccessException {
        Field consumerConfigMapField = ConsumerConfigContainer.class
            .getDeclaredField("consumerConfigMap");
        consumerConfigMapField.setAccessible(true);
        ConcurrentMap<Binding, ConsumerConfig> consumerConfigMap = (ConcurrentMap<Binding, ConsumerConfig>) consumerConfigMapField
            .get(consumerConfigContainer);

        boolean found = false;
        for (ConsumerConfig consumerConfig : consumerConfigMap.values()) {
            if ("loadbalancer".equals(consumerConfig.getUniqueId())
                && AnnotationService.class.getName().equals(consumerConfig.getInterfaceId())) {
                found = true;
                assertThat(consumerConfig.getLoadBalancer()).isEqualTo("roundRobin");
            }
        }
        assertThat(found).isTrue();

    }

    /*@Test
     public void testConnectionNum() throws NoSuchFieldException, IllegalAccessException {
         Field consumerConfigMapField = ConsumerConfigContainer.class
                 .getDeclaredField("consumerConfigMap");
         consumerConfigMapField.setAccessible(true);
         ConcurrentMap<Binding, ConsumerConfig> consumerConfigMap = (ConcurrentMap<Binding, ConsumerConfig>) consumerConfigMapField
                 .get(consumerConfigContainer);

         boolean found1 = false;
         boolean found2 = false;
         for (ConsumerConfig consumerConfig : consumerConfigMap.values()) {
             if ("connectionNum".equals(consumerConfig.getUniqueId())
                     && AnnotationService.class.getName().equals(consumerConfig.getInterfaceId())) {
                 found1 = true;
                 Assert.assertEquals(100, consumerConfig.getConnectionNum());
             } else if (connectionNumService.getClass().getName()
                     .startsWith(consumerConfig.getInterfaceId())) {
                 found2 = true;
                 Assert.assertEquals(300, consumerConfig.getConnectionNum());
             }
         }
         Assert.assertTrue("Found annotation reference", found1);
         Assert.assertTrue("Found xml reference", found2);
     }*/

    @Test
    public void testRestSwagger() throws IOException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet("http://localhost:8341/swagger/openapi");
        HttpResponse response = httpClient.execute(request);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(EntityUtils.toString(response.getEntity())).contains("/webapi/restService");
    }

    @Test
    public void testRestSwaggerAddService() throws IOException {
        List<BindingParam> bindingParams = new ArrayList<>();
        bindingParams.add(new RestBindingParam());

        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInterfaceType(AddService.class);
        serviceParam.setInstance((AddService) () -> "Hello");
        serviceParam.setBindingParams(bindingParams);

        ServiceClient serviceClient = clientFactory.getClient(ServiceClient.class);
        serviceClient.service(serviceParam);

        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet("http://localhost:8341/swagger/openapi");
        HttpResponse response = httpClient.execute(request);
        assertEquals(200, response.getStatusLine().getStatusCode());
        assertTrue(EntityUtils.toString(response.getEntity()).contains("/webapi/add_service"));
    }

    @Test
    public void testBoltSwagger() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpUriRequest request = new HttpGet("http://localhost:8341/swagger/bolt/api");
        HttpResponse response = httpClient.execute(request);
        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
        assertThat(EntityUtils.toString(response.getEntity())).contains(
            "/com.alipay.sofa.tests.rpc.bean.threadpool.ThreadPoolService/sayThreadPool");
    }

    /*@Test
     public void testGrpcSync() throws InterruptedException {
         Thread.sleep(5000);
         HelloReply reply = null;
         HelloRequest request = HelloRequest.newBuilder().setName("world").build();
         reply = sofaGreeterTripleRef.sayHello(request);
         assertThat(reply.getMessage()).isEqualTo("Hello world");
     }*/

    @Test
    public void testDisableTracing() throws NoSuchFieldException, IllegalAccessException {
        Field installedModulesField = ModuleFactory.class.getDeclaredField("INSTALLED_MODULES");
        installedModulesField.setAccessible(true);
        @SuppressWarnings("unchecked")
        ConcurrentHashMap<String, Module> modules = (ConcurrentHashMap<String, Module>) installedModulesField
            .get(ModuleFactory.class);
        assertThat(modules.get("sofaTracer")).isNull();
    }

    @Configuration
    @ImportResource("/spring/test_all.xml")
    static class RpcAllConfiguration {

    }
}
