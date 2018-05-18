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
package com.alipay.sofa.runtime.api.client;

/**
 * A SOFA programming API client factory. There are two ways to get an instance of this class in a Spring bean:
 * <ol>
 * <li>Annotate a field of the type {@link ClientFactory} with annotation
 * {@link com.alipay.sofa.runtime.api.annotation.SofaClientFactory}.</li>
 * <li>Implement the {@link com.alipay.sofa.runtime.api.aware.ClientFactoryAware} interface.</li>
 * </ol>
 *
 * @author xuanbei 18/2/28
 */
public interface ClientFactory {

    /**
     * Get an instance of a specific SOFA programming API client such as {@link ReferenceClient} or
     * {@link ServiceClient}.
     *
     * @param clazz The type of the client to get.
     * @param <T> The type of the client to get.
     * @return The instance of the client that matches the type specified.
     */
    <T> T getClient(Class<T> clazz);
}