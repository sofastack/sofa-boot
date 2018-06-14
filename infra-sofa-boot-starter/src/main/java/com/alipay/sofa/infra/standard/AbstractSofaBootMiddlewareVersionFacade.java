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
package com.alipay.sofa.infra.standard;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * SofaBootMiddlewareVersionEndpoint
 *
 * @author yangguanchao
 * @since 2018/01/05
 */
public abstract class AbstractSofaBootMiddlewareVersionFacade {

    /**
     * Get the name of the endpoint
     *
     * @return endpoint name
     */
    public abstract String getName();

    /**
     * Get the version of the Endpoint
     *
     * @return current version
     */
    public abstract String getVersion();

    /**
     * Get the authors of the Endpoint
     *
     * @return author list
     */
    public abstract List<String> getAuthors();

    /**
     * Get the document url of the Endpoint
     *
     * @return docs
     */
    public abstract String getDocs();

    /***
     * Get the SOFABoot Middleware Runtime Info. Middleware Extension Info
     * @return Middleware Runtime Info
     */
    public Map<String, Object> getRuntimeInfo() {
        return Collections.EMPTY_MAP;
    }
}
