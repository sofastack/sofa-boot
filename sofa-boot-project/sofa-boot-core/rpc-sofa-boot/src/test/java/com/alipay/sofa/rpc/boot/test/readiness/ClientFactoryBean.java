/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.alipay.sofa.rpc.boot.test.readiness;

import com.alipay.sofa.runtime.api.aware.ClientFactoryAware;
import com.alipay.sofa.runtime.api.client.ClientFactory;
import org.springframework.stereotype.Component;

/**
 * ClientFactoryBean
 *
 * @author xunfang
 * @version ClientFactoryBean.java, v 0.1 2023/2/14
 */
@Component
public class ClientFactoryBean implements ClientFactoryAware {

    private ClientFactory clientFactory;

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }
}
