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
package com.alipay.sofa.runtime.spi;

import java.util.concurrent.atomic.AtomicReference;

/**
 * SOFA Framework
 *
 * @author xuanbei 18/3/1
 */
public class SofaFrameworkHolder {
    /**
     * SOFA Framework
     */
    private static AtomicReference<SofaFramework> sofaFrameworkReference = new AtomicReference<>();

    /**
     * Get SOFA Framework
     *
     * @return SOFA Framework
     */
    public static SofaFramework getSofaFramework() {
        return sofaFrameworkReference.get();
    }

    /**
     * Set SOFA Framework
     *
     * @param sofaFramework SOFA Framework
     */
    public static void setSofaFramework(SofaFramework sofaFramework) {
        sofaFrameworkReference.compareAndSet(null, sofaFramework);
    }

    /**
     * contains SOFA Framework or not
     *
     * @return true or false
     */
    public static boolean containsSofaFramework() {
        return getSofaFramework() != null;
    }
}
