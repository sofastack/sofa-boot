package com.alipay.sofa.boot.autoconfigure.ark;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties to configure sofa ark.
 *
 * @author huzijie
 * @version SofaArkProperties.java, v 0.1 2023年01月16日 7:48 PM huzijie Exp $
 */
@ConfigurationProperties("sofa.boot.ark")
public class SofaArkProperties {

    /**
     * Whether enable jvm service cache.
     */
    private boolean jvmServiceCache    = false;

    /**
     * Whether enable jvm service invoke serialize.
     */
    private boolean jvmInvokeSerialize       = true;

    public boolean isJvmServiceCache() {
        return jvmServiceCache;
    }

    public void setJvmServiceCache(boolean jvmServiceCache) {
        this.jvmServiceCache = jvmServiceCache;
    }

    public boolean isJvmInvokeSerialize() {
        return jvmInvokeSerialize;
    }

    public void setJvmInvokeSerialize(boolean jvmInvokeSerialize) {
        this.jvmInvokeSerialize = jvmInvokeSerialize;
    }
}
