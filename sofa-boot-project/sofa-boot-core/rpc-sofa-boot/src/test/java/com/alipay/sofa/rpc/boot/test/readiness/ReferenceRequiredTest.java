/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.rpc.boot.test.readiness;

import com.alipay.sofa.healthcheck.HealthCheckerProcessor;
import com.alipay.sofa.healthcheck.impl.ComponentHealthChecker;
import com.alipay.sofa.rpc.boot.test.bean.reference.RequireService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import com.alipay.sofa.runtime.api.client.ReferenceClient;
import com.alipay.sofa.runtime.api.client.param.ReferenceParam;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * ReferenceRequiredTest
 *
 * @author xunfang
 * @version ReferenceRequiredTest.java, v 0.1 2023/3/29
 */
@SpringBootApplication
@SpringBootTest(classes = ReferenceRequiredTest.class)
@RunWith(SpringRunner.class)
@ImportResource("/spring/test_reference_required.xml")
public class ReferenceRequiredTest {
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ClientFactoryBean  clientFactoryBean;

    @Autowired
    private SofaRuntimeContext sofaRuntimeContext;

    @SofaReference(uniqueId = "requireServiceAnnotation")
    private RequireService requireServiceAnnotation;

    @PostConstruct
    public void init() {
        ReferenceClient referenceClient = clientFactoryBean.getClientFactory().getClient(
                ReferenceClient.class);
        ReferenceParam<RequireService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(RequireService.class);
        referenceParam.setUniqueId("requireServiceClient");
        referenceClient.reference(referenceParam);
    }

    @Test
    public void testHealthCheckerConfig() {
        HealthCheckerProcessor healthCheckerProcessor = applicationContext
                .getBean(HealthCheckerProcessor.class);
        Map<String, Health> healthMap = new HashMap<>();
        boolean result = healthCheckerProcessor.readinessHealthCheck(healthMap);
        Assert.assertFalse(result);

        final Collection<ComponentInfo> componentInfos = sofaRuntimeContext.getComponentManager().getComponents();
        componentInfos.forEach(componentInfo -> {
            Assert.assertFalse(componentInfo.isHealthy().isHealthy());
        });
        Assert.assertTrue(componentInfos.stream().anyMatch(componentInfo -> componentInfo.getName().getName().contains("requireServiceXml")));
        Assert.assertTrue(componentInfos.stream().anyMatch(componentInfo -> componentInfo.getName().getName().contains("requireServiceAnnotation")));
        Assert.assertTrue(componentInfos.stream().anyMatch(componentInfo -> componentInfo.getName().getName().contains("requireServiceClient")));
    }

    @TestConfiguration
    static class Configuration {
        @Bean
        public ComponentHealthChecker sofaComponentHealthChecker(SofaRuntimeContext sofaRuntimeContext) {
            return new ComponentHealthChecker(sofaRuntimeContext);
        }
    }
}
