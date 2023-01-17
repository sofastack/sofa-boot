package com.alipay.sofa.boot.annotation;

import org.springframework.core.env.Environment;

/**
 * @author huzijie
 * @version DefaultPlaceHolderBinder.java, v 0.1 2023年01月17日 3:38 PM huzijie Exp $
 */
public class DefaultPlaceHolderBinder implements PlaceHolderBinder {

    public static final DefaultPlaceHolderBinder INSTANCE = new DefaultPlaceHolderBinder();

    @Override
    public String bind(Environment environment, String string) {
        return environment.resolvePlaceholders(string);
    }
}
