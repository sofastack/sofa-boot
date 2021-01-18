package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.ReferenceRegisterHook;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/18
 */
public class MockedReferenceRegisterHook implements ReferenceRegisterHook {
    @Override
    public void before(Reference reference, SofaRuntimeContext sofaRuntimeContext) {

    }

    @Override
    public void after(Object target) {

    }

    @Override
    public int order() {
        return 0;
    }
}
