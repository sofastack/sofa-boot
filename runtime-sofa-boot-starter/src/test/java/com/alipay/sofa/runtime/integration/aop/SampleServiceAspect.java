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
package com.alipay.sofa.runtime.integration.aop;

import org.aspectj.lang.ProceedingJoinPoint;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author xuanbei
 * @since 2.4.5
 */
public class SampleServiceAspect {
    private static AtomicBoolean aspectInvoked = new AtomicBoolean(false);

    public Object doAround(ProceedingJoinPoint point) throws Throwable {
        aspectInvoked.set(true);
        return point.proceed();
    }

    public static boolean isAspectInvoked() {
        return aspectInvoked.getAndSet(false);
    }
}
