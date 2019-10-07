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

import org.w3c.dom.Element;

import com.alipay.sofa.common.xmap.Context;
import com.alipay.sofa.common.xmap.Path;
import com.alipay.sofa.common.xmap.XAnnotatedMember;
import com.alipay.sofa.common.xmap.XGetter;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.common.xmap.XSetter;
import com.alipay.sofa.common.xmap.spring.XNodeSpring;

/**
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XAnnotatedSpring extends XAnnotatedMember {

    public XAnnotatedSpringObject xaso;

    protected XAnnotatedSpring(XMap xmap, XSetter setter, XGetter getter) {
        super(xmap, setter, getter);
    }

    public XAnnotatedSpring(XMap xmap, XSetter setter, XGetter getter, XNodeSpring anno,
                            XAnnotatedSpringObject xaso) {
        super(xmap, setter, getter);
        path = new Path(anno.value());
        trim = anno.trim();
        type = setter.getType();
        this.xaso = xaso;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Context ctx, Element base) throws Exception {
        // scalar field
        if (type == Element.class) {
            // allow DOM elements as values
            return base;
        }
        return XMapSpringUtil.getSpringOjbect(this, xaso.getApplicationContext(), base);
    }
}
