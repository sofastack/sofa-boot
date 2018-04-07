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
package com.alipay.sofa.runtime.api;

/**
 * service validation exception
 *
 * @author xuanbei 18/2/28
 */
public class ServiceValidationException extends RuntimeException {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 2142600090521376989L;

    /**
     * Override constructor from RuntimeException.
     *
     * @see RuntimeException
     */
    public ServiceValidationException() {
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceValidationException(String message) {
        super(message);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param message passed to RuntimeException
     * @param cause   passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Override constructor from RuntimeException.
     *
     * @param cause passed to RuntimeException
     * @see RuntimeException
     */
    public ServiceValidationException(Throwable cause) {
        super(cause);
    }
}
