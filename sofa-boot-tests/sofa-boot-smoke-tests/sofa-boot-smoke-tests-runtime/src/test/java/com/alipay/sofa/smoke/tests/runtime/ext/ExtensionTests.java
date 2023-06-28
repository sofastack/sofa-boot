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
package com.alipay.sofa.smoke.tests.runtime.ext;

import com.alipay.sofa.common.xmap.Context;
import com.alipay.sofa.common.xmap.DOMSerializer;
import com.alipay.sofa.common.xmap.Resource;
import com.alipay.sofa.common.xmap.XMap;
import com.alipay.sofa.runtime.api.aware.ExtensionClientAware;
import com.alipay.sofa.runtime.api.client.ExtensionClient;
import com.alipay.sofa.runtime.api.client.param.ExtensionParam;
import com.alipay.sofa.runtime.api.client.param.ExtensionPointParam;
import com.alipay.sofa.runtime.ext.component.ExtensionComponent;
import com.alipay.sofa.runtime.model.ComponentType;
import com.alipay.sofa.runtime.spi.component.ComponentInfo;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;
import com.alipay.sofa.runtime.spi.spring.SofaRuntimeContextAware;
import com.alipay.sofa.smoke.tests.runtime.RuntimeSofaBootApplication;
import com.alipay.sofa.smoke.tests.runtime.extension.bean.IExtension;
import com.alipay.sofa.smoke.tests.runtime.extension.bean.SimpleSpringBean;
import com.alipay.sofa.smoke.tests.runtime.extension.bean.SimpleSpringListBean;
import com.alipay.sofa.smoke.tests.runtime.extension.bean.SimpleSpringMapBean;
import com.alipay.sofa.smoke.tests.runtime.extension.descriptor.ClientExtensionDescriptor;
import com.alipay.sofa.smoke.tests.runtime.extension.descriptor.SimpleExtensionDescriptor;
import com.alipay.sofa.smoke.tests.runtime.extension.descriptor.XMapTestDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Extension.
 *
 * @author ruoshan
 * @author huzijie
 * @since 2.6.0
 */
@SpringBootTest(classes = RuntimeSofaBootApplication.class)
@Import(ExtensionTests.ExtensionTestConfiguration.class)
public class ExtensionTests implements ExtensionClientAware, SofaRuntimeContextAware {

    private ExtensionClient      extensionClient;

    private SofaRuntimeContext   sofaRuntimeContext;

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
    public void xMap() throws Exception {
        XMap xMap = new XMap();
        Resource resource = new Resource(new Context(), "spring/extension/extension-xmap.xml");
        xMap.register(XMapTestDescriptor.class, true);
        Object object = xMap.load(resource.toURL());
        assertThat(object).isNotNull();
        assertThat(object instanceof XMapTestDescriptor).isTrue();
        XMapTestDescriptor xMapTestDescriptor = (XMapTestDescriptor) object;
        assertThat("xmaptest").isEqualTo(xMapTestDescriptor.getValue());
        assertThat(1).isEqualTo(xMap.loadAll(resource.toURL()).length);
    }

    @Test
    public void xMapAsString() throws Exception {
        XMap xMap = new XMap();
        xMap.register(XMapTestDescriptor.class);
        Document document = xMap.asXml(new XMapTestDescriptor(), null);
        assertThat(document).isNotNull();
        assertThat("xmaptest").isEqualTo(document.getDocumentElement().getTagName());

        String value = DOMSerializer.toString(document);
        assertThat(value).isNotNull();
        assertThat(value.contains("xmaptest")).isTrue();

        OutputStream out = new ByteArrayOutputStream();
        DOMSerializer.write(document, out);
        assertThat(out).isNotNull();
        assertThat(out.toString().contains("xmaptest")).isTrue();

        DOMSerializer.write(document.getDocumentElement(), out);
        assertThat(out).isNotNull();
        assertThat(out.toString().contains("xmaptest")).isTrue();
    }

    @Test
    public void extensionClient() throws Exception {
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("clientValue");
        extensionPointParam.setTargetName("iExtension");
        extensionPointParam.setTarget(iExtension);
        extensionPointParam.setContributionClass(ClientExtensionDescriptor.class);
        extensionClient.publishExtensionPoint(extensionPointParam);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(Thread.currentThread().getContextClassLoader()
            .getResource("spring/extension/extension.xml").toURI()));
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setTargetName("clientValue");
        extensionParam.setTargetInstanceName("iExtension");
        extensionParam.setElement(doc.getDocumentElement());
        extensionClient.publishExtension(extensionParam);

        assertThat("SOFABoot Extension Client Test").isEqualTo(iExtension.getClientValue());
    }

    @Test
    public void simple() throws Exception {
        assertThat(iExtension).isNotNull();
        assertThat(iExtension.getSimpleExtensionDescriptor()).isNotNull();
        assertThat("SOFABoot Extension Test").isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getStringValue());
        assertThat("value with path").isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getStringValueWithPath());
        assertThat(Integer.valueOf(10)).isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getIntValue());
        assertThat(Long.valueOf(20)).isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getLongValue());
        assertThat(1.1f).isEqualTo(iExtension.getSimpleExtensionDescriptor().getFloatValue());
        assertThat(2.2d).isEqualTo(iExtension.getSimpleExtensionDescriptor().getDoubleValue());
        assertThat(Boolean.TRUE).isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getBooleanValue());
        assertThat(
            iExtension.getSimpleExtensionDescriptor().getDateValue().toString().contains("2019"))
            .isTrue();
        assertThat("file").isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getFileValue().getName());
        assertThat(SimpleExtensionDescriptor.class.getName()).isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getClassValue().getName());
        assertThat("http://test").isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getUrlValue().toString());
        assertThat("extension.xml").isEqualTo(
            iExtension.getSimpleExtensionDescriptor().getResourceValue().toFile().getName());
    }

    @Test
    public void list() {
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getValues().size());
        assertThat(iExtension.getListExtensionDescriptor().getValues().contains("test1")).isTrue();
        assertThat(iExtension.getListExtensionDescriptor().getValues().contains("test2")).isTrue();
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getIntValues().length);
        assertThat(1).isEqualTo(iExtension.getListExtensionDescriptor().getIntValues()[0]);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getIntValues()[1]);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getLongValues().length);
        assertThat(11).isEqualTo(iExtension.getListExtensionDescriptor().getLongValues()[0]);
        assertThat(22).isEqualTo(iExtension.getListExtensionDescriptor().getLongValues()[1]);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getFloatValues().length);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getDoubleValues().length);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getBooleanValues().length);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getCharValues().length);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getShortValues().length);
        assertThat(2).isEqualTo(iExtension.getListExtensionDescriptor().getByteValues().length);
    }

    @Test
    public void map() {
        assertThat(2).isEqualTo(iExtension.getTestMap().size());
        assertThat("testMapValue1").isEqualTo(iExtension.getTestMap().get("testMapKey1"));
        assertThat("testMapValue2").isEqualTo(iExtension.getTestMap().get("testMapKey2"));
    }

    @Test
    public void simpleString() {
        assertThat(iExtension.getSimpleSpringBean()).isNotNull();
        assertThat(iExtension.getSimpleSpringBean()).isEqualTo(simpleSpringBean);
    }

    @Test
    public void springList() {
        assertThat(2).isEqualTo(iExtension.getSimpleSpringListBeans().size());
        assertThat(iExtension.getSimpleSpringListBeans().contains(simpleSpringListBean1)).isTrue();
        assertThat(iExtension.getSimpleSpringListBeans().contains(simpleSpringListBean2)).isTrue();
    }

    @Test
    public void springMap() {
        assertThat(2).isEqualTo(iExtension.getSimpleSpringMapBeanMap().size());
        assertThat(springMapBean1).isEqualTo(
            iExtension.getSimpleSpringMapBeanMap().get("testMapSpringKey1"));
        assertThat(springMapBean2).isEqualTo(
            iExtension.getSimpleSpringMapBeanMap().get("testMapSpringKey2"));
    }

    @Test
    public void context() {
        assertThat("testContextValue\n").isEqualTo(iExtension.getTestContextValue());
    }

    @Test
    public void parent() {
        assertThat("testParentValue").isEqualTo(iExtension.getTestParentValue());
    }

    @Test
    public void bad() {
        assertThat(iExtension.getBadDescriptor()).isNull();
    }

    @Test
    public void testNotExist() {
        Collection<ComponentInfo> componentInfos =
                sofaRuntimeContext.getComponentManager().getComponentInfosByType(new ComponentType("extension"));
        componentInfos.forEach(componentInfo -> {
            if (componentInfo instanceof ExtensionComponent) {
                if (componentInfo.getName().getName().contains("noExist")) {
                    assertThat(componentInfo.isHealthy().isHealthy()).isFalse();
                    assertThat("Can not find corresponding ExtensionPoint: iExtension$noExist").isEqualTo(
                            componentInfo.isHealthy().getHealthReport());
                }
            }
        });
    }

    @Override
    public void setExtensionClient(ExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }

    @Override
    public void setSofaRuntimeContext(SofaRuntimeContext sofaRuntimeContext) {
        this.sofaRuntimeContext = sofaRuntimeContext;
    }

    /**
     * @author huzijie
     * @version ExtensionTestConfiguration.java, v 0.1 2023年02月02日 3:01 PM huzijie Exp $
     */
    @TestConfiguration
    @ImportResource("classpath*:spring/extension/test-extension.xml")
    static class ExtensionTestConfiguration {
    }
}