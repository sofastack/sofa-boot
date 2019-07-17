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
package com.alipay.sofa.tracer.examples.slf4j.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alipay.common.tracer.core.async.SofaTracerRunnable;

/**
 * SampleRestController
 *
 * @author yangguanchao
 * @since 2018/05/11
 */
@RestController
public class SampleRestController {

    private static Logger       logger   = LoggerFactory.getLogger("MDC-EXAMPLE");

    private static final String TEMPLATE = "Hello, %s!";

    private final AtomicLong    counter  = new AtomicLong();

    /***
     * http://localhost:8080/slf4j
     * @param name name
     * @return map
     */
    @RequestMapping("/slf4j")
    public Map<String, Object> slf4j(@RequestParam(value = "name", defaultValue = "SOFATracer SLF4J MDC DEMO") String name) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("id", counter.incrementAndGet());
        resultMap.put("content", String.format(TEMPLATE, name));
        long id = Thread.currentThread().getId();
        logger.info("SOFATracer Print TraceId and SpanId ");

        // Asynchronous thread transparent transmission
        final SofaTracerRunnable sofaTracerRunnable = new SofaTracerRunnable(new Runnable() {
            @Override
            public void run() {
                logger.info("SOFATracer Print TraceId and SpanId In Child Thread.");
            }
        });

        Thread thread = new Thread(sofaTracerRunnable);
        thread.start();
        return resultMap;
    }
}
