package com.alipay.sofa.boot.util;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;

/**
 * Utility methods that are useful for handle {@link ServiceLoader}.
 *
 * @author huzijie
 * @version ServiceLoaderUtils.java, v 0.1 2023年01月17日 12:20 PM huzijie Exp $
 */
public class ServiceLoaderUtils {

    /**
     * Found all available class form ServiceLoader
     * @param clazz class type
     * @return all available class
     */
    public static <T> Set<T> getClassesByServiceLoader(Class<T> clazz) {
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);

        Set<T> result = new HashSet<>();
        for (T t : serviceLoader) {
            result.add(t);
        }
        return result;
    }
}
