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
package com.alipay.sofa.runtime.test.extension.descriptor;

import java.util.HashMap;
import java.util.Map;

import com.alipay.sofa.common.xmap.annotation.XObject;
import com.alipay.sofa.common.xmap.spring.XNodeMapSpring;
import com.alipay.sofa.runtime.test.extension.bean.SimpleSpringMapBean;
import com.alipay.sofa.runtime.test.extension.bean.SimpleSpringMapBeanWithXObject;

/**
 * @author ruoshan
 * @since 2.6.0
 */
@XObject("testSpringMap")
public class SpringMapExtensionDescriptor {

    @XNodeMapSpring(value = "value", key = "@name", componentType = SimpleSpringMapBean.class, type = HashMap.class)
    private Map<String, SimpleSpringMapBean>            values;

    @XNodeMapSpring(value = "value/attribute[@id='springMapTest']", key = "@name", componentType = SimpleSpringMapBeanWithXObject.class, type = HashMap.class)
    private Map<String, SimpleSpringMapBeanWithXObject> valueAttributes;

    public Map<String, SimpleSpringMapBean> getValues() {
        return values;
    }
}
