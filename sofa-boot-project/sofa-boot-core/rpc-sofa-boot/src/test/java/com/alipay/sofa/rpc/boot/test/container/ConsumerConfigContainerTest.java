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
package com.alipay.sofa.rpc.boot.test.container;

import com.alipay.sofa.rpc.boot.container.ConsumerConfigContainer;
import com.alipay.sofa.rpc.boot.runtime.binding.BoltBinding;
import com.alipay.sofa.rpc.boot.runtime.param.BoltBindingParam;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Even
 * @date 2025/5/26 20:33
 */
public class ConsumerConfigContainerTest {

    @Test
    public void test() {
        ConsumerConfigContainer consumerConfigContainer = new ConsumerConfigContainer();
        ConsumerConfig<?> consumerConfig = new ConsumerConfig<>();
        BoltBinding binding = new BoltBinding(new BoltBindingParam(), null, true);
        consumerConfigContainer.addConsumerConfig(binding, consumerConfig);
        binding.setConsumerConfig(consumerConfig);
        Assert.assertNull(null, consumerConfigContainer.getConsumerConfigMap().remove(binding));
    }

}