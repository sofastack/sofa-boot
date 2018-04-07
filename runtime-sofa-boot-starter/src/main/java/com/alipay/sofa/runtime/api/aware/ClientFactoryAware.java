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
package com.alipay.sofa.runtime.api.aware;

import com.alipay.sofa.runtime.api.client.ClientFactory;

/**
 * Interface used to implemented by a Spring bean who want to get an instance of {@link ClientFactory}. Sample usage:
 *
 * <pre>
 *
 * public class ClientFactoryBean implements ClientFactoryAware {
 *
 *     private ClientFactory clientFactory;
 *
 *     &#064;Override
 *     public void setClientFactory(ClientFactory clientFactory) {
 *         this.clientFactory = clientFactory;
 *     }
 *
 *     public ClientFactory getClientFactory() {
 *         return clientFactory;
 *     }
 * }
 * </pre>
 *
 * @author xuanbei 18/2/28
 */
public interface ClientFactoryAware {

    /**
     * Set the instance of {@link ClientFactory} to the Spring bean that implement this interface.
     *
     * @param clientFactory ClientFactory The instance of {@link ClientFactory}
     */
    void setClientFactory(ClientFactory clientFactory);
}
