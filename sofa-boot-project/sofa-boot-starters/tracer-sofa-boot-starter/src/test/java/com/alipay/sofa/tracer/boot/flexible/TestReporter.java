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

import com.alipay.common.tracer.core.appender.TracerLogRootDaemon;
import com.alipay.common.tracer.core.reporter.facade.Reporter;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/8/3 11:21 AM
 * @since:
 **/
public class TestReporter implements Reporter {

    protected static String logDirectoryPath = TracerLogRootDaemon.LOG_FILE_DIR;

    @Override
    public String getReporterType() {
        return "test";
    }

    @Override
    public void report(SofaTracerSpan span) {
        try {
            File file = new File(logDirectoryPath + "/biz.txt");
            if (file.exists()) {
                FileUtils.cleanDirectory(file);
            } else {
                file.createNewFile();
                BufferedWriter out = new BufferedWriter(new FileWriter(file));
                out.write(span.getOperationName());
                out.flush();
                out.close();
            }
        } catch (IOException e) {
            Assert.fail("Error to use custom reporter");
        }
    }

    @Override
    public void close() {
        // ignore
    }
}
