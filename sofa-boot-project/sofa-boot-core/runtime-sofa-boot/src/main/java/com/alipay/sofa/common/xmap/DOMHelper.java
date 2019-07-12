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

import java.util.Collection;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public final class DOMHelper {

    private DOMHelper() {
    }

    /**
     * Gets the value of the node at the given path
     * relative to the given base element.
     * For element nodes the value is the text content and for
     * the attributes node the attribute value.
     *
     * @param base base element
     * @param path path
     * @return the node value or null if no such node was found
     */
    public static String getNodeValue(Element base, Path path) {
        Node node = getElementNode(base, path);
        if (node != null) {
            if (path.attribute != null) {
                Node at = node.getAttributes().getNamedItem(path.attribute);
                return at != null ? at.getNodeValue() : null;
            } else {
                return node.getTextContent();
            }
        }
        return null;
    }

    public static void visitNodes(Context ctx, XAnnotatedList xam, Element base, Path path,
                                  NodeVisitor visitor, Collection<Object> result) {
        Node el = base;
        int len = path.segments.length - 1;
        for (int i = 0; i < len; i++) {
            el = getElementNode(el, path.segments[i]);
            if (el == null) {
                return;
            }
        }
        String name = path.segments[len];

        if (path.attribute != null) {
            visitAttributes(ctx, xam, el, name, path.attribute, visitor, result);
        } else {
            visitElements(ctx, xam, el, name, visitor, result);
        }
    }

    public static void visitAttributes(Context ctx, XAnnotatedList xam, Node base, String name,
                                       String attrName, NodeVisitor visitor,
                                       Collection<Object> result) {
        Node p = base.getFirstChild();
        while (p != null) {
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(p.getNodeName())) {
                    Node at = p.getAttributes().getNamedItem(attrName);
                    if (at != null) {
                        visitor.visitNode(ctx, xam, at, result);
                    }
                }
            }
            p = p.getNextSibling();
        }
    }

    public static void visitElements(Context ctx, XAnnotatedList xam, Node base, String name,
                                     NodeVisitor visitor, Collection<Object> result) {
        Node p = base.getFirstChild();
        while (p != null) {
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(p.getNodeName())) {
                    visitor.visitNode(ctx, xam, p, result);
                }
            }
            p = p.getNextSibling();
        }
    }

    public static void visitMapNodes(Context ctx, XAnnotatedMap xam, Element base, Path path,
                                     NodeMapVisitor visitor, Map<String, Object> result) {
        Node el = base;
        int len = path.segments.length - 1;
        for (int i = 0; i < len; i++) {
            el = getElementNode(el, path.segments[i]);
            if (el == null) {
                return;
            }
        }
        String name = path.segments[len];

        if (path.attribute != null) {
            visitMapAttributes(ctx, xam, el, name, path.attribute, visitor, result);
        } else {
            visitMapElements(ctx, xam, el, name, visitor, result);
        }
    }

    public static void visitMapAttributes(Context ctx, XAnnotatedMap xam, Node base, String name,
                                          String attrName, NodeMapVisitor visitor,
                                          Map<String, Object> result) {
        Node p = base.getFirstChild();
        while (p != null) {
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(p.getNodeName())) {
                    Node at = p.getAttributes().getNamedItem(attrName);
                    if (at != null) {
                        String key = getNodeValue((Element) p, xam.key);
                        if (key != null) {
                            visitor.visitNode(ctx, xam, at, key, result);
                        }
                    }
                }
            }
            p = p.getNextSibling();
        }
    }

    public static void visitMapElements(Context ctx, XAnnotatedMap xam, Node base, String name,
                                        NodeMapVisitor visitor, Map<String, Object> result) {
        Node p = base.getFirstChild();
        while (p != null) {
            if (p.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(p.getNodeName())) {
                    String key = getNodeValue((Element) p, xam.key);
                    if (key != null) {
                        visitor.visitNode(ctx, xam, p, key, result);
                    }
                }
            }
            p = p.getNextSibling();
        }
    }

    public static Node getElementNode(Node base, String name) {
        Node node = base.getFirstChild();
        while (node != null) {
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (name.equals(node.getNodeName())) {
                    return node;
                }
            }
            node = node.getNextSibling();
        }
        return null;
    }

    public static Node getElementNode(Node base, Path path) {
        Node el = base;
        int len = path.segments.length;
        for (int i = 0; i < len; i++) {
            el = getElementNode(el, path.segments[i]);
            if (el == null) {
                return null;
            }
        }
        return el;
    }

    public abstract static class NodeVisitor {

        public abstract void visitNode(Context ctx, XAnnotatedMember xam, Node node,
                                       Collection<Object> result);

    }

    public abstract static class NodeMapVisitor {

        public abstract void visitNode(Context ctx, XAnnotatedMember xam, Node node, String key,
                                       Map<String, Object> result);

    }

}
