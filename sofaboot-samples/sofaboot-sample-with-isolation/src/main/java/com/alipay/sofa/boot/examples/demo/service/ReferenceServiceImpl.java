package com.alipay.sofa.boot.examples.demo.service;

import com.alipay.sofa.boot.examples.demo.service.facade.ReferenceService;
import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import org.springframework.util.Assert;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class ReferenceServiceImpl implements ReferenceService {

    @Override
    public void reference() {
        ConsumerConfig<SampleService> consumerConfig = new ConsumerConfig<SampleService>()
            .setInterfaceId(SampleService.class.getName()).setProtocol("bolt")
            .setDirectUrl("127.0.0.1:9696");

        SampleService sampleService = consumerConfig.refer();

        Assert.isTrue("service".equals(sampleService.service()));
    }

}