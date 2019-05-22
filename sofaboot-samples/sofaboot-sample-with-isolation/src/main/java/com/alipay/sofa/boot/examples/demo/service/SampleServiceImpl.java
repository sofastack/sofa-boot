package com.alipay.sofa.boot.examples.demo.service;

import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
public class SampleServiceImpl implements SampleService {
    @Override
    public String service() {
        return "service";
    }
}