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
package com.alipay.sofa.runtime.impl;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alipay.sofa.runtime.spi.client.ClientFactoryInternal;

/**
 * Default implementation of {@link ClientFactoryInternal}.
 *
 * @author xuanbei 18/3/9
 */
public class ClientFactoryImpl implements ClientFactoryInternal {

    private final Map<Class<?>, Object> clients = new ConcurrentHashMap<>(8);

    @Override
    public void registerClient(Class<?> clientType, Object clientInstance) {
        if (clients.containsKey(clientType)) {
            return;
        }

        clients.put(clientType, clientInstance);
    }

    @Override
    public void destroy() {
        clients.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getClient(Class<T> clazz) {
        return (T) clients.get(clazz);
    }

    public Collection<Class<?>> getAllClientTypes() {
        return clients.keySet();
    }
}
