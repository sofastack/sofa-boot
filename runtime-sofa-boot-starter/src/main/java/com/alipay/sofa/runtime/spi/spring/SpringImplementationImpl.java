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
package com.alipay.sofa.runtime.spi.spring;

import com.alipay.sofa.runtime.spi.component.AbstractImplementation;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Spring Implementation
 *
 * @author xuanbei 18/3/2
 */
public class SpringImplementationImpl extends AbstractImplementation {

    protected ApplicationContext applicationContext;
    protected String             beanName;

    public SpringImplementationImpl(String beanName, ApplicationContext applicationContext) {
        Assert.hasText(beanName);
        Assert.notNull(applicationContext);

        this.beanName = beanName;
        this.applicationContext = applicationContext;
    }

    @Override
    public Object getTarget() {
        return applicationContext.getBean(this.beanName);
    }

    @Override
    public Class<?> getTargetClass() {
        return applicationContext.getBean(this.beanName).getClass();
    }

    @Override
    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public String getName() {
        return beanName;
    }
}
