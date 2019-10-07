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
package com.alipay.sofa.runtime.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import com.alipay.sofa.common.xmap.Context;
import com.alipay.sofa.common.xmap.DOMSerializer;
import com.alipay.sofa.common.xmap.Resource;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.runtime.api.aware.ExtensionClientAware;
import com.alipay.sofa.runtime.api.client.ExtensionClient;
import com.alipay.sofa.runtime.api.client.param.ExtensionParam;
import com.alipay.sofa.runtime.api.client.param.ExtensionPointParam;
import com.alipay.sofa.runtime.test.configuration.RuntimeConfiguration;
import com.alipay.sofa.runtime.test.extension.bean.IExtension;
import com.alipay.sofa.runtime.test.extension.bean.SimpleSpringBean;
import com.alipay.sofa.runtime.test.extension.bean.SimpleSpringListBean;
import com.alipay.sofa.runtime.test.extension.bean.SimpleSpringMapBean;
import com.alipay.sofa.runtime.test.extension.descriptor.ClientExtensionDescriptor;
import com.alipay.sofa.runtime.test.extension.descriptor.SimpleExtensionDescriptor;
import com.alipay.sofa.runtime.test.extension.descriptor.XMapTestDescriptor;

/**
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = "spring.application.name=ExtensionTest")
public class ExtensionTest implements ExtensionClientAware {

    private ExtensionClient      extensionClient;

    @Autowired
    private IExtension           iExtension;

    @Autowired
    private SimpleSpringBean     simpleSpringBean;

    @Autowired
    private SimpleSpringListBean simpleSpringListBean1;

    @Autowired
    private SimpleSpringListBean simpleSpringListBean2;

    @Autowired
    private SimpleSpringMapBean  springMapBean1;

    @Autowired
    private SimpleSpringMapBean  springMapBean2;

    @Test
    public void testXMap() throws Exception {
        XMap xMap = new XMap();
        Resource resource = new Resource(new Context(), "META-INF/extension/extension-xmap.xml");
        xMap.register(XMapTestDescriptor.class, true);
        Object object = xMap.load(resource.toURL());
        Assert.assertNotNull(object);
        Assert.assertTrue(object instanceof XMapTestDescriptor);
        XMapTestDescriptor xMapTestDescriptor = (XMapTestDescriptor) object;
        Assert.assertEquals("xmaptest", xMapTestDescriptor.getValue());
        Assert.assertEquals(1, xMap.loadAll(resource.toURL()).length);
    }

    @Test
    public void testXMapAsString() throws Exception {
        XMap xMap = new XMap();
        xMap.register(XMapTestDescriptor.class);
        Document document = xMap.asXml(new XMapTestDescriptor(), null);
        Assert.assertNotNull(document);
        Assert.assertEquals("xmaptest", document.getDocumentElement().getTagName());

        String value = DOMSerializer.toString(document);
        Assert.assertNotNull(value);
        Assert.assertTrue(value.contains("xmaptest"));

        OutputStream out = new ByteArrayOutputStream();
        DOMSerializer.write(document, out);
        Assert.assertNotNull(out);
        Assert.assertTrue(out.toString().contains("xmaptest"));

        DOMSerializer.write(document.getDocumentElement(), out);
        Assert.assertNotNull(out);
        Assert.assertTrue(out.toString().contains("xmaptest"));
    }

    @Test
    public void testExtensionClient() throws Exception {
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("clientValue");
        extensionPointParam.setTargetName("iExtension");
        extensionPointParam.setTarget(iExtension);
        extensionPointParam.setContributionClass(ClientExtensionDescriptor.class);
        extensionClient.publishExtensionPoint(extensionPointParam);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(Thread.currentThread().getContextClassLoader()
            .getResource("META-INF/extension/extension.xml").toURI()));
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setTargetName("clientValue");
        extensionParam.setTargetInstanceName("iExtension");
        extensionParam.setElement(doc.getDocumentElement());
        extensionClient.publishExtension(extensionParam);

        Assert.assertEquals("SOFABoot Extension Client Test", iExtension.getClientValue());
    }

    @Test
    public void testSimple() throws Exception {
        Assert.assertNotNull(iExtension);
        Assert.assertNotNull(iExtension.getSimpleExtensionDescriptor());
        Assert.assertEquals("SOFABoot Extension Test", iExtension.getSimpleExtensionDescriptor()
            .getStringValue());
        Assert.assertEquals("value with path", iExtension.getSimpleExtensionDescriptor()
            .getStringValueWithPath());
        Assert.assertEquals(new Integer(10), iExtension.getSimpleExtensionDescriptor()
            .getIntValue());
        Assert.assertEquals(new Long(20), iExtension.getSimpleExtensionDescriptor().getLongValue());
        Assert.assertEquals(new Float(1.1), iExtension.getSimpleExtensionDescriptor()
            .getFloatValue());
        Assert.assertEquals(new Double(2.2), iExtension.getSimpleExtensionDescriptor()
            .getDoubleValue());
        Assert.assertEquals(Boolean.TRUE, iExtension.getSimpleExtensionDescriptor()
            .getBooleanValue());
        Assert.assertTrue(iExtension.getSimpleExtensionDescriptor().getDateValue().toString()
            .contains("2019"));
        Assert.assertEquals("file", iExtension.getSimpleExtensionDescriptor().getFileValue()
            .getName());
        Assert.assertEquals(SimpleExtensionDescriptor.class.getName(), iExtension
            .getSimpleExtensionDescriptor().getClassValue().getName());
        Assert.assertEquals("http://test", iExtension.getSimpleExtensionDescriptor().getUrlValue()
            .toString());
        Assert.assertEquals("extension.xml", iExtension.getSimpleExtensionDescriptor()
            .getResourceValue().toFile().getName());
    }

    @Test
    public void testList() {
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getValues().size());
        Assert.assertTrue(iExtension.getListExtensionDescriptor().getValues().contains("test1"));
        Assert.assertTrue(iExtension.getListExtensionDescriptor().getValues().contains("test2"));
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getIntValues().length);
        Assert.assertEquals(1, iExtension.getListExtensionDescriptor().getIntValues()[0]);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getIntValues()[1]);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getLongValues().length);
        Assert.assertEquals(11, iExtension.getListExtensionDescriptor().getLongValues()[0]);
        Assert.assertEquals(22, iExtension.getListExtensionDescriptor().getLongValues()[1]);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getFloatValues().length);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getDoubleValues().length);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getBooleanValues().length);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getCharValues().length);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getShortValues().length);
        Assert.assertEquals(2, iExtension.getListExtensionDescriptor().getByteValues().length);
    }

    @Test
    public void testMap() {
        Assert.assertEquals(2, iExtension.getTestMap().size());
        Assert.assertEquals("testMapValue1", iExtension.getTestMap().get("testMapKey1"));
        Assert.assertEquals("testMapValue2", iExtension.getTestMap().get("testMapKey2"));
    }

    @Test
    public void testSimpleString() {
        Assert.assertNotNull(iExtension.getSimpleSpringBean());
        Assert.assertEquals(iExtension.getSimpleSpringBean(), simpleSpringBean);
    }

    @Test
    public void testSpringList() {
        Assert.assertEquals(2, iExtension.getSimpleSpringListBeans().size());
        Assert.assertTrue(iExtension.getSimpleSpringListBeans().contains(simpleSpringListBean1));
        Assert.assertTrue(iExtension.getSimpleSpringListBeans().contains(simpleSpringListBean2));
    }

    @Test
    public void testSpringMap() {
        Assert.assertEquals(2, iExtension.getSimpleSpringMapBeanMap().size());
        Assert.assertEquals(springMapBean1,
            iExtension.getSimpleSpringMapBeanMap().get("testMapSpringKey1"));
        Assert.assertEquals(springMapBean2,
            iExtension.getSimpleSpringMapBeanMap().get("testMapSpringKey2"));
    }

    @Test
    public void testContext() {
        Assert.assertEquals("testContextValue\n", iExtension.getTestContextValue());
    }

    @Test
    public void testParent() {
        Assert.assertEquals("testParentValue", iExtension.getTestParentValue());
    }

    @Test
    public void testBad() {
        Assert.assertNull(iExtension.getBadDescriptor());
    }

    @Override
    public void setExtensionClient(ExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }

    @Configuration
    @Import(RuntimeConfiguration.class)
    @ImportResource("classpath*:META-INF/extension/test-extension.xml")
    static class ExtensionTestConfiguration {

    }
}