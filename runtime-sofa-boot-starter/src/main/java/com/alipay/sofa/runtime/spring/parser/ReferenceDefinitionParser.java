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
package com.alipay.sofa.runtime.spring.parser;

import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author xuanbei 18/3/1
 */
public class ReferenceDefinitionParser extends AbstractContractDefinitionParser {

    private static final String LOCAL_FIRST           = "local-first";
    private static final String JVM_SERVICE           = "jvm-service";
    private static final String PROPERTY_LOCAL_FIRST  = "localFirst";
    private static final String PROPERTY_JVM_SERVICE  = "jvmService";
    private static final String PROPERTY_LOAD_BALANCE = "loadBalance";

    @Override
    protected void doParseInternal(Element element, ParserContext parserContext,
                                   BeanDefinitionBuilder builder) {
        String localFirstString = element.getAttribute(LOCAL_FIRST);

        if (localFirstString != null && localFirstString.length() > 0) {
            if ("true".equalsIgnoreCase(localFirstString)) {
                builder.addPropertyValue(PROPERTY_LOCAL_FIRST, true);
            } else if ("false".equalsIgnoreCase(localFirstString)) {
                builder.addPropertyValue(PROPERTY_LOCAL_FIRST, false);
            } else {
                throw new RuntimeException(
                    "Invalid value of property local-first, can only be true or false.");
            }
        }

        String jvmServiceString = element.getAttribute(JVM_SERVICE);
        String id = element.getAttribute("id");

        if (jvmServiceString != null && jvmServiceString.length() > 0) {
            if ("true".equalsIgnoreCase(jvmServiceString)) {
                if (id != null && id.length() > 0) {
                    builder.addPropertyValue(PROPERTY_JVM_SERVICE, true);
                }
            } else if ("false".equalsIgnoreCase(jvmServiceString)) {
                builder.addPropertyValue(PROPERTY_JVM_SERVICE, false);
            } else {
                throw new RuntimeException(
                    "Invalid value of property jvm-service, can only be true or false");
            }
        }

        String loadBalance = element.getAttribute(PROPERTY_LOAD_BALANCE);
        if (loadBalance != null && loadBalance.length() > 0) {
            builder.addPropertyValue(PROPERTY_LOAD_BALANCE, loadBalance);
        }
    }

    @Override
    protected Class getBeanClass(Element element) {
        return ReferenceFactoryBean.class;
    }

    @Override
    public String supportTagName() {
        return "reference";
    }
}