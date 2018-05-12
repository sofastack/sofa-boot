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
package com.alipay.sofa.runtime.spi.binding;

/**
 * abstract binding implementation
 *
 * @author xuanbei 18/2/28
 */
public abstract class AbstractBinding implements Binding {
    protected boolean isHealthy   = true;

    protected boolean isDestroyed = false;

    @Override
    public String dump() {
        return "[" + getBindingType().getType() + "]";
    }

    @Override
    public String getName() {
        return getBindingType().getType();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public void setHealthy(boolean healthy) {
        isHealthy = healthy;
    }

    @Override
    public void setDestroyed(boolean destroyed) {
        isDestroyed = destroyed;
    }

    /**
     * Getter method for property <tt>isDestroyed</tt>.
     *
     * @return property value of isDestroyed
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
}