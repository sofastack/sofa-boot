/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spring.factory;

import com.alipay.sofa.runtime.service.impl.BindingFactoryContainer;
import com.alipay.sofa.runtime.spi.binding.Binding;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.service.BindingConverter;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import com.alipay.sofa.runtime.spi.service.BindingConverterFactory;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
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
public abstract class AbstractContractFactoryBean implements InitializingBean,
                                                 SofaRuntimeContextAware, FactoryBean,
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
    protected List<Binding>           bindings                = new ArrayList<Binding>(2);
    /** document encoding */
    protected String                  documentEncoding;
    /** repeat times */
    protected String                  repeatReferLimit;
    /** binding converter factory */
    protected BindingConverterFactory bindingConverterFactory = BindingFactoryContainer.getBindingConverterFactory();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<Element> tempElements = new ArrayList<Element>();
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
        this.bindings = parseBindings(tempElements, applicationContext, isInBinding());
        doAfterPropertiesSet();
    }

    protected List<Binding> parseBindings(List<Element> parseElements,
                                          ApplicationContext appContext, boolean isInBinding) {
        List<Binding> result = new ArrayList<Binding>();

        if (parseElements != null) {
            for (Element element : parseElements) {
                String tagName = element.getLocalName();
                BindingConverter bindingConverter = bindingConverterFactory
                    .getBindingConverterByTagName(tagName);

                if (bindingConverter == null) {
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

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }
}
