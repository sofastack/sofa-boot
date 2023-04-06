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
import com.alipay.sofa.ark.spi.service.biz.BizManagerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author huzijie
 * @version MockBizManagerService.java, v 0.1 2023年04月06日 2:33 PM huzijie Exp $
 */
public class MockBizManagerService implements BizManagerService {

    private final List<Biz> bizList = new ArrayList<>();

    @Override
    public boolean registerBiz(Biz biz) {
        return bizList.add(biz);
    }

    @Override
    public Biz unRegisterBiz(String s, String s1) {
        return null;
    }

    @Override
    public Biz unRegisterBizStrictly(String s, String s1) {
        return null;
    }

    @Override
    public List<Biz> getBiz(String s) {
        return null;
    }

    @Override
    public Biz getBiz(String s, String s1) {
        return null;
    }

    @Override
    public Biz getBizByIdentity(String s) {
        return null;
    }

    @Override
    public Biz getBizByClassLoader(ClassLoader classLoader) {
        return null;
    }

    @Override
    public Set<String> getAllBizNames() {
        return null;
    }

    @Override
    public Set<String> getAllBizIdentities() {
        return null;
    }

    @Override
    public List<Biz> getBizInOrder() {
        return bizList;
    }

    @Override
    public Biz getActiveBiz(String s) {
        return null;
    }

    @Override
    public boolean isActiveBiz(String s, String s1) {
        return true;
    }

    @Override
    public void activeBiz(String s, String s1) {

    }

    @Override
    public BizState getBizState(String s, String s1) {
        return null;
    }

    @Override
    public BizState getBizState(String s) {
        return null;
    }

    @Override
    public boolean removeAndAddBiz(Biz biz, Biz biz1) {
        return false;
    }

    @Override
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Biz>> getBizRegistration() {
        return null;
    }

    public void clear() {
        bizList.clear();
    }
}
