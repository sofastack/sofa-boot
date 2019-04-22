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

import com.alipay.sofa.runtime.api.client.param.ReferenceParam;

/**
 * This class is used to create a SOFA reference. There are to ways to get an instance of this class in a Spring bean:
 * <ol>
 * <li>Get the instance of {@link ClientFactory}, then invoke {@link ClientFactory#getClient(Class)} to get the instance
 * of this class.</li>
 * <li>Annotate a field of type {@link ReferenceClient} with annotation
 * {@link com.alipay.sofa.runtime.api.annotation.SofaClientFactory}.</li>
 * </ol>
 *
 * @author xuanbei 18/2/28
 */
public interface ReferenceClient {

    /**
     * Create a SOFA reference, sample usage:
     *
     * <pre>
     * ReferenceParam&lt;SampleService&gt; referenceParam = new ReferenceParam&lt;SampleService&gt;();
     * referenceParam3.setInterfaceType(SampleService.class);
     * SampleService sampleService3 = referenceClient.reference(referenceParam);
     * </pre>
     *
     * @param referenceParam The parameter of the SOFA reference to create.
     * @param <T> The type of the SOFA reference to create.
     * @return The SOFA reference to create.
     */
    <T> T reference(ReferenceParam<T> referenceParam);

    /**
     * Remove a specified SOFA reference
     *
     * @param referenceParam The parameter of the SOFA reference to remove
     * @param <T> The type of the SOFA reference to remove
     */
    <T> void removeReference(ReferenceParam<T> referenceParam);

    /**
     * Remove SOFA references on service dimension
     * @param interfaceClass the interface type of the reference component
     */
    void removeReference(Class<?> interfaceClass);

    /**
     * Remove SOFA references on service dimension
     * @param interfaceClass the interface type of the reference component
     * @param uniqueId the uniqueId of the reference component
     */
    void removeReference(Class<?> interfaceClass, String uniqueId);

}
