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

import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.Context;
import com.alipay.sofa.common.xmap.DOMHelper;
import com.alipay.sofa.common.xmap.Path;
import com.alipay.sofa.common.xmap.XAnnotatedMap;
import com.alipay.sofa.common.xmap.XAnnotatedMember;
import com.alipay.sofa.common.xmap.XGetter;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.common.xmap.XSetter;
import com.alipay.sofa.common.xmap.spring.XNodeMapSpring;

/**
 *
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public class XAnnotatedMapSpring extends XAnnotatedMap {

    /**
     * dom visitor
     */
    protected static final ElementValueMapVisitor   elementVisitor   = new ElementValueMapVisitor();
    protected static final AttributeValueMapVisitor attributeVisitor = new AttributeValueMapVisitor();

    private XAnnotatedSpringObject                  xaso;

    public XAnnotatedMapSpring(XMap xmap, XSetter setter, XGetter getter, XNodeMapSpring anno,
                               XAnnotatedSpringObject xaso) {
        super(xmap, setter, getter, null);
        this.setXaso(xaso);
        path = new Path(anno.value());
        trim = anno.trim();
        key = new Path(anno.key());
        type = anno.type();
        componentType = anno.componentType();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Context ctx, Element base) throws IllegalAccessException,
                                                        InstantiationException {
        Map<String, Object> values = (Map) type.newInstance();
        if (path.attribute != null) {
            // attribute list
            DOMHelper.visitMapNodes(ctx, this, base, path, attributeVisitor, values);
        } else {
            // element list
            DOMHelper.visitMapNodes(ctx, this, base, path, elementVisitor, values);
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

class ElementValueMapVisitor extends DOMHelper.NodeMapVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                          Map<String, Object> result) {
        String val = node.getTextContent();
        if (val != null && val.length() > 0) {
            if (xam.trim)
                val = val.trim();
            Object object = XMapSpringUtil.getSpringObject(
                ((XAnnotatedMapSpring) xam).componentType, val, ((XAnnotatedMapSpring) xam)
                    .getXaso().getApplicationContext());
            if (object != null)
                result.put(key, object);
        }
    }
}

class AttributeValueMapVisitor extends DOMHelper.NodeMapVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                          Map<String, Object> result) {
        String val = node.getNodeValue();
        if (val != null && val.length() > 0) {
            if (xam.trim)
                val = val.trim();
            Object object = XMapSpringUtil.getSpringObject(
                ((XAnnotatedMapSpring) xam).componentType, val, ((XAnnotatedMapSpring) xam)
                    .getXaso().getApplicationContext());
            if (object != null)
                result.put(key, object);
        }
    }
}
