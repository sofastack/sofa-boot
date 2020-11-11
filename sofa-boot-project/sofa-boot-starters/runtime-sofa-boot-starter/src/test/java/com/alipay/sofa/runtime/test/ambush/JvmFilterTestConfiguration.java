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
package com.alipay.sofa.runtime.test.ambush;

import com.alipay.sofa.runtime.filter.JvmFilterContext;
import com.alipay.sofa.runtime.filter.JvmFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
@EnableAutoConfiguration
@ImportResource("classpath:spring/runtime.xml")
public class JvmFilterTestConfiguration {
    public static int beforeCount = 0;
    public static int afterCount  = 0;

    @Bean
    public JvmFilter egressFilter1() {
        return new JvmFilter() {
            @Override
            public boolean before(JvmFilterContext context) {
                ++beforeCount;
                return true;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public boolean after(JvmFilterContext context) {
                return true;
            }
        };
    }

    @Bean
    public JvmFilter egressFilter2() {
        return new JvmFilter() {
            @Override
            public boolean before(JvmFilterContext context) {
                ++beforeCount;
                return true;
            }

            @Override
            public int getOrder() {
                return 100;
            }

            @Override
            public boolean after(JvmFilterContext context) {
                return true;
            }
        };
    }

    @Bean
    public JvmFilter egressFilter3() {
        return new JvmFilter() {
            @Override
            public boolean before(JvmFilterContext context) {
                ++beforeCount;
                return true;
            }

            @Override
            public int getOrder() {
                return -100;
            }

            @Override
            public boolean after(JvmFilterContext context) {
                return true;
            }
        };
    }

    @Bean
    public JvmFilter ingressFilter1() {
        return new JvmFilter() {
            @Override
            public boolean after(JvmFilterContext context) {
                ++afterCount;
                context.setInvokeResult("egressFilter1");
                return false;
            }

            @Override
            public int getOrder() {
                return 0;
            }

            @Override
            public boolean before(JvmFilterContext context) {
                return true;
            }
        };
    }

    @Bean
    public JvmFilter ingressFilter2() {
        return new JvmFilter() {
            @Override
            public boolean after(JvmFilterContext context) {
                ++afterCount;
                return true;
            }

            @Override
            public int getOrder() {
                return -100;
            }

            @Override
            public boolean before(JvmFilterContext context) {
                return true;
            }
        };
    }
}
