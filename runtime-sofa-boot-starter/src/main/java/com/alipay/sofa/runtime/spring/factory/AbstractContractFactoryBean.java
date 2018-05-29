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
package com.alipay.sofa.runtime.spring.factory;

import com.alipay.sofa.runtime.api.ServiceRuntimeException;
import com.alipay.sofa.runtime.constants.SofaRuntimeFrameworkConstants;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.binding.BindingAdapterFactory;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Contract Factory Bean
 *
 * @author xuanbei 18/3/1
 */
public abstract class AbstractContractFactoryBean implements InitializingBean, FactoryBean,
                                                 ApplicationContextAware {
    /** bean id */
    protected String                  beanId;
    /** unique id */
    protected String                  uniqueId;
    /** interface class name */
    protected String                  interfaceType;
    /** interface class type */
    protected Class<?>                interfaceClass;
    /** sofa runtime context */
    protected SofaRuntimeContext      sofaRuntimeContext;
    /** xml elements */
    protected List<TypedStringValue>  elements;
    /** spring context */
    protected ApplicationContext      applicationContext;
    /** bindings */
    protected List<Binding>           bindings = new ArrayList<>(2);
    /** document encoding */
    protected String                  documentEncoding;
    /** repeat times */
    protected String                  repeatReferLimit;
    /** binding converter factory */
    protected BindingConverterFactory bindingConverterFactory;
    /** binding adapter factory */
    protected BindingAdapterFactory   bindingAdapterFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Element> tempElements = new ArrayList<>();
        for (TypedStringValue element : elements) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            InputSource inputSource = new InputSource(new ByteArrayInputStream(element.getValue()
                .getBytes()));
            inputSource.setEncoding(documentEncoding);
            Element node = documentBuilderFactory.newDocumentBuilder().parse(inputSource)
                .getDocumentElement();
            tempElements.add(node);
        }
        sofaRuntimeContext = applicationContext.getBean(
            SofaRuntimeFrameworkConstants.SOFA_RUNTIME_CONTEXT_BEAN_ID, SofaRuntimeContext.class);
        bindingConverterFactory = applicationContext.getBean(
            SofaRuntimeFrameworkConstants.BINDING_CONVERTER_FACTORY_BEAN_ID,
            BindingConverterFactory.class);
        bindingAdapterFactory = applicationContext.getBean(
            SofaRuntimeFrameworkConstants.BINDING_ADAPTER_FACTORY_BEAN_ID,
            BindingAdapterFactory.class);
        this.bindings = parseBindings(tempElements, applicationContext, isInBinding());
        doAfterPropertiesSet();
    }

    protected List<Binding> parseBindings(List<Element> parseElements,
                                          ApplicationContext appContext, boolean isInBinding) {
        List<Binding> result = new ArrayList<>();

        if (parseElements != null) {
            for (Element element : parseElements) {
                String tagName = element.getLocalName();
                BindingConverter bindingConverter = bindingConverterFactory
                    .getBindingConverterByTagName(tagName);

                if (bindingConverter == null) {
                    if (!tagName.equals(SofaRuntimeFrameworkConstants.BINDING_PREFIX
                                        + JvmBinding.JVM_BINDING_TYPE.toString())) {
                        throw new ServiceRuntimeException("Can't find BindingConverter of type "
                                                          + tagName);
                    }
                    continue;
                }

                BindingConverterContext bindingConverterContext = new BindingConverterContext();
                bindingConverterContext.setInBinding(isInBinding);
                bindingConverterContext.setApplicationContext(appContext);
                bindingConverterContext.setAppName(sofaRuntimeContext.getAppName());
                bindingConverterContext.setAppClassLoader(sofaRuntimeContext.getAppClassLoader());
                bindingConverterContext.setRepeatReferLimit(repeatReferLimit);

                setProperties(bindingConverterContext);

                Binding binding = bindingConverter.convert(element, bindingConverterContext);

                result.add(binding);
            }
        }

        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public Class<?> getInterfaceClass() {
        if (interfaceClass == null && this.applicationContext != null) {
            try {
                interfaceClass = this.getClass().getClassLoader().loadClass(interfaceType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        return interfaceClass;
    }

    public List<Binding> getBindings() {
        return bindings;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public void setElements(List<TypedStringValue> elements) {
        this.elements = elements;
    }

    public void setDocumentEncoding(String documentEncoding) {
        this.documentEncoding = documentEncoding;
    }

    public void setRepeatReferLimit(String repeatReferLimit) {
        this.repeatReferLimit = repeatReferLimit;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    /**
     * is in binding or not
     *
     * @return true or false
     */
    protected abstract boolean isInBinding();

    protected abstract void doAfterPropertiesSet() throws Exception;

    protected abstract void setProperties(BindingConverterContext bindingConverterContext);
}
