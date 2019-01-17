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
package com.alipay.sofa.runtime.integration.extension;

import com.alipay.sofa.runtime.api.aware.ExtensionClientAware;
import com.alipay.sofa.runtime.api.client.ExtensionClient;
import com.alipay.sofa.runtime.api.client.param.ExtensionParam;
import com.alipay.sofa.runtime.api.client.param.ExtensionPointParam;
import com.alipay.sofa.runtime.integration.base.SofaBootTestApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 *
 * @author ruoshan
 * @since 2.6.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SofaBootTestApplication.class)
public class ExtensionTest implements ExtensionClientAware {

    private ExtensionClient extensionClient;

    @Autowired
    IExtension              iExtension;

    @Test
    public void test() {
        Assert.assertNotNull(iExtension);
        Assert.assertEquals("SOFABoot Extension Test", iExtension.say());
    }

    @Test
    public void testExtensionClient() throws Exception {
        ExtensionPointParam extensionPointParam = new ExtensionPointParam();
        extensionPointParam.setName("clientWord");
        extensionPointParam.setTargetName("iExtension");
        extensionPointParam.setTarget(iExtension);
        extensionPointParam.setContributionClass(ClientExtensionDescriptor.class);
        extensionClient.publishExtensionPoint(extensionPointParam);

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(new File(Thread.currentThread().getContextClassLoader()
            .getResource("META-INF/extension/extension.xml").toURI()));
        ExtensionParam extensionParam = new ExtensionParam();
        extensionParam.setTargetName("clientWord");
        extensionParam.setTargetInstanceName("iExtension");
        extensionParam.setElement(doc.getDocumentElement());
        extensionClient.publishExtension(extensionParam);

        Assert.assertEquals("SOFABoot Extension Client Test", iExtension.sayFromClient());
    }

    @Override
    public void setExtensionClient(ExtensionClient extensionClient) {
        this.extensionClient = extensionClient;
    }
}