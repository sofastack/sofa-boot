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


package com.alipay.sofa.healthcheck.core;

/**
 *
 * @author liangen
 * @version $Id: HealthCheckerType.java, v 0.1 2017年10月24日 上午10:31 liangen Exp $
 */
public enum HealthCheckerType {

    /**
     * Retry the checker.
     *
     * The interface will be retried when it fails and the number of retries
     * will be configured by the user 20 times.
     * If the maximum number of retries is also checked for success by default,
     * the check process and final check results are not affected, and only
     * the check log is printed. If the user open com.alipay.sofa.healthcheck.strict.com ponent parameters,
     * the will to the situation of the actual inspection as test results, affect the inspection process
     * and final inspection results. A reference service such as RPC is usually recommended as the
     * implementation of this interface, because the process will have a situation where the address is
     * not timely.
     */
    RETRY_ON_FAILED_CHECK("retry_on_failed_check"),

    /**
     * Un Retry the checker.
     *
     * If fail to check, won't retry.
     */
    UN_RETRY_ON_FAILED_CHECK("un_retry_on_failed_check");

    private final String typeName;

    HealthCheckerType(String typeName) {
        this.typeName = typeName;
    }
}