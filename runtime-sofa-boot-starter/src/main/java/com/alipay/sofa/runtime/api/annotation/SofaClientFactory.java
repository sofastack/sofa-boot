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
package com.alipay.sofa.runtime.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Annotation to get an instance of {@link com.alipay.sofa.runtime.api.client.ClientFactory} or an instance of specific
 * client in {@link com.alipay.sofa.runtime.api.client.ClientFactory} such as
 * {@link com.alipay.sofa.runtime.api.client.ServiceClient} in a Spring bean.
 * </p>
 *
 * <p>
 * Sample of getting an instance of {@link com.alipay.sofa.runtime.api.client.ClientFactory} in a Spring bean:
 *
 * <code>
 * public class ClientAnnotatedBean {
 *
 *     &#064;SofaClientFactory
 *     private ClientFactory clientFactory;
 * }
 * </code>
 *
 * </p>
 *
 * <p>
 * Sample of getting an instance of specific client in {@link com.alipay.sofa.runtime.api.client.ClientFactory} in a
 * Spring bean:
 *
 * <code>
 * public class ClientAnnotatedBean {
 *
 *     &#064;SofaClientFactory
 *     private ServiceClient serviceClient;
 * }
 * </code>
 *
 * </p>
 *
 * @author xuanbei 18/2/28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SofaClientFactory {
}
