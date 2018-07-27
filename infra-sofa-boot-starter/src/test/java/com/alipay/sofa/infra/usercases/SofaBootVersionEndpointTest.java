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
package com.alipay.sofa.infra.usercases;

import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpoint;
import org.junit.Assert;
import org.junit.Test;
import java.util.List;
import java.util.Properties;

/**
 * @author qilong.zql
 * @since 2.5.0
 */
public class SofaBootVersionEndpointTest {

    @Test
    @SuppressWarnings("unchecked")
    public void test() {
        SofaBootVersionEndpoint sofaBootVersionEndpoint = new SofaBootVersionEndpoint();
        List<Object> result = sofaBootVersionEndpoint.versions();
        List<Object> cacheResult = sofaBootVersionEndpoint.versions();

        Assert.assertNotNull(result);
        Assert.assertNotNull(cacheResult);
        Assert.assertTrue(result.equals(cacheResult));

        Properties versionInfo = (Properties) cacheResult.get(0);

        Assert.assertTrue("com.alipay.sofa".equals(versionInfo.getProperty("GroupId")));
        Assert.assertTrue("infra-sofa-boot-starter".equals(versionInfo.getProperty("ArtifactId")));
        Assert.assertTrue("https://github.com/alipay/sofa-boot".equals(versionInfo
            .getProperty("Doc-Url")));
    }
}