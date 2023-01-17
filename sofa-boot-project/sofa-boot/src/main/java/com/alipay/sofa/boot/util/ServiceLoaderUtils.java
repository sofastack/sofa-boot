package com.alipay.sofa.boot.util;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * @author huzijie
 * @version ServiceLoaderUtils.java, v 0.1 2023年01月17日 12:20 PM huzijie Exp $
 */
public class ServiceLoaderUtils {

    public static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}
