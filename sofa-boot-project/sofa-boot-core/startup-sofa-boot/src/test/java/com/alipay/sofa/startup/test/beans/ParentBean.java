package com.alipay.sofa.startup.test.beans;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author huzijie
 * @version FatherBean.java, v 0.1 2021年01月04日 9:28 下午 huzijie Exp $
 */
public class ParentBean implements InitializingBean {
    public static final int PARENT_INIT_TIM = 30;
    @Autowired
    ChildBean childBean;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread.sleep(PARENT_INIT_TIM);
    }
}
