package com.alipay.sofa.runtime.ambush;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class Context {
    private Object invokeResult;

    public Context() {}

    public Context(Object invokeResult) {
        this.invokeResult = invokeResult;
    }

    public Object getInvokeResult() {
        return invokeResult;
    }

    public void setInvokeResult(Object invokeResult) {
        this.invokeResult = invokeResult;
    }
}
