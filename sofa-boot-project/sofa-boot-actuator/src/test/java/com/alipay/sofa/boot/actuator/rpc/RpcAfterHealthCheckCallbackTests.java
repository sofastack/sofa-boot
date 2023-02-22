/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
        assertThat(rpcAfterHealthCheckCallback.onHealthy(null)).isEqualTo(Health.up().build());
    }

    @Test
    public void listenerFail() {
        Mockito.doReturn(false).when(applicationListener).isSuccess();
        Health result = rpcAfterHealthCheckCallback.onHealthy(null);
        assertThat(result.getStatus()).isEqualTo(Status.DOWN);
        assertThat(result.getDetails().toString()).isEqualTo("{Reason=Rpc service start fail}");
    }
}
