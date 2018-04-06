/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.runtime.spring.factory;

import com.alipay.sofa.runtime.model.InterfaceMode;
import com.alipay.sofa.runtime.service.binding.JvmBinding;
import com.alipay.sofa.runtime.service.component.Reference;
import com.alipay.sofa.runtime.service.component.impl.ReferenceImpl;
import com.alipay.sofa.runtime.service.helper.ReferenceRegisterHelper;
import com.alipay.sofa.runtime.spi.service.BindingConverterContext;
import org.springframework.util.Assert;

/**
 * @author xuanbei 18/3/1
 */
public class ReferenceFactoryBean extends AbstractContractFactoryBean {
    private Object    proxy;

    /** local first or not */
    protected boolean localFirst = true;
    /** jvm service or not */
    protected boolean jvmService;
    /** load balance **/
    private String    loadBalance;

    @Override
    protected void doAfterPropertiesSet() throws Exception {
        Reference reference = buildReference();
        Assert
            .isTrue(bindings.size() <= 1,
                "Found more than one binding in <sofa:reference/>, <sofa:reference/> can only have one binding.");

        if (bindings.size() == 0) {
            bindings.add(new JvmBinding());
        }

        reference.addBinding(bindings.get(0));
        proxy = ReferenceRegisterHelper.registerReference(reference, sofaRuntimeContext);
    }

    @Override
    protected void setProperties(BindingConverterContext bindingConverterContext) {
        bindingConverterContext.setLoadBalance(loadBalance);
        bindingConverterContext.setBeanId(beanId);
    }

    protected Reference buildReference() {
        return new ReferenceImpl(uniqueId, getInterfaceClass(), InterfaceMode.spring, localFirst,
            jvmService);
    }

    @Override
    public Object getObject() throws Exception {
        return proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return getInterfaceClass();
    }

    @Override
    protected boolean isInBinding() {
        return true;
    }

    public void setLocalFirst(boolean localFirst) {
        this.localFirst = localFirst;
    }

    public void setJvmService(boolean jvmService) {
        this.jvmService = jvmService;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }
}
