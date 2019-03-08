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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.api.client.param.BindingParam;

/**
 * @author qilong.zql
 * @since 3.1.2
 */
public class JvmBindingParam implements BindingParam {

    private boolean serialize = true;

    @Override
    public BindingType getBindingType() {
        return JvmBinding.JVM_BINDING_TYPE;
    }

    /**
     * whether ignore serialize when invoke across ClassLoader.
     *
     * @return
     */
    public boolean isSerialize() {
        return serialize;
    }

    /**
     * Set whether ignore serialize when invoke across ClassLoader.
     *
     * @param serialize
     */
    public JvmBindingParam setSerialize(boolean serialize) {
        this.serialize = serialize;
        return this;
    }
}