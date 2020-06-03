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
package com.alipay.sofa.tracer.boot.flexible;

import com.alipay.sofa.tracer.boot.TestUtil;
import com.alipay.sofa.tracer.boot.base.AbstractTestBase;
import com.alipay.sofa.tracer.plugin.flexible.FlexibleTracer;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.tag.Tags;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/3 11:29 AM
 * @since:
 **/
@ActiveProfiles("flexible")
public class FlexibleReporterTest extends AbstractTestBase {

    @Autowired
    Tracer tracer;

    @Test
    public void testTracer() {
        Assert.assertTrue(tracer instanceof FlexibleTracer);
        Assert.assertTrue(((FlexibleTracer) tracer).getReporter().getReporterType().equals("test"));
    }

    @Test
    public void testManual() throws Exception {
        Span manualSpan = tracer.buildSpan("manualSpan").start();
        manualSpan.setTag("key", "value");
        manualSpan.setTag(Tags.SPAN_KIND.getKey(), Tags.SPAN_KIND_CLIENT);
        manualSpan.finish();
        TestUtil.waitForAsyncLog();
        //wait for async output
        List<String> contents = FileUtils.readLines(new File(logDirectoryPath + File.separator
                                                             + "biz.txt"));
        assertTrue(contents.size() == 1 && contents.get(0).equalsIgnoreCase("manualSpan"));
    }

    @After
    public void clean() {
        File file = new File(logDirectoryPath + File.separator + "biz.txt");
        if (file.exists()) {
            file.delete();
        }
    }
}
