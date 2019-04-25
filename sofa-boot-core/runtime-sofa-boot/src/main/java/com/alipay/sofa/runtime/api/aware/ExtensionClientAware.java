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

import com.alipay.sofa.runtime.api.client.ExtensionClient;

/**
 * Interface used to implemented by a Spring bean who want to get an instance of {@link ExtensionClient}. Sample usage:
 *
 * <pre>
 *
 * public class ExtensionClientBean implements ExtensionClientAware {
 *
 *     private ExtensionClient extensionClient;
 *
 *     &#064;Override
 *     public void setExtensionClient(ExtensionClient extensionClient) {
 *         this.clientFactory = extensionClient;
 *     }
 *
 *     public ExtensionClient getClientFactory() {
 *         return extensionClient;
 *     }
 * }
 *
 * </pre>
 *
 * @author ruoshan
 * @since 2.6.0
 */
public interface ExtensionClientAware {

    /**
     * Set the instance of {@link ExtensionClient} to the Spring bean that implement this interface.
     *
     * @param extensionClient ExtensionClient The instance of {@link ExtensionClient}
     */
    void setExtensionClient(ExtensionClient extensionClient);
}
