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
package com.alipay.sofa.startup.test.beans;

import com.alipay.sofa.service.api.component.Extension;
import com.alipay.sofa.startup.test.beans.facade.TestService;

/**
 * @author huzijie
 * @version ExtensionBean.java, v 0.1 2021年01月05日 11:17 上午 huzijie Exp $
 */
public class ExtensionBean implements TestService {
    private String word;

    @Override
    public void test() {
        System.out.println(word);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void registerExtension(Extension extension) throws Exception {
        Object[] contributions = extension.getContributions();
        String extensionPoint = extension.getExtensionPoint();

        if (contributions == null) {
            return;
        }

        for (Object contribution : contributions) {
            if ("word".equals(extensionPoint)) {
                setWord(((ExtensionDescriptor) contribution).getValue());
            }
        }
    }
}
