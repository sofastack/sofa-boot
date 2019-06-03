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
package com.alipay.sofa.rpc.boot.common;

import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import com.alipay.sofa.rpc.log.LogCodes;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 *
 * Spring工具
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 * @author <a href="mailto:caojie.cj@antfin.com">CaoJie</a>
 */
public class SofaBootRpcSpringUtil {

    private static final Logger LOGGER = SofaBootRpcLoggerFactory.getLogger(SofaBootRpcSpringUtil.class);

    /**
     * 根据配置的ref以及class字符串，获得真正的spring bean
     * 先优先获得refBean，再获得class
     *
     * @param beanRef            spring ref
     * @param beanClass          简单class配置
     * @param applicationContext spring上下文
     * @param appClassLoader     业务上下文
     * @return 业务bean
     */
    public static Object getSpringBean(String beanRef, String beanClass,
                                       ApplicationContext applicationContext, ClassLoader appClassLoader,
                                       String appName) {
        Object callbackHandler = null;

        if (StringUtils.hasText(beanRef)) {
            if (applicationContext == null) {
                LOGGER.error("get bean from spring failed. beanRef[" + beanRef + "];classLoader[" + appClassLoader +
                    "];appName[" + appName + "]");
            } else {
                callbackHandler = applicationContext.getBean(beanRef);
            }
        } else if (StringUtils.hasText(beanClass)) {
            callbackHandler = newInstance(beanClass, appClassLoader, appName);
        }

        return callbackHandler;
    }

    /**
     * 根据配置的ref获得真正的spring bean
     *
     *
     * @param beanRef            spring ref
     * @param applicationContext spring上下文
     * @param appClassLoader     业务上下文
     * @return 业务bean
     */
    public static Object getSpringBean(String beanRef,
                                       ApplicationContext applicationContext,
                                       ClassLoader appClassLoader,
                                       String appName) {
        Object object = null;

        if (StringUtils.hasText(beanRef)) {
            if (applicationContext == null) {
                LOGGER.error("get bean from spring failed. beanRef[" + beanRef + "];classLoader[" + appClassLoader +
                    "];appName[" + appName + "]");
            } else {
                object = applicationContext.getBean(beanRef);
            }
        }

        return object;
    }

    /**
     * 使用指定的classloader实例化某个类
     *
     * @param clazz 全类名
     * @param loader 类加载器
     * @return 类实例
     */
    public static Object newInstance(String clazz, ClassLoader loader, String appName) {
        if (!StringUtils.hasText(clazz)) {
            return null;
        }
        try {
            return Class.forName(clazz, true, loader).newInstance();
        } catch (Exception e) {
            LOGGER.error("new instance failed. clazz[" + clazz + "];classLoader[" + loader + "];appName[" + appName +
                "]", e);
            throw new RuntimeException(LogCodes.getLog(
                LogCodes.ERROR_PROXY_BINDING_CLASS_CANNOT_FOUND, clazz), e);
        }
    }

}