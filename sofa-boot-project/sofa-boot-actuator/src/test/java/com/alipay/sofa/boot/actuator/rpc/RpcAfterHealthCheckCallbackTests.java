package com.alipay.sofa.boot.actuator.rpc;

import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
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
 * Tests for {@link RpcAfterHealthCheckCallback}.
 *
 * @author huzijie
 * @version RpcAfterHealthCheckCallbackTests.java, v 0.1 2023年02月22日 10:29 AM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class RpcAfterHealthCheckCallbackTests {

    @InjectMocks
    private RpcAfterHealthCheckCallback rpcAfterHealthCheckCallback;

    @Mock
    private RpcStartApplicationListener applicationListener;

    @Test
    public void listenerSuccess() {
        Mockito.doReturn(true).when(applicationListener).isSuccess();
        assertThat(rpcAfterHealthCheckCallback.onHealthy(null))
                .isEqualTo(Health.up().build());
    }

    @Test
    public void listenerFail() {
        Mockito.doReturn(false).when(applicationListener).isSuccess();
        Health result = rpcAfterHealthCheckCallback.onHealthy(null);
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
        assertThat(result.getDetails().toString()).isEqualTo("{Reason=Rpc service start fail}");
    }
}
