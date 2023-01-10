package com.alipay.sofa.boot.tracer.mongodb;

import com.alipay.sofa.tracer.plugins.mongodb.SofaTracerCommandListener;
import com.mongodb.MongoClientSettings;
import com.mongodb.event.CommandListener;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link SofaTracerCommandListenerCustomizer}.
 *
 * @author huzijie
 * @version SofaTracerCommandListenerCustomizerTests.java, v 0.1 2023年01月09日 7:46 PM huzijie Exp $
 */
public class SofaTracerCommandListenerCustomizerTests {

    @Test
    public void customize() {
        SofaTracerCommandListenerCustomizer commandListenerCustomizer = new SofaTracerCommandListenerCustomizer();
        commandListenerCustomizer.setAppName("testApp");
        MongoClientSettings.Builder builder = MongoClientSettings.builder();
        commandListenerCustomizer.customize(builder);
        MongoClientSettings mongoClientSettings = builder.build();
        List<CommandListener> listeners = mongoClientSettings.getCommandListeners();
        assertThat(listeners).anyMatch(listener -> listener instanceof SofaTracerCommandListener);
    }
}
