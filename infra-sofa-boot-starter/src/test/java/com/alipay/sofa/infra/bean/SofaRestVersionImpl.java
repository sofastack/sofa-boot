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
package com.alipay.sofa.infra.bean;

import com.alipay.sofa.infra.standard.AbstractSofaBootMiddlewareVersionFacade;

import java.util.LinkedList;
import java.util.List;

/**
 * SofaRestVersionImpl
 *
 * @author yangguanchao
 * @since 2018/03/10
 */
public class SofaRestVersionImpl extends AbstractSofaBootMiddlewareVersionFacade {

    @Override
    public String getName() {
        return "SOFA REST";
    }

    @Override
    public String getVersion() {
        return "1.0.1.SOFAREST";
    }

    @Override
    public List<String> getAuthors() {
        List<String> authors = new LinkedList<>();
        authors.add("guanchao.ygc");
        return authors;
    }

    @Override
    public String getDocs() {
        return "https://www.cloud.alipay.com/docs";
    }
}
