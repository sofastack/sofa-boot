package com.alipay.sofa.boot.tracer.resttemplate;

import com.sofa.alipay.tracer.plugins.rest.interceptor.RestTemplateInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RestTemplateBeanPostProcessor}.
 *
 * @author huzijie
 * @version RestTemplateBeanPostProcessorTests.java, v 0.1 2023年01月10日 8:00 PM huzijie Exp $
 */
public class RestTemplateBeanPostProcessorTests {

    @Test
    public void enhanceRestTemplate() {
        RestTemplateBeanPostProcessor springMessageTracerBeanPostProcessor = new RestTemplateBeanPostProcessor(new RestTemplateEnhance());
        RestTemplate restTemplate = new RestTemplate();
        Object bean = springMessageTracerBeanPostProcessor.postProcessAfterInitialization(restTemplate, "restTemplate");
        assertThat(bean).isEqualTo(restTemplate);
        assertThat(restTemplate.getInterceptors()).anyMatch(interceptor -> interceptor instanceof RestTemplateInterceptor);

        springMessageTracerBeanPostProcessor.postProcessAfterInitialization(restTemplate, "restTemplate");
        assertThat(restTemplate.getInterceptors()).anyMatch(advice -> advice instanceof RestTemplateInterceptor).hasSize(1);
    }
}
