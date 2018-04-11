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

import com.alipay.sofa.runtime.api.client.param.ServiceParam;

/**
 * This class is used to create a SOFA service. There are to ways to get an instance of this class in a Spring bean:
 * <ol>
 * <li>Get the instance of {@link ClientFactory}, then invoke {@link ClientFactory#getClient(Class)} to get the instance
 * of this class.</li>
 * <li>Annotate a field of type {@link ServiceClient} with annotation
 * {@link com.alipay.sofa.runtime.api.annotation.SofaClientFactory}.</li>
 * </ol>
 *
 * @author xuanbei 18/2/28
 */
public interface ServiceClient {

    /**
     * Creating a SOFA service, sample usage:
     *
     * <pre>
     * ServiceParam serviceParam = new ServiceParam();
     * SampleService sampleService = new SampleServiceImpl();
     * serviceParam.setInstance(sampleService);
     * serviceClient.service(serviceParam);
     * </pre>
     *
     * @param serviceParam The service parameter used to create a SOFA service.
     */
    void service(ServiceParam serviceParam);

    /**
     * Equivalent to {@link ServiceClient#removeService(Class, String, int)} except that the uniqueId parameter is
     * default to an empty string.
     *
     * @param interfaceClass the interface type to be removed
     * @param millisecondsToDelay after the specified time, then service would be removed
     */
    void removeService(Class<?> interfaceClass, int millisecondsToDelay);

    /**
     * Remove a service component from SOFA runtime.
     *
     * @param interfaceClass the interface type of the service component
     * @param uniqueId the uniqueId of the service component
     * @param millisecondsToDelay milliseconds to delay while remove a service component, only non-negative value is
     * accepted. it is useful when remove a RPC service component since remove registry info from config server is a
     * async-procedure
     */
    void removeService(Class<?> interfaceClass, String uniqueId, int millisecondsToDelay);
}
