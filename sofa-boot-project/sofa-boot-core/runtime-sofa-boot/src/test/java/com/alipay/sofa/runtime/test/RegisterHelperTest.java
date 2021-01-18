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
package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.service.component.impl.ServiceImpl;
import com.alipay.sofa.runtime.service.helper.ServiceRegisterHelper;
import com.alipay.sofa.runtime.spi.binding.Binding;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/14
 */
public class RegisterHelperTest {
    @Test
    public void serviceTest() {
        Service service = new ServiceImpl("", null, null);
        try {
            ServiceRegisterHelper.registerService(service, null, null, null);
        } catch (Throwable e) {
            Assert.assertEquals(1, service.getBindings().size());
            Assert.assertEquals(JvmBinding.JVM_BINDING_TYPE, ((Binding) service.getBindings()
                .toArray()[0]).getBindingType());
        }
    }

    @Test
    public void referenceTest() {
        // TODO: add mock
        //        Reference reference = new ReferenceImpl("", null, InterfaceMode.spring, true);
        //        try {
        //            ReferenceRegisterHelper.registerReference(reference, null, null);
        //        } catch (Throwable e) {
        //            Assert.assertEquals(1, reference.getBindings().size());
        //            Assert.assertEquals(JvmBinding.JVM_BINDING_TYPE,((Binding) reference.getBindings().toArray()[0]).getBindingType());
        //        }
    }
}
