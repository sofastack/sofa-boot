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
package com.alipay.sofa.runtime.filter;

import org.springframework.core.Ordered;

/**
 * Filter for JVM service invoking.
 * Multiple filters are called in ascending order.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public interface JvmFilter extends Ordered {
    /**
     * This method is called before the actual JVM service invoking.
     * If filter chain is not interrupted, setting of <code>invokeResult</code> of context
     * makes no sense here.
     * @param context JVM invoking context
     * @return whether to continue processing
     */
    boolean before(JvmFilterContext context);

    /**
     * This method is called after the actual JVM service invoking.
     * Filter can replace the <code>invokeResult</code> of context to do something nasty.
     * @param context JVM invoking context
     * @return whether to continue processing
     */
    boolean after(JvmFilterContext context);
}
