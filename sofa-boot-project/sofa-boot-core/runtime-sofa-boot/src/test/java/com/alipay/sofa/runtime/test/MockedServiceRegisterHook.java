package com.alipay.sofa.runtime.test;

import com.alipay.sofa.runtime.api.ServiceRegisterHook;
import com.alipay.sofa.runtime.service.component.Service;
import com.alipay.sofa.runtime.spi.component.SofaRuntimeContext;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2021/1/18
 */
public class MockedServiceRegisterHook implements ServiceRegisterHook {
    @Override
    public void before(Service service, SofaRuntimeContext sofaRuntimeContext) {

    }

    @Override
    public void after() {

    }

    @Override
    public int order() {
        return 0;
    }
}
