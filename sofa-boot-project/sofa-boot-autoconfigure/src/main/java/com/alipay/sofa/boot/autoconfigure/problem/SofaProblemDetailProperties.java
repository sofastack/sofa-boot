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
package com.alipay.sofa.boot.autoconfigure.problem;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Configuration properties for SOFABoot problem detail support.
 *
 * @author OpenAI
 */
@ConfigurationProperties("sofa.boot.problem-detail")
public class SofaProblemDetailProperties {

    /**
     * Whether problem detail support is enabled.
     */
    private boolean enabled            = true;

    /**
     * Default type URI for unmapped exceptions.
     */
    private URI     defaultType        = URI.create("about:blank");

    /**
     * Base URI used to generate problem type links for SOFA exceptions.
     */
    private URI     typeBaseUri        = URI.create("https://sofastack.io/errors/");

    /**
     * Whether stack traces should be exposed in the response.
     */
    private boolean includeStackTrace;

    /**
     * Whether service metadata should be included in the response.
     */
    private boolean includeServiceInfo = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public URI getDefaultType() {
        return defaultType;
    }

    public void setDefaultType(URI defaultType) {
        this.defaultType = defaultType;
    }

    public URI getTypeBaseUri() {
        return typeBaseUri;
    }

    public void setTypeBaseUri(URI typeBaseUri) {
        this.typeBaseUri = typeBaseUri;
    }

    public boolean isIncludeStackTrace() {
        return includeStackTrace;
    }

    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }

    public boolean isIncludeServiceInfo() {
        return includeServiceInfo;
    }

    public void setIncludeServiceInfo(boolean includeServiceInfo) {
        this.includeServiceInfo = includeServiceInfo;
    }
}
