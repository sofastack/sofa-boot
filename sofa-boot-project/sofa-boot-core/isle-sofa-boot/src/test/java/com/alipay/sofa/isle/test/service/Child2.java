package com.alipay.sofa.isle.test.service;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/10/14
 */
public class Child2 {
    @Autowired
    private GrandChild1 grandChild1;

    @Autowired
    private GrandChild2 grandChild2;

    @Autowired
    private GrandChild3 grandChild3;

    public void sleepInit() {
        try {
            Thread.sleep(20);
        } catch (Exception e) {
            //
        }
    }
}
