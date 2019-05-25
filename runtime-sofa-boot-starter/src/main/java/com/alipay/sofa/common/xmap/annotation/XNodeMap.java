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
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @since 2.6.0
 */
@XMemberAnnotation(XMemberAnnotation.NODE_MAP)
@Target({ ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface XNodeMap {

    /**
     * A path expression specifying the XML node to bind to.
     *
     * @return the node xpath
     */
    String value();

    /**
     * Whether to trim text content for element nodes.
     *
     * @return
     */
    boolean trim() default true;

    /**
     * The path relative to the current node
     * (which is located by {@link com.alipay.sofa.common.xmap.annotation.XNodeMap#value()}) which contain
     * the map key to be used.
     *
     * @return
     */
    String key();

    /**
     * The type of collection object.
     *
     * @return the type of items
     */
    Class type();

    /**
     * The type of the objects in this collection.
     *
     * @return the type of items
     */
    Class componentType();

    /**
     * is or not cdata
     */
    boolean cdata() default false;

}
