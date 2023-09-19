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
package com.alipay.sofa.smoke.tests.rpc.boot.bean.threadpool;

import com.alipay.sofa.runtime.api.annotation.SofaServiceBean;
import com.alipay.sofa.runtime.api.annotation.SofaServiceBinding;

/**
 * @author huzijie
 * @version ThreadPoolServiceAnnotationImpl.java, v 0.1 2023年09月19日 6:05 PM huzijie Exp $
 */
@SofaServiceBean(uniqueId = "annotation", bindings = { @SofaServiceBinding(bindingType = "bolt", userThreadPool = "customerThreadPool") })
public class ThreadPoolServiceAnnotationImpl implements ThreadPoolService {

    @Override
    public String sayThreadPool(String string) {
        return string + "[" + Thread.currentThread().getName() + "]";
    }
}
