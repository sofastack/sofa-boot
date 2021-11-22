package com.alipay.sofa.runtime.api.model;

/**
 * Basic binding type enums.
 *
 * @author TimeChaser
 * @since 2021/11/19 21:19
 */
public enum BindingTypeEnum {

    /**
     * bolt
     */
    BOLT("bolt"),
    /**
     * dubbo
     */
    DUBBO("dubbo"),
    /**
     * h2c
     */
    H2C("h2c"),
    /**
     * http
     */
    HTTP("http"),
    /**
     * jvm
     * <p>
     * Be used as the default choice in {@link com.alipay.sofa.runtime.api.annotation.SofaServiceBinding}
     * and {@link com.alipay.sofa.runtime.api.annotation.SofaReferenceBinding}.
     */
    JVM("jvm"),
    /**
     * rest
     */
    REST("rest"),
    /**
     * triple
     */
    TRIPLE("tri");

    private final String type;

    BindingTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }
}
