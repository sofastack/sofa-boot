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
package com.alipay.sofa.runtime.spi.component;

import com.alipay.sofa.runtime.api.ServiceValidationException;

/**
 * Default implementation of {@link Implementation}.
 *
 * @author xuanbei 18/3/1
 */
public class DefaultImplementation implements Implementation {

    private String name;

    private Object target;

    public DefaultImplementation() {

    }

    public DefaultImplementation(String name) {
        this.name = name;
    }

    public DefaultImplementation(Object target) {
        this.target = target;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getTarget() {
        return target;
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public boolean isFactory() {
        return false;
    }

    @Override
    public Class<?> getTargetClass() {
        return target.getClass();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public boolean isLazyInit() {
        return false;
    }

    @Override
    public void validate() throws ServiceValidationException {

    }
}
