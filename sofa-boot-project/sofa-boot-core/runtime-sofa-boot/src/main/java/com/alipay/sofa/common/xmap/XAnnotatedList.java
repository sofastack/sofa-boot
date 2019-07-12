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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.annotation.XNodeList;
import com.alipay.sofa.runtime.log.SofaLogger;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XAnnotatedList extends XAnnotatedMember {

    protected static final ElementVisitor        elementListVisitor = new ElementVisitor();
    protected static final ElementValueVisitor   elementVisitor     = new ElementValueVisitor();
    protected static final AttributeValueVisitor attributeVisitor   = new AttributeValueVisitor();

    // indicates the type of the collection components
    public Class<?>                              componentType;

    protected XAnnotatedList(XMap xmap, XSetter setter, XGetter getter) {
        super(xmap, setter, getter);
    }

    public XAnnotatedList(XMap xmap, XSetter setter, XGetter getter, XNodeList anno) {
        super(xmap, setter, getter);
        path = new Path(anno.value());
        trim = anno.trim();
        type = anno.type();
        cdata = anno.cdata();
        componentType = anno.componentType();
        valueFactory = xmap.getValueFactory(componentType);
        xao = xmap.register(componentType);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object getValue(Context ctx, Element base) throws Exception {
        ArrayList<Object> values = new ArrayList<Object>();
        if (xao != null) {
            DOMHelper.visitNodes(ctx, this, base, path, elementListVisitor, values);
        } else {
            if (path.attribute != null) {
                // attribute list
                DOMHelper.visitNodes(ctx, this, base, path, attributeVisitor, values);
            } else {
                // element list
                DOMHelper.visitNodes(ctx, this, base, path, elementVisitor, values);
            }
        }

        if (type != ArrayList.class) {
            if (type.isArray()) {
                if (componentType.isPrimitive()) {
                    // primitive arrays cannot be casted to Object[]
                    return PrimitiveArrays.toPrimitiveArray(values, componentType);
                } else {
                    return values
                        .toArray((Object[]) Array.newInstance(componentType, values.size()));
                }
            } else {
                Collection col = (Collection) type.newInstance();
                col.addAll(values);
                return col;
            }
        }

        return values;
    }

    @SuppressWarnings("unchecked")
    public void decode(Object instance, Node base, Document document, List<String> filters)
                                                                                           throws Exception {
        if (!isFilter(filters)) {
            return;
        }

        Collection col = null;
        if (Collection.class.isAssignableFrom(type)) {
            col = (Collection) getter.getValue(instance);
        } else {
            if (type.isArray()) {
                col = new ArrayList();

                Object obj = getter.getValue(instance);

                int length = Array.getLength(obj);

                for (int i = 0; i < length; i++) {
                    col.add(Array.get(obj, i));
                }
            } else {
                throw new Exception("@XNodeList " + base.getNodeName()
                                    + " 'type' only support Collection ande Array type");
            }
        }

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

        for (Object object : col) {

            Element element = document.createElement(name);
            node = lastParentNode.appendChild(element);

            if (xao != null) {
                xao.decode(object, node, document, filters);
            } else {
                String value = object == null ? "" : object.toString();

                if (path.attribute != null && path.attribute.length() > 0) {
                    Attr attr = document.createAttribute(path.attribute);
                    attr.setNodeValue(value);

                    ((Element) node).setAttributeNode(attr);
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
 * <code>Element</code><code>NodeVisitor</code>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class ElementVisitor extends DOMHelper.NodeVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, Collection<Object> result) {
        try {
            result.add(xam.xao.newInstance(ctx, (Element) node));
        } catch (Throwable e) {
            SofaLogger.error("visitNode error", e);
        }
    }
}

/**
 * <code>ElementValue</code><code>NodeVisitor</code>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class ElementValueVisitor extends DOMHelper.NodeVisitor {

    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, Collection<Object> result) {
        String val = node.getTextContent();
        if (xam.trim) {
            val = val.trim();
        }
        if (xam.valueFactory != null) {
            result.add(xam.valueFactory.getValue(ctx, val));
        } else {
            // TODO: log warning?
            result.add(val);
        }
    }
}

/**
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
class AttributeValueVisitor extends DOMHelper.NodeVisitor {
    /**
     *
     * @param ctx
     * @param xam
     * @param node
     * @param result
     */
    @Override
    public void visitNode(Context ctx, XAnnotatedMember xam, Node node, Collection<Object> result) {
        String val = node.getNodeValue();
        if (xam.valueFactory != null) {
            result.add(xam.valueFactory.getValue(ctx, val));
        } else {
            // TODO: log warning?
            result.add(val);
        }
    }
}
