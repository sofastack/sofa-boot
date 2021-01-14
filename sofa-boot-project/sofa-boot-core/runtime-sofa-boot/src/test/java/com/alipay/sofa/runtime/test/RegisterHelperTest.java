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
            Assert.assertEquals(JvmBinding.JVM_BINDING_TYPE, ((Binding) service.getBindings().toArray()[0]).getBindingType());
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
