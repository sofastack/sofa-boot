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
package com.alipay.sofa.runtime.service.binding;

import com.alipay.sofa.runtime.api.binding.BindingType;
import com.alipay.sofa.runtime.spi.binding.AbstractBinding;
import com.alipay.sofa.runtime.spi.health.HealthResult;
import org.w3c.dom.Element;

/**
 * JVM Service and Reference Binding
 *
 * @author xi.hux@alipay.com
 * @version $Id: JVMBinding.java,v 0.1 2009-9-8 22:03:49 xi.hux Exp $
 */
public class JvmBinding extends AbstractBinding {
    /**
     * binding type: JVM
     */
    public static BindingType JVM_BINDING_TYPE = new BindingType("jvm");

    public JvmBinding() {

    }

    /**
     * backup proxy
     */
    private Object backupProxy;

    public Object getBackupProxy() {
        return backupProxy;
    }

    public void setBackupProxy(Object backupProxy) {
        this.backupProxy = backupProxy;
    }

    /**
     * whether has backup proxy or not
     *
     * @return true or false
     */
    public boolean hasBackupProxy() {
        return this.backupProxy != null;
    }

    @Override
    public String getURI() {
        return null;
    }

    @Override
    public BindingType getBindingType() {
        return JVM_BINDING_TYPE;
    }

    @Override
    public Element getBindingPropertyContent() {
        return null;
    }

    @Override
    public int getBindingHashCode() {
        return JVM_BINDING_TYPE.hashCode();
    }

    @Override
    public HealthResult healthCheck() {
        HealthResult healthResult = new HealthResult(getName());
        healthResult.setHealthy(isHealthy);
        return healthResult;
    }
}