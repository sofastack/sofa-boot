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
package com.alipay.sofa.common.xmap.annotation.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import com.alipay.sofa.common.xmap.DOMHelper;
import com.alipay.sofa.common.xmap.XAnnotatedMember;

/**
 * XMapSpring util
 * @author xi.hux@alipay.com
 * @since 2.6.0
 *
 */
public class XMapSpringUtil {

    /**
     * Get spring object
     * @param type type
     * @param beanName bean name
     * @param applicationContext application context
     * @return spring object
     */
    public static Object getSpringObject(Class type, String beanName,
                                         ApplicationContext applicationContext) {
        if (type == Resource.class) {
            return applicationContext.getResource(beanName);
        } else {
            return applicationContext.getBean(beanName, type);
        }
    }

    /**
     * Get spring object
     * @param xam XAnnotated member
     * @param applicationContext application context
     * @param base element base
     * @return
     */
    public static Object getSpringOjbect(XAnnotatedMember xam,
                                         ApplicationContext applicationContext, Element base) {
        String val = DOMHelper.getNodeValue(base, xam.path);
        if (val != null && val.length() > 0) {
            if (xam.trim) {
                val = val.trim();
            }
            return getSpringObject(xam.type, val, applicationContext);
        }
        return null;
    }
}
