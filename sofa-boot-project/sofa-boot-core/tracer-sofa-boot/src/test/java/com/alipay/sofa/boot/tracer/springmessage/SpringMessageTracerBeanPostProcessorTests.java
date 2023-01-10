package com.alipay.sofa.boot.tracer.springmessage;

import com.alipay.sofa.tracer.plugins.message.interceptor.SofaTracerChannelInterceptor;
import org.junit.jupiter.api.Test;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.Message;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SpringMessageTracerBeanPostProcessor}.
 *
 * @author huzijie
 * @version SpringMessageTracerBeanPostProcessorTests.java, v 0.1 2023年01月09日 8:49 PM huzijie Exp $
 */
public class SpringMessageTracerBeanPostProcessorTests {

    @Test
    public void addInterceptor() {
        SpringMessageTracerBeanPostProcessor springMessageTracerBeanPostProcessor = new SpringMessageTracerBeanPostProcessor();
        springMessageTracerBeanPostProcessor.setAppName("testApp");
        AbstractMessageChannel channel = new EmptyMessageChannel();
        Object bean = springMessageTracerBeanPostProcessor.postProcessBeforeInitialization(channel, "channel");
        assertThat(bean).isEqualTo(channel);
        assertThat(channel.getInterceptors()).anyMatch(channelInterceptor -> channelInterceptor instanceof SofaTracerChannelInterceptor);
    }

    static class EmptyMessageChannel extends AbstractMessageChannel {

        @Override
        protected boolean doSend(Message<?> message, long timeout) {
            return false;
        }
    }
}
