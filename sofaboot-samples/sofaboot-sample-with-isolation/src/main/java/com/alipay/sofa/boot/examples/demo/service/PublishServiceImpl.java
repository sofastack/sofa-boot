package com.alipay.sofa.boot.examples.demo.service;

import com.alipay.sofa.boot.examples.demo.service.facade.PublishService;
import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.ServerConfig;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class PublishServiceImpl implements PublishService {

    @Override
    public void publish() {
        ServerConfig serverConfig = new ServerConfig().setProtocol("bolt").setPort(9696);

        ProviderConfig<SampleService> providerConfig = new ProviderConfig<SampleService>()
            .setInterfaceId(SampleService.class.getName()).setRef(new SampleServiceImpl())
            .setServer(serverConfig);

        providerConfig.export();
    }

}