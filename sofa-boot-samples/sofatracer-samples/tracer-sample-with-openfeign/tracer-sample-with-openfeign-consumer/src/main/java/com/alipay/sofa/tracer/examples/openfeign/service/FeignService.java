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
package com.alipay.sofa.tracer.examples.openfeign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 基于 Spring Cloud Feign 进行服务调用
 * @author: guolei.sgl (guolei.sgl@antfin.com) 2019/3/5 5:44 PM
 * @since:
 **/
@FeignClient(value = "tracer-provider", fallback = FeignServiceFallbackFactory.class)
public interface FeignService {
    /**
     * 查询用户详情
     * @return
     */
    @RequestMapping(value = "/feign", method = RequestMethod.GET)
    String testFeign();
}
