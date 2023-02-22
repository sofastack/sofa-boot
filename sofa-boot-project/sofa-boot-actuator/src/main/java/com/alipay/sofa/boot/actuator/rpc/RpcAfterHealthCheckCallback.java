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

import com.alipay.sofa.boot.actuator.health.ReadinessCheckCallback;
import com.alipay.sofa.rpc.boot.context.RpcStartApplicationListener;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.context.ApplicationContext;
import org.springframework.core.PriorityOrdered;

/**
 * Implementation of {@link ReadinessCheckCallback} to publish sofa rpc services.
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class RpcAfterHealthCheckCallback implements ReadinessCheckCallback, PriorityOrdered {

    private final RpcStartApplicationListener rpcStartApplicationListener;

    public RpcAfterHealthCheckCallback(RpcStartApplicationListener rpcStartApplicationListener) {
        this.rpcStartApplicationListener = rpcStartApplicationListener;
    }

    @Override
    public Health onHealthy(ApplicationContext applicationContext) {
        Health.Builder builder = new Health.Builder();

        rpcStartApplicationListener.publishRpcStartEvent();

        if (rpcStartApplicationListener.isSuccess()) {
            return builder.status(Status.UP).build();
        } else {
            return builder.status(Status.DOWN).withDetail("Reason", "Rpc service start fail")
                .build();
        }
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.LOWEST_PRECEDENCE;
    }
}
