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
package com.alipay.sofa.healthcheck.util;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

/**
 *
 * @author liangen
 * @version $Id: HealthCheckUtil.java, v 0.1 2018年03月22日 下午10:41 liangen Exp $
 */
public class HealthCheckUtil {

    public static boolean isHealth(Health health) {
        if (health == null) {
            return false;
        }

        Status status = health.getStatus();
        if (status == null) {
            return false;
        }

        return status.equals(Status.UP);
    }
}