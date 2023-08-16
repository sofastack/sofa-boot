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
package com.alipay.sofa.boot.compatibility;

import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Verification result.
 *
 * @author huzijie
 * @version VerificationResult.java, v 0.1 2023年08月03日 4:08 PM huzijie Exp $
 */
public class VerificationResult {

    private final String description;

    private final String action;

    // if OK
    private VerificationResult() {
        this.description = "";
        this.action = "";
    }

    // if not OK
    private VerificationResult(String errorDescription, String action) {
        this.description = errorDescription;
        this.action = action;
    }

    public static VerificationResult compatible() {
        return new VerificationResult();
    }

    public static VerificationResult notCompatible(String errorDescription, String action) {
        return new VerificationResult(errorDescription, action);
    }

    public boolean isNotCompatible() {
        return StringUtils.hasText(this.description) || StringUtils.hasText(this.action);
    }

    public String toErrorMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("VerificationResult:");
        stringBuilder.append("\n");
        stringBuilder.append("—— description: ");
        stringBuilder.append(description);
        stringBuilder.append("\n");
        stringBuilder.append("—— action: ");
        stringBuilder.append(action);
        return stringBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationResult that)) {
            return false;
        }
        return description.equals(that.description) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, action);
    }
}
