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
package com.alipay.sofa.common.xmap.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to identify XMap annotations.
 * <p>
 * This annotation has a single parameter "value" of type <code>int</code> that specifies the type of the annotation.
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @since 2.6.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface XMemberAnnotation {

    int NODE             = 1;
    int NODE_LIST        = 2;
    int NODE_MAP         = 3;
    int PARENT           = 4;
    int CONTENT          = 5;
    int NODE_SPRING      = 6;
    int NODE_LIST_SPRING = 7;
    int NODE_MAP_SPRING  = 8;

    /**
     * The type of the annotation.
     *
     * @return the type of the annotation.
     */
    int value();

}
