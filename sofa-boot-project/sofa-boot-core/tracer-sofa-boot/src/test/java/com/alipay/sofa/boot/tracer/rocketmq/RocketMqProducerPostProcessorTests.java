package com.alipay.sofa.boot.tracer.rocketmq;

import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link RocketMqProducerPostProcessor}.
 *
 * @author huzijie
 * @version RocketMqProducerPostProcessorTests.java, v 0.1 2023年01月09日 8:24 PM huzijie Exp $
 */
public class RocketMqProducerPostProcessorTests {

    @Test
    public void customize() {
        RocketMqProducerPostProcessor rocketMqProducerPostProcessor = new RocketMqProducerPostProcessor();
        rocketMqProducerPostProcessor.setAppName("testApp");
        DefaultMQProducer producer = new DefaultMQProducer();
        Object bean = rocketMqProducerPostProcessor.postProcessAfterInitialization(producer, "producer");
        DefaultMQProducerImpl defaultMQProducerImpl = producer.getDefaultMQProducerImpl();
        assertThat(bean).isEqualTo(producer);

        Field field = ReflectionUtils.findField(DefaultMQProducerImpl.class, "sendMessageHookList");
        ReflectionUtils.makeAccessible(field);
        ArrayList<SendMessageHook> hooks = (ArrayList<SendMessageHook>) ReflectionUtils.getField(field, defaultMQProducerImpl);
        assertThat(hooks).anyMatch(hook -> hook instanceof SendMessageHook);
    }
}
