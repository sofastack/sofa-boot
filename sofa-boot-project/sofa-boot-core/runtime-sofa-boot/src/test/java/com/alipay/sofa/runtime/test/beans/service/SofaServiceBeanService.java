/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.runtime.test.beans.service;

import com.alipay.sofa.runtime.api.annotation.SofaServiceBean;

/**
 * SofaServiceBeanTest
 *
 * @author xunfang
 * @version SofaServiceBeanTest.java, v 0.1 2023/5/23
 */
@SofaServiceBean(value = "sofaServiceBeanService", uniqueId = "sofaServiceBeanService")
public class SofaServiceBeanService {
    public String service() {
            return "sofaServiceBeanService";
        }
}
