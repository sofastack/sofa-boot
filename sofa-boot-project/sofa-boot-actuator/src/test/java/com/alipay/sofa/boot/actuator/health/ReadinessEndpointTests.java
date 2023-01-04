package com.alipay.sofa.boot.actuator.health;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link ReadinessEndpoint}.
 *
 * @author huzijie
 * @version ReadinessEndpointTests.java, v 0.1 2023年01月04日 12:06 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class ReadinessEndpointTests {

    @InjectMocks
    private ReadinessEndpoint readinessEndpoint;

    @Mock
    private ReadinessCheckListener readinessCheckListener;

    @Test
    public void testHealthWithOutDetails() {
        Health health = Health.down().withDetail("db", "error").build();
        Mockito.doReturn(health).when(readinessCheckListener).aggregateReadinessHealth();
        Health result = readinessEndpoint.health("false");
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
        assertThat(result.getDetails()).isEmpty();
    }

    @Test
    public void testHealthWithDetails() {
        Health health = Health.up().withDetail("db", "success").build();
        Mockito.doReturn(health).when(readinessCheckListener).aggregateReadinessHealth();
        Health result =readinessEndpoint.health("true");
        assertThat(result.getStatus()).isEqualTo(Status.UP);
        assertThat(result.getDetails().toString()).contains("db=success");
    }
}
