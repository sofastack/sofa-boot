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
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * @author xuanbei 18/3/1
 */
public class ReferenceDefinitionParser extends AbstractContractDefinitionParser {
    private static final String JVM_FIRST             = "jvm-first";
    private static final String PROPERTY_JVM_FIRST    = "jvmFirst";
    private static final String PROPERTY_LOAD_BALANCE = "loadBalance";

    @Override
    protected void doParseInternal(Element element, ParserContext parserContext,
                                   BeanDefinitionBuilder builder) {
        String jvmFirstString = element.getAttribute(JVM_FIRST);

        if (StringUtils.hasText(jvmFirstString)) {
            if ("true".equalsIgnoreCase(jvmFirstString)) {
                builder.addPropertyValue(PROPERTY_JVM_FIRST, true);
            } else if ("false".equalsIgnoreCase(jvmFirstString)) {
                builder.addPropertyValue(PROPERTY_JVM_FIRST, false);
            } else {
                throw new RuntimeException(
                    "Invalid value of property jvm-first, can only be true or false.");
            }
        }

        String loadBalance = element.getAttribute(PROPERTY_LOAD_BALANCE);
        if (StringUtils.hasText(loadBalance)) {
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