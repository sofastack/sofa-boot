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
package com.alipay.sofa.boot.ark;

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.model.BizState;

import java.net.URL;
import java.util.Map;
import java.util.Set;

/**
 * Mock Biz.
 *
 * @author huzijie
 * @version MockBiz.java, v 0.1 2023年04月06日 11:45 AM huzijie Exp $
 */
public class MockBiz implements Biz {

    private boolean  called = false;

    private BizState bizState;

    private String   bizVersion;

    @Override
    public void start(String[] strings) throws Throwable {

    }

    @Override
    public void stop() throws Throwable {

    }

    @Override
    public boolean isDeclared(URL url, String s) {
        return false;
    }

    @Override
    public boolean isDeclaredMode() {
        return false;
    }

    @Override
    public void setCustomBizName(String s) {

    }

    @Override
    public String getBizName() {
        return null;
    }

    @Override
    public String getBizVersion() {
        return bizVersion;
    }

    @Override
    public String getIdentity() {
        return null;
    }

    @Override
    public String getMainClass() {
        return null;
    }

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Set<String> getDenyImportPackages() {
        return null;
    }

    @Override
    public Set<String> getDenyImportPackageNodes() {
        return null;
    }

    @Override
    public Set<String> getDenyImportPackageStems() {
        return null;
    }

    @Override
    public Set<String> getDenyImportClasses() {
        return null;
    }

    @Override
    public Set<String> getDenyImportResources() {
        return null;
    }

    @Override
    public Set<String> getDenyPrefixImportResourceStems() {
        return null;
    }

    @Override
    public Set<String> getDenySuffixImportResourceStems() {
        return null;
    }

    @Override
    public ClassLoader getBizClassLoader() {
        called = true;
        return this.getClass().getClassLoader();
    }

    @Override
    public BizState getBizState() {
        return bizState;
    }

    @Override
    public String getWebContextPath() {
        return null;
    }

    @Override
    public Map<String, String> getAttributes() {
        return null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    public void setBizState(BizState bizState) {
        this.bizState = bizState;
    }

    public boolean isCalled() {
        return called;
    }

    public void setBizVersion(String bizVersion) {
        this.bizVersion = bizVersion;
    }
}
