package com.alipay.sofa.boot.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ManualReadinessCallbackEndPoint}.
 *
 * @author huzijie
 * @version ManualReadinessCallbackEndPointTests.java, v 0.1 2023年01月04日 3:58 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ManualReadinessCallbackEndPointTests {

    @InjectMocks
    private ManualReadinessCallbackEndPoint manualReadinessCallbackEndPoint;

    @Mock
    private ReadinessCheckListener readinessCheckListener;

    @Test
    public void testTrigger() {
        ReadinessCheckListener.ManualReadinessCallbackResult mockResult = new ReadinessCheckListener.ManualReadinessCallbackResult(
                true, "trigger Success");
        Mockito.doReturn(mockResult).when(readinessCheckListener).triggerReadinessCallback();
        ReadinessCheckListener.ManualReadinessCallbackResult result = manualReadinessCallbackEndPoint.trigger();
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getDetails()).isEqualTo("trigger Success");
    }
}
