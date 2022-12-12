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

import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @author ruoshan
 * @since 2.6.0
 */
public final class DOMSerializer {

    private static final DocumentBuilderFactory BUILDER_FACTORY = DocumentBuilderFactory
                                                                    .newInstance();

    //
    //    // Default output format which is : no xml declaration, no document type, indent.
    //    private static final OutputFormat           DEFAULT_FORMAT  = new OutputFormat();
    //
    //    static {
    //        DEFAULT_FORMAT.setOmitXMLDeclaration(false);
    //        DEFAULT_FORMAT.setIndenting(true);
    //        DEFAULT_FORMAT.setMethod("xml");
    //        DEFAULT_FORMAT.setEncoding("UTF-8");
    //    }

    private DOMSerializer() {
    }

    public static DocumentBuilderFactory getBuilderFactory() {
        return BUILDER_FACTORY;
    }

    public static String toString(Element element) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(element, baos);
        return baos.toString();
    }

    public static String toString(DocumentFragment fragment) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(fragment, baos);
        return baos.toString();
    }

    public static String toString(Document doc) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        write(doc, baos);
        return baos.toString();
    }

    public static void write(Element element, OutputStream out) throws IOException {
        //        LSSerializer serializer = new LSSerializerImpl();
        //        serializer.writeToString().serialize(element);
    }

    public static void write(DocumentFragment fragment, OutputStream out) throws IOException {
        //        XMLSerializer serializer = new XMLSerializer(out, format);
        //        serializer.asDOMSerializer().serialize(fragment);
    }

    public static void write(Document doc, OutputStream out) throws IOException {
        //        XMLSerializer serializer = new XMLSerializer(out, format);
        //        serializer.asDOMSerializer().serialize(doc);
    }

}
