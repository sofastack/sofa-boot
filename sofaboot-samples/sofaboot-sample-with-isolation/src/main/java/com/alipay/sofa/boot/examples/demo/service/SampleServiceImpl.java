/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.boot.examples.demo.service;

import org.springframework.stereotype.Service;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@Service("sampleService")
public class SampleServiceImpl implements SampleService {
    @Override
    public String service() {
        return "service";
    }
}