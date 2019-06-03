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
package com.alipay.sofa.rpc.boot.context;

import com.alipay.sofa.rpc.boot.context.event.SofaBootRpcStartAfterEvent;
import com.alipay.sofa.rpc.boot.context.event.SofaBootRpcStartEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Spring上下文监听器.负责关闭SOFABoot RPC 的资源。
 *
 * @author <a href="mailto:leizhiyuan@gmail.com">leizhiyuan</a>
 */
public class ApplicationContextRefreshedListener implements ApplicationListener {

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {

            ApplicationContext applicationContext = ((ContextRefreshedEvent) event).getApplicationContext();
            //rpc 开始启动事件监听器
            applicationContext.publishEvent(new SofaBootRpcStartEvent(applicationContext));

            //rpc 启动完毕事件监听器
            applicationContext.publishEvent(new SofaBootRpcStartAfterEvent(applicationContext));
        }
    }
}
