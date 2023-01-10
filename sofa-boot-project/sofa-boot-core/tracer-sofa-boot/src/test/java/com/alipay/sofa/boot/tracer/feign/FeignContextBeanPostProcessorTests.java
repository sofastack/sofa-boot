package com.alipay.sofa.boot.tracer.feign;

import com.alipay.sofa.tracer.plugins.springcloud.instruments.feign.SofaTracerFeignContext;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.openfeign.FeignContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link FeignContextBeanPostProcessor}.
 *
 * @author huzijie
 * @version FeignContextBeanPostProcessorTests.java, v 0.1 2023年01月09日 7:21 PM huzijie Exp $
 */
public class FeignContextBeanPostProcessorTests {

    private final FeignContextBeanPostProcessor feignContextBeanPostProcessor = new FeignContextBeanPostProcessor();

    @Test
    public void wrapFeignContext() {
        FeignContext feignContext = new FeignContext();
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(feignContext, "feignContext");
        assertThat(bean).isNotEqualTo(feignContext);
        assertThat(bean).isInstanceOf(SofaTracerFeignContext.class);
    }

    @Test
    public void skipNotFeignContext() {
        Object object = new Object();
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(object, "feignContext");
        assertThat(bean).isEqualTo(object);
        assertThat(bean).isNotInstanceOf(SofaTracerFeignContext.class);
    }

    @Test
    public void skipTransformedFeignContext() {
        FeignContext feignContext = new FeignContext();
        SofaTracerFeignContext sofaTracerFeignContext = new SofaTracerFeignContext(feignContext, null);
        Object bean = feignContextBeanPostProcessor.postProcessBeforeInitialization(sofaTracerFeignContext, "feignContext");
        assertThat(bean).isEqualTo(sofaTracerFeignContext);
    }
}
