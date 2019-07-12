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
package com.alipay.sofa.common.xmap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.annotation.XNodeMap;
import com.alipay.sofa.runtime.log.SofaLogger;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XAnnotatedMap extends XAnnotatedList {

    protected static final ElementMapVisitor        elementMapVisitor = new ElementMapVisitor();
    protected static final ElementValueMapVisitor   elementVisitor    = new ElementValueMapVisitor();
    protected static final AttributeValueMapVisitor attributeVisitor  = new AttributeValueMapVisitor();

    protected Path                                  key;

    public XAnnotatedMap(XMap xmap, XSetter setter, XGetter getter, XNodeMap anno) {
        super(xmap, setter, getter);
        if (anno != null) {
            path = new Path(anno.value());
            trim = anno.trim();
            key = new Path(anno.key());
            type = anno.type();
            cdata = anno.cdata();
            componentType = anno.componentType();
            valueFactory = xmap.getValueFactory(componentType);
            xao = xmap.register(componentType);
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Context ctx, Element base) throws IllegalAccessException,
                                                        InstantiationException {
        Map<String, Object> values = (Map) type.newInstance();
        if (xao != null) {
            DOMHelper.visitMapNodes(ctx, this, base, path, elementMapVisitor, values);
        } else {
            if (path.attribute != null) {
                // attribute list
                DOMHelper.visitMapNodes(ctx, this, base, path, attributeVisitor, values);
            } else {
                // element list
                DOMHelper.visitMapNodes(ctx, this, base, path, elementVisitor, values);
            }
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void decode(Object instance, Node base, Document document, List<String> filters)
                                                                                           throws Exception {
        if (!isFilter(filters)) {
            return;
        }

        Map values = (Map) getter.getValue(instance);

        Node node = base;
        int len = path.segments.length - 1;
        for (int i = 0; i < len; i++) {
            Node n = DOMHelper.getElementNode(node, path.segments[i]);
            if (n == null) {
                Element element = document.createElement(path.segments[i]);
                node = node.appendChild(element);
            } else {
                node = n;
            }
        }

        String name = path.segments[len];

        Node lastParentNode = node;

        Set<Map.Entry> entrys = values.entrySet();
        for (Map.Entry entry : entrys) {

            Element element = document.createElement(name);
            node = lastParentNode.appendChild(element);

            Object keyObj = entry.getKey();
            String keyValue = keyObj == null ? "" : keyObj.toString();

            Object object = entry.getValue();

            Attr attrKey = document.createAttribute(key.attribute);
            attrKey.setNodeValue(keyValue);
            ((Element) node).setAttributeNode(attrKey);

            if (xao != null) {
                xao.decode(object, node, document, filters);
            } else {
                String value = object == null ? "" : object.toString();

                if (path.attribute != null && path.attribute.length() > 0) {
                    Attr attrValue = document.createAttribute(path.attribute);
                    attrValue.setNodeValue(value);

                    ((Element) node).setAttributeNode(attrValue);
                } else {
                    if (cdata) {
                        CDATASection cdataSection = document.createCDATASection(value);
                        node.appendChild(cdataSection);
                    } else {
                        node.setTextContent(value);
                    }
                }
            }
        }
    }

}

/**
 * <code>ElementMap</code><code>NodeVisitor</code>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class ElementMapVisitor extends DOMHelper.NodeMapVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                          Map<String, Object> result) {
        try {
            result.put(key, xam.xao.newInstance(ctx, (Element) node));
        } catch (Exception e) {
            SofaLogger.error("visitNode error", e);
        }
    }
}

/**
 * <code>ElementValueMap</code><code>NodeVisitor</code>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class ElementValueMapVisitor extends DOMHelper.NodeMapVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                          Map<String, Object> result) {
        String val = node.getTextContent();
        if (xam.trim) {
            val = val.trim();
        }
        if (xam.valueFactory != null) {
            result.put(key, xam.valueFactory.getValue(ctx, val));
        } else {
            // TODO: log warning?
            result.put(key, val);
        }
    }
}

/**
 * <code>AttributeValueMap</code><code>NodeVisitor</code>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class AttributeValueMapVisitor extends DOMHelper.NodeMapVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                          Map<String, Object> result) {
        String val = node.getNodeValue();
        if (xam.valueFactory != null) {
            result.put(key, xam.valueFactory.getValue(ctx, val));
        } else {
            // TODO: log warning?
            result.put(key, val);
        }
    }
}
