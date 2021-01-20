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
package com.alipay.sofa.runtime.service.helper;

import com.alipay.sofa.runtime.api.ServiceRegisterHook;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import org.springframework.core.Ordered;

/**
 * For each service contract, add a JVM binding if it doesn't have any binding.
 * The hook's order has value of minimum integer, the first to invoke.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/8
 */
public class JvmServiceAddingHook implements ServiceRegisterHook {
    @Override
    public void before(Service service, SofaRuntimeContext sofaRuntimeContext) {
        if (service.getBindings().size() == 0) {
            service.getBindings().add(new JvmBinding());
        }
    }

    @Override
    public void after(Service service, SofaRuntimeContext sofaRuntimeContext) {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
