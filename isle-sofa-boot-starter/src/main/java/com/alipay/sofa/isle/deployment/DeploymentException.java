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
package com.alipay.sofa.isle.deployment;

/**
 * Signals a checked exception to the caller of the deploy subsystem.
 *
 * This class is thread safe.
 *
 * @author linfengqi  2011-3-26
 */
public class DeploymentException extends Exception {
    private static final long serialVersionUID = -6809659761040724153L;

    /**
     * Creates a new <code>DeploymentException</code> with the supplied error message.
     * false)}.
     *
     * @param message The exception's message
     */
    public DeploymentException(String message) {
        super(message);
    }

    /**
     * Creates a new <code>DeploymentException</code>, with the supplied error message and cause.
     *
     * @param message The exception's message.
     * @param cause The exception's cause.
     */
    public DeploymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
