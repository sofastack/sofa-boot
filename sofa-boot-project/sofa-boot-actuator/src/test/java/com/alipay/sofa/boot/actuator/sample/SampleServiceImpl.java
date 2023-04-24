package com.alipay.sofa.boot.actuator.sample;

/**
 * @author huzijie
 * @version SampleServiceImpl.java, v 0.1 2023年04月24日 10:59 AM huzijie Exp $
 */
public class SampleServiceImpl implements SampleService {
    @Override
    public String hello() {
        return "hello";
    }
}
