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

import com.alipay.sofa.runtime.SofaRuntimeProperties;
import com.alipay.sofa.runtime.api.ReferenceRegisterHook;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * For each normal RPC service contract, a JVM binding is added automatically by default.
 * This hook's order has value of minimum integer, the first to invoke.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/8
 */
public class JvmReferenceAddingHook implements ReferenceRegisterHook {
    @Override
    public void before(Reference reference, SofaRuntimeContext sofaRuntimeContext) {
        Binding binding = (Binding) reference.getBindings().toArray()[0];

        if (!binding.getBindingType().equals(JvmBinding.JVM_BINDING_TYPE)
            && !SofaRuntimeProperties.isDisableJvmFirst(sofaRuntimeContext)
            && reference.isJvmFirst()) {
            // Add JVM reference by default
            reference.addBinding(new JvmBinding());
        }
    }

    @Override
    public void after(Object target) {
    }

    @Override
    public int order() {
        return Integer.MIN_VALUE;
    }
}
