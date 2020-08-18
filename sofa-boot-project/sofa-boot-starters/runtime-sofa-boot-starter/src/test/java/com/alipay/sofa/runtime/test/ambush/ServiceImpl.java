package com.alipay.sofa.runtime.test.ambush;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class ServiceImpl implements Service {
    @Override
    public String say() {
        return "my service implementation";
    }
}
