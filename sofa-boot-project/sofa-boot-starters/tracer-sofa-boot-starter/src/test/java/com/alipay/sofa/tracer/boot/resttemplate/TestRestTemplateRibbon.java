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
package com.alipay.sofa.tracer.boot.resttemplate;

import com.alipay.sofa.tracer.boot.base.AbstractTestCloudBase;
import com.sofa.alipay.tracer.plugins.rest.RestTemplateLogEnum;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/9/12 12:52 AM
 * @since:
 **/
@ActiveProfiles("ribbon")
@Import({ FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class })
@EnableFeignClients
public class TestRestTemplateRibbon extends AbstractTestCloudBase {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRestTemplate() throws IOException, InterruptedException {
        Assert.assertTrue(restTemplate.getInterceptors().size() >= 1);
        // test for connect error
        try {
            restTemplate.getForObject("http://localhost:1234", String.class);
        } catch (Throwable t) {
            Assert.assertTrue(t != null);
        } finally {
            Thread.sleep(500);
            //wait for async output
            List<String> contents = FileUtils.readLines(new File(
                logDirectoryPath + File.separator
                        + RestTemplateLogEnum.REST_TEMPLATE_DIGEST.getDefaultLogName()));
            Assert.assertTrue(contents.size() == 1);
            Assert.assertTrue(contents.get(0).contains("Connection refused"));
        }

        String re = restTemplate.getForObject("http://localhost:8890/feign", String.class);
        Assert.assertTrue(re.equalsIgnoreCase("feign"));

        Thread.sleep(500);
        //wait for async output
        List<String> contents = FileUtils.readLines(new File(
            logDirectoryPath + File.separator
                    + RestTemplateLogEnum.REST_TEMPLATE_DIGEST.getDefaultLogName()));
        Assert.assertTrue(contents.size() == 2);
    }
}
