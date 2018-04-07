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
 * abstract implementation
 *
 * @author xuanbei 18/3/2
 */
public abstract class AbstractImplementation implements Implementation {

    protected String   name;
    protected Object   target;
    protected Class<?> targetClass;

    protected boolean  singleton = true;
    protected boolean  lazyInit  = false;
    protected boolean  factory   = false;

    protected AbstractImplementation() {

    }

    public String getName() {
        return this.name;
    }

    public Object getTarget() {
        return this.target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Class<?> getTargetClass() {
        return this.targetClass;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    public boolean isFactory() {
        return this.factory;
    }

    public void validate() throws ServiceValidationException {

    }
}
