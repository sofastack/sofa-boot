package com.alipay.sofa.startup.test.beans;

/**
 * @author huzijie
 * @version ChildBean.java, v 0.1 2021年01月04日 9:14 下午 huzijie Exp $
 */
public class ChildBean {
    public static final int CHILD_INIT_TIME = 50;

    public void init() throws InterruptedException {
        Thread.sleep(CHILD_INIT_TIME);
    }
}
