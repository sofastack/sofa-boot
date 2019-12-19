package com.alipay.sofa.rpc.boot.test.xsd;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">guaner.zzx</a>
 * Created on 2019/12/18
 */
public class WhateverClass implements WhateverInterface {
    @Override
    public String say() {
        return "whatever";
    }
}
