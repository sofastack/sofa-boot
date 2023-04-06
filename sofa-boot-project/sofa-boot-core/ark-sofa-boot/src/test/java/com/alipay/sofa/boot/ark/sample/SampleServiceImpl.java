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
package com.alipay.sofa.boot.ark.sample;

import java.util.Objects;

/**
 * @author huzijie
 * @version SampleServiceImpl.java, v 0.1 2023年04月06日 2:38 PM huzijie Exp $
 */
public class SampleServiceImpl implements SampleService {

    public SampleServiceImpl() {
        this.name = "hello";
    }

    private final String name;

    @Override
    public String hello() {
        return name;
    }

    @Override
    public Pojo transform(Pojo pojo) {
        return pojo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SampleServiceImpl that = (SampleServiceImpl) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "SampleServiceImpl{" + "name='" + name + '\'' + '}';
    }
}
