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
package com.alipay.sofa.boot.autoconfigure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;

/**
 * Configuration properties for SOFA ProblemDetail support.
 */
@ConfigurationProperties(SofaProblemDetailProperties.PREFIX)
public class SofaProblemDetailProperties {

    public static final String PREFIX             = "sofa.web.problem-detail";

    private boolean            enabled            = true;

    private URI                defaultType        = URI.create("about:blank");

    private boolean            includeStackTrace  = false;

    private boolean            includeServiceInfo = true;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public URI getDefaultType() {
        return this.defaultType;
    }

    public void setDefaultType(URI defaultType) {
        this.defaultType = defaultType;
    }

    public boolean isIncludeStackTrace() {
        return this.includeStackTrace;
    }

    public void setIncludeStackTrace(boolean includeStackTrace) {
        this.includeStackTrace = includeStackTrace;
    }

    public boolean isIncludeServiceInfo() {
        return this.includeServiceInfo;
    }

    public void setIncludeServiceInfo(boolean includeServiceInfo) {
        this.includeServiceInfo = includeServiceInfo;
    }
}
