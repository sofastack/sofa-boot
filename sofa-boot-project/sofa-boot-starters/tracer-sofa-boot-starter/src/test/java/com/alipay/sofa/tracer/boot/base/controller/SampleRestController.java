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
package com.alipay.sofa.tracer.boot.base.controller;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * SampleRestController
 *
 * @author yangguanchao
 * @since 2018/05/01
 */
@RestController
public class SampleRestController {

    public static String        ASYNC_RESP = "Hello World!";

    private static final String template   = "Hello, %s!";

    private final AtomicLong    counter    = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        Greeting greeting = new Greeting();
        greeting.setSuccess(true);
        greeting.setId(counter.incrementAndGet());
        greeting.setContent(String.format(template, name));
        return greeting;
    }

    @RequestMapping("/noDigestLog")
    public Greeting noDigestLog() {
        Greeting greeting = new Greeting();
        greeting.setSuccess(true);
        greeting.setId(counter.incrementAndGet());
        greeting.setContent("noDigestLog");
        return greeting;
    }

    @RequestMapping("/asyncServlet")
    public void asyncServlet(HttpServletRequest request, HttpServletResponse response)
                                                                                      throws IOException {
        AsyncContext asyncContext = request.startAsync();
        asyncContext.getResponse().getWriter().write(ASYNC_RESP);
        asyncContext.complete();
    }

    @RequestMapping("/feign")
    public String feign() {
        return "feign";
    }

    public static class Greeting {

        private boolean success = false;
        private long    id;
        private String  content;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
