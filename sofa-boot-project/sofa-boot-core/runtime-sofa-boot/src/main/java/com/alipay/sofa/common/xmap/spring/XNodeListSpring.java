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
package com.alipay.sofa.common.xmap.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.alipay.sofa.common.xmap.annotation.XMemberAnnotation;

/**
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
@XMemberAnnotation(XMemberAnnotation.NODE_LIST_SPRING)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XNodeListSpring {

    /**
     * Get the node xpath
     *
     * @return the node xpath
     */
    String value();

    /**
     * Get whether need to trim or not
     *
     * @return trim or not
     */
    boolean trim() default true;

    /**
     * Get the type of items
     *
     * @return the type of items
     */
    Class type();

    /**
     * Get the type of items
     *
     * @return the type of items
     */
    Class componentType();
}
