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

import java.beans.Introspector;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.alipay.sofa.common.xmap.annotation.XContent;
import com.alipay.sofa.common.xmap.annotation.XMemberAnnotation;
import com.alipay.sofa.common.xmap.annotation.XNode;
import com.alipay.sofa.common.xmap.annotation.XNodeList;
import com.alipay.sofa.common.xmap.annotation.XNodeMap;
import com.alipay.sofa.common.xmap.annotation.XObject;
import com.alipay.sofa.common.xmap.annotation.XParent;

/**
 * XMap maps an XML file to a java object.
 * The mapping is described by annotations on java objects.
 * The following annotations are supported:
 * <ul>
 * <li> {@link XObject}
 * Mark the object as being mappable to an XML node
 * <li> {@link XNode}
 * Map an XML node to a field of a mappable object
 * <li> {@link XNodeList}
 * Map an list of XML nodes to a field of a mappable object
 * <li> {@link XNodeMap}
 * Map an map of XML nodes to a field of a mappable object
 * <li> {@link XContent}
 * Map an XML node content to a field of a mappable object
 * <li> {@link XParent}
 * Map a field of the current mappable object to the parent object if any exists
 * The parent object is the mappable object containing the current object as a field
 * </ul>
 * The mapping is done in 2 steps:
 * <ul>
 * <li> The XML file is loaded as a DOM document
 * <li> The DOM document is parsed and the nodes mapping is resolved
 * </ul>
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @author ruoshan
 */

public class XMap {

    // top level objects
    protected final Map<String, XAnnotatedObject> roots;

    // the scanned objects
    protected final Map<Class, XAnnotatedObject>  objects;

    protected final Map<Class, XValueFactory>     factories;

    private static final String                   DDD = "http://apache.org/xml/features/disallow-doctype-decl";

    /**
     * Creates a new XMap object.
     */
    public XMap() {
        objects = new Hashtable<>();
        roots = new Hashtable<>();
        factories = new Hashtable<>(XValueFactory.defaultFactories);
    }

    /**
     * Gets the value factory used for objects of the given class.
     * Value factories are used to decode values from XML strings.
     *
     * @param type the object type
     * @return the value factory if any, null otherwise
     */
    public XValueFactory getValueFactory(Class type) {
        return factories.get(type);
    }

    /**
     * Sets a custom value factory for the given class.
     * Value factories are used to decode values from XML strings.
     *
     * @param type    the object type
     * @param factory the value factory to use for the given type
     */
    public void setValueFactory(Class type, XValueFactory factory) {
        factories.put(type, factory);
    }

    /**
     * Gets a list of scanned objects.
     * Scanned objects are annotated objects that was registered
     * by this XMap instance.
     *
     * @return list of scanned objects.
     */
    public Collection<XAnnotatedObject> getScannedObjects() {
        return objects.values();
    }

    /**
     * Gets the root objects.
     * Root objects are scanned objects that can be mapped to XML elements
     * that are not part from other objects.
     *
     * @return the root objects
     */
    public Collection<XAnnotatedObject> getRootObjects() {
        return roots.values();
    }

    /**
     * Registers a mappable object class.
     * The class will be scanned for XMap annotations
     * and a mapping description is created.
     *
     * @param klass the object class
     * @return the mapping description
     */
    @SuppressWarnings("unchecked")
    public XAnnotatedObject register(Class klass) {
        XAnnotatedObject xao = objects.get(klass);
        if (xao == null) { // avoid scanning twice
            XObject xob = checkObjectAnnotation(klass, klass.getClassLoader());
            if (xob != null) {
                xao = new XAnnotatedObject(this, klass, xob);
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
     * Registers a mappable object class.
     * The class will be scanned for XMap annotations
     * and a mapping description is created.
     *
     * @param klass the object class
     * @return the mapping description
     */
    @SuppressWarnings("unchecked")
    public XAnnotatedObject register(Class klass, boolean isParent) {
        XAnnotatedObject xao = objects.get(klass);
        if (xao == null) { // avoid scanning twice
            XObject xob = checkObjectAnnotation(klass, klass.getClassLoader());
            if (xob != null) {
                xao = new XAnnotatedObject(this, klass, xob);
                objects.put(xao.klass, xao);
                if (isParent) {
                    scanParent(xao);
                } else {
                    scan(xao);
                }

                String key = xob.value();
                if (key.length() > 0) {
                    roots.put(xao.path.path, xao);
                }
            }
        }
        return xao;
    }

    private void scan(XAnnotatedObject xob) {
        Field[] fields = xob.klass.getDeclaredFields();
        for (Field field : fields) {
            Annotation anno = checkMemberAnnotation(field);
            if (anno != null) {
                XAnnotatedMember member = createFieldMember(field, anno);
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
                xob.addMember(member);
            }
        }
    }

    private void scanParent(XAnnotatedObject xob) {
        Class scanClass = xob.klass;

        for (; scanClass != Object.class; scanClass = scanClass.getSuperclass()) {
            for (Method method : scanClass.getDeclaredMethods()) {
                // we accept only methods with one parameter
                Class[] paramTypes = method.getParameterTypes();
                if (paramTypes.length != 1) {
                    continue;
                }
                Annotation anno = checkMemberAnnotation(method);
                if (anno != null) {
                    XAnnotatedMember member = createMethodMember(method, xob.klass, anno);
                    xob.addMember(member);
                }
            }

            for (Field field : scanClass.getDeclaredFields()) {
                Annotation anno = checkMemberAnnotation(field);
                if (anno != null) {
                    XAnnotatedMember member = createFieldMember(field, anno);
                    xob.addMember(member);
                }
            }
        }
    }

    /**
     * Processes the XML file at the given URL using a default context.
     * Returns the first registered top level object that is found in the file.
     * If not objects are found null is returned.
     *
     * @param url the XML file url
     * @return the first register top level object
     * @throws Exception
     */
    public Object load(URL url) throws Exception {
        return load(new Context(), url.openStream());
    }

    /**
     * Processes the XML file at the given URL and using the given contexts.
     * Returns the first registered top level object that is found in the file.
     *
     * @param ctx the context to use
     * @param url the XML file url
     * @return the first register top level object
     * @throws Exception any exception
     */
    public Object load(Context ctx, URL url) throws Exception {
        return load(ctx, url.openStream());
    }

    /**
     * Processes the XML content from the given input stream using a default context.
     * Returns the first registered top level object that is found in the file.
     *
     * @param in the XML input source
     * @return the first register top level object
     * @throws Exception
     */
    public Object load(InputStream in) throws Exception {
        return load(new Context(), in);
    }

    /**
     * Processes the XML content from the given input stream using the given context.
     * Return the first registered top level object that is found in the file.
     *
     * @param ctx the context to use
     * @param in  the input stream
     * @return the first register top level object
     * @throws Exception any exception
     */
    public Object load(Context ctx, InputStream in) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(DDD, true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            return load(ctx, document.getDocumentElement());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Processes the XML file at the given URL using a default context.
     * Returns a list with all registered top level objects that are found in the file.
     * If not objects are found, an empty list is returned.
     *
     * @param url the XML file url
     * @return a list with all registered top level objects that are found in the file
     * @throws Exception
     */
    public Object[] loadAll(URL url) throws Exception {
        return loadAll(new Context(), url.openStream());
    }

    /**
     * Processes the XML file at the given URL using the given context
     * Return a list with all registered top level objects that are found in the file.
     * If not objects are found an empty list is returned.
     *
     * @param ctx the context to use
     * @param url the XML file url
     * @return a list with all registered top level objects that are found in the file
     * @throws Exception any exception
     */
    public Object[] loadAll(Context ctx, URL url) throws Exception {
        return loadAll(ctx, url.openStream());
    }

    /**
     * Processes the XML from the given input stream using the given context.
     * Returns a list with all registered top level objects that are found in the file.
     * If not objects are found, an empty list is returned.
     *
     * @param in the XML input stream
     * @return a list with all registered top level objects that are found in the file
     * @throws Exception any exception
     */
    public Object[] loadAll(InputStream in) throws Exception {
        return loadAll(new Context(), in);
    }

    /**
     * Processes the XML from the given input stream using the given context.
     * Returns a list with all registered top level objects that are found in the file.
     * If not objects are found, an empty list is returned.
     *
     * @param ctx the context to use
     * @param in  the XML input stream
     * @return a list with all registered top level objects that are found in the file
     * @throws Exception
     */
    public Object[] loadAll(Context ctx, InputStream in) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(DDD, true);
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(in);
            return loadAll(ctx, document.getDocumentElement());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Processes the given DOM element and return the first mappable object
     * found in the element.
     * A default context is used.
     *
     * @param root the element to process
     * @return the first object found in this element or null if none
     * @throws Exception
     */
    public Object load(Element root) throws Exception {
        return load(new Context(), root);
    }

    /**
     * Processes the given DOM element and return the first mappable object
     * found in the element.
     * The given context is used.
     *
     * @param ctx  the context to use
     * @param root the element to process
     * @return the first object found in this element or null if none
     * @throws Exception
     */
    public Object load(Context ctx, Element root) throws Exception {
        // check if the current element is bound to an annotated object
        String name = root.getNodeName();
        XAnnotatedObject xob = roots.get(name);
        if (xob != null) {
            return xob.newInstance(new Context(), root);
        } else {
            Node p = root.getFirstChild();
            while (p != null) {
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    // Recurse in the first child Element
                    return load((Element) p);
                }
                p = p.getNextSibling();
            }
            // We didn't find any Element
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T load(Map<String, Object> map, String keyPrefix, Class<T> klass) throws Exception {
        XAnnotatedObject xob = objects.get(klass);
        if (xob == null) {
            xob = this.register(klass);
        }

        return (T) xob.newInstance(new Context(), map, keyPrefix);
    }

    /**
     * Processes the given DOM element and return a list with all top-level
     * mappable objects found in the element.
     * The given context is used.
     *
     * @param ctx  the context to use
     * @param root the element to process
     * @return the list of all top level objects found
     * @throws Exception
     */
    public Object[] loadAll(Context ctx, Element root) throws Exception {
        List<Object> result = new ArrayList<Object>();
        loadAll(ctx, root, result);
        return result.toArray();
    }

    /**
     * Processes the given DOM element and return a list with all top-level
     * mappable objects found in the element.
     * The default context is used.
     *
     * @param root the element to process
     * @return the list of all top level objects found
     * @throws Exception
     */
    public Object[] loadAll(Element root) throws Exception {
        return loadAll(new Context(), root);
    }

    /**
     * Same as {@link com.alipay.sofa.common.xmap.XMap#loadAll(Element)} but put collected objects in the
     * given collection.
     *
     * @param root   the element to process
     * @param result the collection where to collect objects
     * @throws Exception
     */
    public void loadAll(Element root, Collection<Object> result) throws Exception {
        loadAll(new Context(), root, result);
    }

    public Document asXml(Object contribution, List<String> filters) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setFeature(DDD, true);
        factory.setNamespaceAware(false);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        if (contribution == null) {
            return document;
        }

        XAnnotatedObject xao = objects.get(contribution.getClass());

        if (xao != null) {
            xao.decode(contribution, document, document, filters);
        } // else TODO

        return document;
    }

    public String asXmlString(Object contribution, String encoding, List<String> filters)
                                                                                         throws Exception {
        return asXmlString(contribution, encoding, filters, false);

    }

    public String asXmlString(Object contribution, String encoding, List<String> filters,
                              boolean pretty) throws Exception {
        Document doc = asXml(contribution, filters);

        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(doc);
        if (encoding != null && encoding.length() > 0) {
            transformer.setOutputProperty(OutputKeys.ENCODING, encoding);
        }
        if (pretty) {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        } else {
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
        }

        StringWriter sw = new StringWriter();

        StreamResult result = new StreamResult(sw);
        transformer.transform(source, result);

        return sw.toString();

    }

    /**
     * Same as {@link com.alipay.sofa.common.xmap.XMap#loadAll(Context, Element)} but put collected objects in the
     * given collection.
     *
     * @param ctx    the context to use
     * @param root   the element to process
     * @param result the collection where to collect objects
     * @throws Exception
     */
    public void loadAll(Context ctx, Element root, Collection<Object> result) throws Exception {
        // check if the current element is bound to an annotated object
        String name = root.getNodeName();
        XAnnotatedObject xob = roots.get(name);
        if (xob != null) {
            Object ob = xob.newInstance(ctx, root);
            result.add(ob);
        } else {
            Node p = root.getFirstChild();
            while (p != null) {
                if (p.getNodeType() == Node.ELEMENT_NODE) {
                    loadAll(ctx, (Element) p, result);
                }
                p = p.getNextSibling();
            }
        }
    }

    protected static Annotation checkMemberAnnotation(AnnotatedElement ae) {
        Annotation[] annos = ae.getAnnotations();
        for (Annotation anno : annos) {
            if (anno.annotationType().isAnnotationPresent(XMemberAnnotation.class)) {
                return anno;
            }
        }
        return null;
    }

    protected static XObject checkObjectAnnotation(AnnotatedElement ae, ClassLoader classLoader) {
        return ae.getAnnotation(XObject.class);
    }

    private XAnnotatedMember createMember(Annotation annotation, XSetter setter, XGetter getter) {
        XAnnotatedMember member = null;
        int type = annotation.annotationType().getAnnotation(XMemberAnnotation.class).value();
        if (type == XMemberAnnotation.NODE) {
            member = new XAnnotatedMember(this, setter, getter, (XNode) annotation);
        } else if (type == XMemberAnnotation.NODE_LIST) {
            member = new XAnnotatedList(this, setter, getter, (XNodeList) annotation);
        } else if (type == XMemberAnnotation.NODE_MAP) {
            member = new XAnnotatedMap(this, setter, getter, (XNodeMap) annotation);
        } else if (type == XMemberAnnotation.PARENT) {
            member = new XAnnotatedParent(this, setter, getter);
        } else if (type == XMemberAnnotation.CONTENT) {
            member = new XAnnotatedContent(this, setter, getter, (XContent) annotation);
        }
        return member;
    }

    public final XAnnotatedMember createFieldMember(Field field, Annotation annotation) {
        XSetter setter = new XFieldSetter(field);
        XGetter getter = new XFieldGetter(field);
        return createMember(annotation, setter, getter);
    }

    public final XAnnotatedMember createMethodMember(Method method, Class<?> clazz,
                                                     Annotation annotation) {
        XSetter setter = new XMethodSetter(method);

        String methodName = method.getName();
        String name = Introspector.decapitalize(methodName.substring(3));
        XGetter getter = new XMethodGetter(getGetterMethod(clazz, name), clazz, name);
        return createMember(annotation, setter, getter);
    }

    public Method getGetterMethod(Class<?> clazz, String name) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.matches("^(get|is).*") && method.getParameterTypes().length == 0) {
                if (Introspector.decapitalize(methodName.substring(3)).equals(name)) {
                    return method;
                }
            }
        }

        return null;
    }

}
