package com.alipay.sofa.boot;

/**
 * Exposes the SOFABoot version.
 *
 * @author huzijie
 * @version SofaBootVersion.java, v 0.1 2023年08月03日 4:36 PM huzijie Exp $
 */
public final class SofaBootVersion {

    private SofaBootVersion() {
    }

    /**
     * Return the full version string of the present SOFABoot codebase.
     * @return the version of SOFABoot
     */
    public static String getVersion() {
        return "3.";
    }
}
