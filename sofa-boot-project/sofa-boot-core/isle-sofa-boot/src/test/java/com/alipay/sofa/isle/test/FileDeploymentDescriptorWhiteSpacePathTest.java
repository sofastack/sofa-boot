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
package com.alipay.sofa.isle.test;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.isle.deployment.DeploymentBuilder;
import com.alipay.sofa.isle.deployment.DeploymentDescriptor;
import org.junit.Assert;
import org.junit.Test;

import java.net.URL;
import java.util.Enumeration;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/12/18
 */
public class FileDeploymentDescriptorWhiteSpacePathTest {
    @Test
    public void test() throws Exception {
        ClassLoader classLoader = this.getClass().getClassLoader();
        Enumeration<URL> urls = classLoader.getResources("white space/"
                                                         + SofaBootConstants.SOFA_MODULE_FILE);
        while (urls != null && urls.hasMoreElements()) {
            URL url = urls.nextElement();
            DeploymentDescriptor dd = DeploymentBuilder.build(url, null, null, classLoader);
            Assert.assertTrue(dd.isSpringPowered());
        }
    }
}
