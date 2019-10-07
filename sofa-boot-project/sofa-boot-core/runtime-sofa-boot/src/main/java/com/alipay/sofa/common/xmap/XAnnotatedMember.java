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

import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.annotation.XNode;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XAnnotatedMember {

    public final XMap       xmap;
    public final XSetter    setter;
    public final XGetter    getter;
    public Path             path;
    public boolean          trim;
    public boolean          cdata;

    // the java type of the described element
    public Class            type;
    // not null if the described object is an xannotated object
    public XAnnotatedObject xao;
    // the value factory used to transform strings in objects compatible
    // with this member type
    // In the case of collection types this factory is
    // used for collection components
    public XValueFactory    valueFactory;

    protected XAnnotatedMember(XMap xmap, XSetter setter, XGetter getter) {
        this.xmap = xmap;
        this.setter = setter;
        this.getter = getter;
    }

    public XAnnotatedMember(XMap xmap, XSetter setter, XGetter getter, XNode anno) {
        this.xmap = xmap;
        this.setter = setter;
        this.getter = getter;
        this.path = new Path(anno.value());
        this.trim = anno.trim();
        this.cdata = anno.cdata();
        this.type = setter.getType();
        this.valueFactory = xmap.getValueFactory(this.type);
        this.xao = xmap.register(this.type);
    }

    protected void setValue(Object instance, Object value) throws Exception {
        setter.setValue(instance, value);
    }

    public void process(Context ctx, Element element) throws Exception {
        Object value = getValue(ctx, element);
        if (value != null) {
            setValue(ctx.getObject(), value);
        }
    }

    public void process(Context ctx, Map<String, Object> map, String keyPrefix) throws Exception {
        Object value = getValue(ctx, map, keyPrefix);
        if (value != null) {
            setValue(ctx.getObject(), value);
        }
    }

    public void decode(Object instance, Node base, Document document, List<String> filters)
                                                                                           throws Exception {
        if (!isFilter(filters)) {
            return;
        }

        Node node = base;

        int len = path.segments.length;
        for (int i = 0; i < len; i++) {
            Node n = DOMHelper.getElementNode(node, path.segments[i]);
            if (n == null) {
                Element element = document.createElement(path.segments[i]);
                node = node.appendChild(element);
            } else {
                node = n;
            }
        }

        Object object = getter.getValue(instance);

        if (object != null && Element.class.isAssignableFrom(object.getClass())) {
            return;
        }

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

    protected Object getValue(Context ctx, Element base) throws Exception {
        if (xao != null) {
            Element el = (Element) DOMHelper.getElementNode(base, path);
            return el == null ? null : xao.newInstance(ctx, el);
        }
        // scalar field
        if (type == Element.class) {
            // allow DOM elements as values
            return base;
        }
        String val = DOMHelper.getNodeValue(base, path);
        if (val != null) {
            if (trim) {
                val = val.trim();
            }
            return valueFactory.getValue(ctx, val);
        }
        return null;
    }

    protected Object getValue(Context ctx, Map<String, Object> map, String keyPrefix)
                                                                                     throws Exception {
        String key = keyPrefix == null ? path.path : keyPrefix + path.path;
        Object val = map.get(key);
        Object result = null;

        if (val == null) {
            result = null;
        } else if (val instanceof String) {
            String str = (String) val;
            if (str != null) {
                if (trim) {
                    str = str.trim();
                }
                result = valueFactory.getValue(ctx, str);
            }
        } else {
            result = val;
        }

        return result;
    }

    protected boolean isFilter(List<String> filters) {
        boolean filter = false;

        if (filters == null || filters.size() == 0) {
            filter = true;
        } else {
            filter = filters.contains(path.path);
        }

        return filter;
    }
}
