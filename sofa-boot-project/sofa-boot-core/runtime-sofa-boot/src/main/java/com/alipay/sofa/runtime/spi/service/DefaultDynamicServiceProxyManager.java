package com.alipay.sofa.runtime.spi.service;

import com.alipay.sofa.runtime.service.component.ServiceComponent;
import com.alipay.sofa.runtime.spi.binding.Contract;

/**
 * Default implementation of {@link DynamicServiceProxyManager}, return null immediately.
 *
 * @author huzijie
 * @version DefaultDynamicServiceProxyManager.java, v 0.1 2023年01月17日 3:25 PM huzijie Exp $
 */
public class DefaultDynamicServiceProxyManager implements DynamicServiceProxyManager {

    @Override
    public ServiceProxy getDynamicServiceProxy(Contract contract, ClassLoader classLoader) {
        return null;
    }

    @Override
    public ServiceComponent getDynamicServiceComponent(Contract contract, ClassLoader classLoader) {
        return null;
    }
}
