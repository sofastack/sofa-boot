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
package com.alipay.sofa.ark.web.embed.tomcat;

import com.alipay.sofa.ark.spi.web.EmbeddedServerService;
import org.apache.catalina.startup.Tomcat;

/**
 * This implementation would be published as ark service.
 *
 * @author guolei.sgl
 * @author qilong.zql
 * @since 3.4.7
 */
public class EmbeddedServerServiceImpl implements EmbeddedServerService<Tomcat> {
    private Tomcat tomcat;
    private Object lock = new Object();

    @Override
    public Tomcat getEmbedServer() {
        return tomcat;
    }

    @Override
    public void setEmbedServer(Tomcat tomcat) {
        if (this.tomcat == null) {
            synchronized (lock) {
                if (this.tomcat == null) {
                    this.tomcat = tomcat;
                }
            }
        }
    }
}