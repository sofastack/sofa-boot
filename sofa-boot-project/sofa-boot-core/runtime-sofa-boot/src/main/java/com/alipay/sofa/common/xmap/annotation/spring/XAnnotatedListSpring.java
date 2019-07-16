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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.Context;
import com.alipay.sofa.common.xmap.DOMHelper;
import com.alipay.sofa.common.xmap.Path;
import com.alipay.sofa.common.xmap.XAnnotatedList;
import com.alipay.sofa.common.xmap.XAnnotatedMember;
import com.alipay.sofa.common.xmap.XGetter;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.common.xmap.XSetter;
import com.alipay.sofa.common.xmap.spring.XNodeListSpring;

/**
 *
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public class XAnnotatedListSpring extends XAnnotatedList {

    /**
     * dom visitor
     */
    protected static final ElementValueVisitor   elementVisitor   = new ElementValueVisitor();
    protected static final AttributeValueVisitor attributeVisitor = new AttributeValueVisitor();

    private XAnnotatedSpringObject               xaso;

    protected XAnnotatedListSpring(XMap xmap, XSetter setter, XGetter getter) {
        super(xmap, setter, getter);
    }

    public XAnnotatedListSpring(XMap xmap, XSetter setter, XGetter getter, XNodeListSpring anno,
                                XAnnotatedSpringObject xaso) {
        super(xmap, setter, getter);
        path = new Path(anno.value());
        trim = anno.trim();
        type = anno.type();
        componentType = anno.componentType();
        this.xaso = xaso;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Context ctx, Element base) throws Exception {
        ArrayList<Object> values = new ArrayList<Object>();
        if (path.attribute != null) {
            // attribute list
            DOMHelper.visitNodes(ctx, this, base, path, attributeVisitor, values);
        } else {
            // element list
            DOMHelper.visitNodes(ctx, this, base, path, elementVisitor, values);
        }

        if (type != ArrayList.class) {
            if (type.isArray() && !componentType.isPrimitive()) {
                values.toArray((Object[]) Array.newInstance(componentType, values.size()));
            } else {
                Collection col = (Collection) type.newInstance();
                col.addAll(values);
                return col;
            }
        }
        return values;
    }

    public void setXaso(XAnnotatedSpringObject xaso) {
        this.xaso = xaso;
    }

    public XAnnotatedSpringObject getXaso() {
        return xaso;
    }
}

class ElementValueVisitor extends DOMHelper.NodeVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, Collection<Object> result) {
        String val = node.getTextContent();
        if (val != null && val.length() > 0) {
            if (xam.trim)
                val = val.trim();
            Object object = XMapSpringUtil.getSpringObject(
                ((XAnnotatedListSpring) xam).componentType, val, ((XAnnotatedListSpring) xam)
                    .getXaso().getApplicationContext());
            if (object != null)
                result.add(object);
        }
    }
}

class AttributeValueVisitor extends DOMHelper.NodeVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, Collection<Object> result) {
        String val = node.getNodeValue();
        if (val != null && val.length() > 0) {
            if (xam.trim)
                val = val.trim();
            Object object = XMapSpringUtil.getSpringObject(
                ((XAnnotatedListSpring) xam).componentType, val, ((XAnnotatedListSpring) xam)
                    .getXaso().getApplicationContext());
            if (object != null)
                result.add(object);
        }
    }
}
