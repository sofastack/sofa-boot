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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.springframework.context.ApplicationContext;

import com.alipay.sofa.common.xmap.XAnnotatedMember;
import com.alipay.sofa.common.xmap.XAnnotatedObject;
import com.alipay.sofa.common.xmap.XFieldGetter;
import com.alipay.sofa.common.xmap.XFieldSetter;
import com.alipay.sofa.common.xmap.XGetter;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.common.xmap.XMethodGetter;
import com.alipay.sofa.common.xmap.XMethodSetter;
import com.alipay.sofa.common.xmap.XSetter;
import com.alipay.sofa.common.xmap.annotation.XMemberAnnotation;
import com.alipay.sofa.common.xmap.annotation.XObject;
import com.alipay.sofa.common.xmap.spring.XNodeListSpring;
import com.alipay.sofa.common.xmap.spring.XNodeMapSpring;
import com.alipay.sofa.common.xmap.spring.XNodeSpring;

/**
 * Integrate XMap with spring
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XMapSpring extends XMap {

    @SuppressWarnings("unchecked")
    public XAnnotatedObject register(Class klass, ApplicationContext applicationContext) {
        XAnnotatedObject xao = objects.get(klass);
        if (xao == null) { // avoid scanning twice
            XObject xob = checkObjectAnnotation(klass, klass.getClassLoader());
            if (xob != null) {
                xao = new XAnnotatedSpringObject(this, klass, xob, applicationContext);
                objects.put(xao.klass, xao);
                scan(xao);
                String key = xob.value();
                if (key.length() > 0) {
                    roots.put(xao.path.path, xao);
                }
            }
        }
        return xao;
    }

    /**
     * Scan Field
     *
     * @param xob XAnnotated Object
     */
    private void scan(XAnnotatedObject xob) {
        Field[] fields = xob.klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation anno = checkMemberAnnotation(field);
            if (anno != null) {
                XAnnotatedMember member = createFieldMember(field, anno);
                if (member == null) {
                    member = createExtendFieldMember(field, anno, xob);
                }
                xob.addMember(member);
            }
        }

        Method[] methods = xob.klass.getDeclaredMethods();
        for (Method method : methods) {
            // we accept only methods with one parameter
            Class[] paramTypes = method.getParameterTypes();
            if (paramTypes.length != 1) {
                continue;
            }
            Annotation anno = checkMemberAnnotation(method);
            if (anno != null) {
                XAnnotatedMember member = createMethodMember(method, xob.klass, anno);
                if (member == null) {
                    member = createExtendMethodMember(method, anno, xob);
                }
                xob.addMember(member);
            }
        }
    }

    private XAnnotatedMember createExtendFieldMember(Field field, Annotation annotation,
                                                     XAnnotatedObject xob) {
        XSetter setter = new XFieldSetter(field);
        XGetter getter = new XFieldGetter(field);
        return createExtendMember(annotation, setter, getter, xob);
    }

    public final XAnnotatedMember createExtendMethodMember(Method method, Annotation annotation,
                                                           XAnnotatedObject xob) {
        XSetter setter = new XMethodSetter(method);
        XGetter getter = new XMethodGetter(null, null, null);
        return createExtendMember(annotation, setter, getter, xob);
    }

    private XAnnotatedMember createExtendMember(Annotation annotation, XSetter setter,
                                                XGetter getter, XAnnotatedObject xob) {
        XAnnotatedMember member = null;
        int type = annotation.annotationType().getAnnotation(XMemberAnnotation.class).value();
        if (type == XMemberAnnotation.NODE_SPRING) {
            member = new XAnnotatedSpring(this, setter, getter, (XNodeSpring) annotation,
                (XAnnotatedSpringObject) xob);
        } else if (type == XMemberAnnotation.NODE_LIST_SPRING) {
            member = new XAnnotatedListSpring(this, setter, getter, (XNodeListSpring) annotation,
                (XAnnotatedSpringObject) xob);
        } else if (type == XMemberAnnotation.NODE_MAP_SPRING) {
            member = new XAnnotatedMapSpring(this, setter, getter, (XNodeMapSpring) annotation,
                (XAnnotatedSpringObject) xob);
        }
        return member;
    }
}
