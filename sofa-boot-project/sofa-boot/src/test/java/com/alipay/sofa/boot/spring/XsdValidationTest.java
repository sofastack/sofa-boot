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
package com.alipay.sofa.boot.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

/**
 * Test xml validation with rpc.xsd.
 *
 * @author <a href="mailto:scienjus@gmail.com">ScienJus</a>
 */
public class XsdValidationTest {

    @Test
    public void testSofaParameter() {
        Exception exception = catchException(() -> loadXml("config/spring/test_sofa_parameter.xml"));
        assertThat(exception).isNull();
    }

    @Test
    public void testSofaParameterMissingKey() {
        Exception exception = catchException(() -> loadXml("config/spring/test_sofa_parameter_missing_key.xml"));
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SAXParseException.class);
        // org.xml.sax.SAXParseException; lineNumber: 10; columnNumber: 52;
        // cvc-complex-type.4: Attribute 'key' must appear on element 'sofa:parameter'.
        assertSaxException(10, 52, "cvc-complex-type.4", (SAXParseException) exception);
    }

    @Test
    public void testSofaParameterOutsideBinding() {
        Exception exception = catchException(() -> loadXml("config/spring/test_sofa_parameter_outside_binding.xml"));
        assertThat(exception).isNotNull();
        assertThat(exception).isInstanceOf(SAXParseException.class);
        // org.xml.sax.SAXParseException; lineNumber: 11; columnNumber: 65;
        // cvc-complex-type.2.4.a: Invalid content was found starting with element 'sofa:parameter'.
        // One of '{"http://sofastack.io/schema/sofaboot":binding.jvm,
        // "http://sofastack.io/schema/sofaboot":binding.rest,
        // "http://sofastack.io/schema/sofaboot":binding.dubbo,
        // "http://sofastack.io/schema/sofaboot":binding.h2c}' is expected.
        assertSaxException(11, 65, "cvc-complex-type.2.4.a", (SAXParseException) exception);
    }

    private void assertSaxException(int expectedLineNumber, int expectedColumnNumber,
                                    String expectedErrorCode, SAXParseException e) {
        assertThat(expectedLineNumber).isEqualTo(e.getLineNumber());
        assertThat(expectedColumnNumber).isEqualTo(e.getColumnNumber());
        assertThat(expectedErrorCode).isEqualTo(e.getMessage().split(":")[0]);
    }

    private void loadXml(String xml) throws Exception {
        DocumentLoader documentLoader = new DefaultDocumentLoader();
        documentLoader.loadDocument(new InputSource(this.getClass().getClassLoader()
            .getResourceAsStream(xml)), new DelegatingEntityResolver(this.getClass()
            .getClassLoader()), new ErrorHandler() {

            @Override
            public void warning(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void error(SAXParseException exception) throws SAXException {
                throw exception;
            }

            @Override
            public void fatalError(SAXParseException exception) throws SAXException {
                throw exception;

            }
        }, XmlValidationModeDetector.VALIDATION_XSD, true);
    }
}
