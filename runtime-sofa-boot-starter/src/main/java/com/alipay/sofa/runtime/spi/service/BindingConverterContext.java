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
package com.alipay.sofa.runtime.spi.service;

import org.springframework.context.ApplicationContext;

/**
 * context information for binding convert
 *
 * @author xuanbei 18/2/28
 */
public class BindingConverterContext {
    /** is inBinding  */
    private boolean            inBinding;
    /** spring context */
    private ApplicationContext applicationContext;
    /** app name */
    private String             appName;
    /** service ref beanId */
    private String             beanId;

    private ClassLoader        appClassLoader;

    private String             loadBalance;

    private String             repeatReferLimit;

    public ClassLoader getAppClassLoader() {
        return appClassLoader;
    }

    public void setAppClassLoader(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    public boolean isInBinding() {
        return inBinding;
    }

    public void setInBinding(boolean inBinding) {
        this.inBinding = inBinding;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getBeanId() {
        return beanId;
    }

    public void setBeanId(String beanId) {
        this.beanId = beanId;
    }

    public String getLoadBalance() {
        return loadBalance;
    }

    public void setLoadBalance(String loadBalance) {
        this.loadBalance = loadBalance;
    }

    public String getRepeatReferLimit() {
        return repeatReferLimit;
    }

    public void setRepeatReferLimit(String repeatReferLimit) {
        this.repeatReferLimit = repeatReferLimit;
    }
}
