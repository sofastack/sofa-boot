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
package com.alipay.sofa.isle.test.service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/10/14
 */
public class Child2 {
    @Autowired
    private GrandChild1 grandChild1;

    @Autowired
    private GrandChild2 grandChild2;

    @Autowired
    private GrandChild3 grandChild3;

    public void sleepInit() {
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            //
        }
    }
}
