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
package com.alipay.sofa.boot.ark.handler;

import com.alipay.sofa.ark.spi.event.biz.AfterBizStartupEvent;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.ark.spi.service.event.EventHandler;
import com.alipay.sofa.boot.ark.invoke.DynamicJvmServiceProxyFinder;

/**
 * Implementation of {@link EventHandler<AfterBizStartupEvent>} mark biz startup.
 *
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * @author huzijie
 * Created on 03/09/2021
 */
public class AfterBizStartupEventHandler implements EventHandler<AfterBizStartupEvent> {

    @Override
    public void handleEvent(AfterBizStartupEvent event) {
        Biz biz = event.getSource();
        DynamicJvmServiceProxyFinder.getInstance().afterBizStartup(biz);
    }

    @Override
    public int getPriority() {
        return LOWEST_PRECEDENCE;
    }
}
