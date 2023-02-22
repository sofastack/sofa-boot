package com.alipay.sofa.smoke.tests.runtime.spring;

import com.alipay.sofa.runtime.api.annotation.SofaService;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;
import com.alipay.sofa.runtime.api.component.ComponentName;
import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.spi.component.ComponentNameFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeManager;
import com.alipay.sofa.runtime.spring.bean.SofaBeanNameGenerator;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.service.SampleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SofaService}.
 *
 * @author huzijie
 * @version SofaServiceAnnotationTests.java, v 0.1 2023年02月22日 11:21 AM huzijie Exp $
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(SofaServiceAnnotationTests.ServiceBeanAnnotationConfiguration.class)
@TestPropertySource(properties = {"methodUniqueId=a", "bindingType=jvm"} )
public class SofaServiceAnnotationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private SofaRuntimeManager sofaRuntimeManager;

    @Test
    public void checkFactoryBean() {
        String beanName = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                "a", "methodSampleService");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(applicationContext.getBean(beanName)).isInstanceOf(ServiceImpl.class);

        beanName = SofaBeanNameGenerator.generateSofaServiceBeanName(SampleService.class,
                null, "classSampleService");
        assertThat(applicationContext.containsBean(beanName)).isTrue();
        assertThat(applicationContext.getBean(beanName)).isInstanceOf(ServiceImpl.class);
    }

    @Test
    public void checkServiceComponent() {
        ComponentName componentName = ComponentNameFactory.createComponentName(
                ServiceComponent.SERVICE_COMPONENT_TYPE, SampleService.class,
                "a");
        assertThat(sofaRuntimeManager.getComponentManager().getComponentInfo(componentName)).isNotNull();

        componentName = ComponentNameFactory.createComponentName(
                ServiceComponent.SERVICE_COMPONENT_TYPE, SampleService.class,
                null);
        assertThat(sofaRuntimeManager.getComponentManager().getComponentInfo(componentName)).isNotNull();
    }


    @Configuration
    @Import(ClassSampleService.class)
    static class ServiceBeanAnnotationConfiguration {

        @SofaService(uniqueId = "${methodUniqueId}")
        @Bean
        public SampleService methodSampleService() {
            return () -> "methodSampleService";
        }
    }

    @SofaService(bindings = @SofaServiceBinding(bindingType = "${bindingType}"))
    @Component("classSampleService")
    static class ClassSampleService implements SampleService {

        @Override
        public String service() {
            return "classSampleService";
        }
    }
}
