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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import org.w3c.dom.Node;

import com.alipay.sofa.runtime.log.SofaLogger;

/**
 * Value factories are used to decode values from XML strings.
 * To register a new factory for a given XMap instance use the method
 * {@link com.alipay.sofa.common.xmap.XMap#setValueFactory(Class, com.alipay.sofa.common.xmap.XValueFactory)}
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public abstract class XValueFactory {

    static final Map<Class, XValueFactory> defaultFactories = new Hashtable<Class, XValueFactory>();

    public abstract Object getValue(Context ctx, String value);

    public final Object getElementValue(Context ctx, Node element, boolean trim) {
        String text = element.getTextContent();
        return getValue(ctx, trim ? text.trim() : text);
    }

    public final Object getAttributeValue(Context ctx, Node element, String name) {
        Node at = element.getAttributes().getNamedItem(name);
        return at != null ? getValue(ctx, at.getNodeValue()) : null;
    }

    public static void addFactory(Class klass, XValueFactory factory) {
        defaultFactories.put(klass, factory);
    }

    public static XValueFactory getFactory(Class type) {
        return defaultFactories.get(type);
    }

    public static Object getValue(Context ctx, Class klass, String value) {
        XValueFactory factory = defaultFactories.get(klass);
        if (factory == null) {
            return null;
        }
        return factory.getValue(ctx, value);
    }

    public static final XValueFactory STRING   = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return value;
                                                   }
                                               };

    public static final XValueFactory INTEGER  = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Integer.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory LONG     = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Long.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory DOUBLE   = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Double.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory FLOAT    = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Float.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory BOOLEAN  = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Boolean.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory DATE     = new XValueFactory() {
                                                   final DateFormat df = new SimpleDateFormat(
                                                                           "dd-MM-yyyy");

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return df.parse(value);
                                                       } catch (Exception e) {
                                                           return null;
                                                       }
                                                   }
                                               };

    public static final XValueFactory FILE     = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return new File(value);
                                                   }
                                               };

    public static final XValueFactory URL      = new XValueFactory() {
                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return new java.net.URL(value);
                                                       } catch (Exception e) {
                                                           return null;
                                                       }
                                                   }
                                               };

    public static final XValueFactory CLASS    = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return ctx.loadClass(value);
                                                       } catch (Throwable t) {
                                                           SofaLogger.error("load class error", t);
                                                           return null;
                                                       }
                                                   }
                                               };

    public static final XValueFactory RESOURCE = new XValueFactory() {

                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return new Resource(
                                                               ctx.getResource(value));
                                                       } catch (Throwable t) {
                                                           SofaLogger
                                                               .error("new resource error", t);
                                                           return null;
                                                       }
                                                   }
                                               };

    public static final XValueFactory SHORT    = new XValueFactory() {
                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       return Short.valueOf(value);
                                                   }
                                               };

    public static final XValueFactory CHAR     = new XValueFactory() {
                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return value.toCharArray()[0];
                                                       } catch (Throwable e) {
                                                           return null;
                                                       }
                                                   }
                                               };

    public static final XValueFactory BYTE     = new XValueFactory() {
                                                   @Override
                                                   public Object getValue(Context ctx, String value) {
                                                       try {
                                                           return value.getBytes()[0];
                                                       } catch (Throwable e) {
                                                           return null;
                                                       }
                                                   }
                                               };

    static {
        addFactory(String.class, STRING);
        addFactory(Integer.class, INTEGER);
        addFactory(Long.class, LONG);
        addFactory(Float.class, FLOAT);
        addFactory(Double.class, DOUBLE);
        addFactory(Date.class, DATE);
        addFactory(Boolean.class, BOOLEAN);
        addFactory(File.class, FILE);
        addFactory(java.net.URL.class, URL);
        addFactory(Short.class, SHORT);
        addFactory(Character.class, CHAR);
        addFactory(Byte.class, BYTE);

        addFactory(int.class, INTEGER);
        addFactory(long.class, LONG);
        addFactory(double.class, DOUBLE);
        addFactory(float.class, FLOAT);
        addFactory(boolean.class, BOOLEAN);
        addFactory(short.class, SHORT);
        addFactory(char.class, CHAR);
        addFactory(byte.class, BYTE);

        addFactory(Class.class, CLASS);
        addFactory(Resource.class, RESOURCE);
    }

}
