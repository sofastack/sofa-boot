package com.alipay.sofa.runtime;

import com.alipay.sofa.boot.startup.BeanStat;
import com.alipay.sofa.boot.startup.BeanStatCustomizer;
import com.alipay.sofa.runtime.ext.spring.ExtensionFactoryBean;
import com.alipay.sofa.runtime.ext.spring.ExtensionPointFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ReferenceFactoryBean;
import com.alipay.sofa.runtime.spring.factory.ServiceFactoryBean;

/**
 * @author huzijie
 * @version ComponentBeanStatCustomizer.java, v 0.1 2023年01月12日 7:47 PM huzijie Exp $
 */
public class ComponentBeanStatCustomizer implements BeanStatCustomizer {

    @Override
    public BeanStat customize(String beanName, Object bean, BeanStat bs) {
        if (bean instanceof ServiceFactoryBean) {
            bs.setInterfaceType(((ServiceFactoryBean) bean).getInterfaceType());
            return null;
        } else if (bean instanceof ReferenceFactoryBean) {
            bs.setInterfaceType(((ReferenceFactoryBean) bean).getInterfaceType());
            return null;
        }
        if (bean instanceof ExtensionFactoryBean) {
            bs.setInterfaceType(((ExtensionFactoryBean) bean).getPoint());
            return null;
        }
        if (bean instanceof ExtensionPointFactoryBean) {
            bs.setInterfaceType(((ExtensionPointFactoryBean) bean).getName());
            return null;
        }
        return bs;
    }
}
