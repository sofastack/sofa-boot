package com.alipay.sofa.isle.test.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2019/11/27
 */
public class ServiceImpl implements IService, ApplicationContextAware {
    @Autowired
    private Child1 child1;

    @Autowired
    private Child2 child2;

    @Autowired
    private Child3 child3;

    public String say() {
        return "os-sofaboot";
    }

    public void sleepInit() {
        try {
            Thread.sleep(10);
        } catch (Exception e) {
            //
        }
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
