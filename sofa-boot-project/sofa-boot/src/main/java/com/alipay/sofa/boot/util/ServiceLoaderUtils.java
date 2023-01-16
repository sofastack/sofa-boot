/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
