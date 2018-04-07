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


package com.alipay.boot.sofarpc.configuration;

import com.alipay.sofa.infra.constants.CommonMiddlewareConstants;
import com.alipay.sofa.infra.constants.SystemPropertyConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 此配置类的路径不对，保留该配置类，具体实现桥接到configuration目录下的Slite2Configuration
 * @author luoguimu123
 * Created by luoguimu on 17/7/17
 */
public class Slite2Configuration {

    private static final Logger logger = LoggerFactory.getLogger(Slite2Configuration.class);

    //用于存放用配置文件中读取属性
    public static Properties properties = new Properties();

    private static Environment environment = null;

    //判断是否被初始化，此处可以不做多线程判断
    private static AtomicBoolean initialized = new AtomicBoolean(false);

    private static volatile String appName;

    private static AtomicBoolean environmentSetted = new AtomicBoolean(false);

    public static String getProperty(String key){
        String result = null;
        if (environment != null){
            result = environment.getProperty(key);
            if (result != null){
                return result;
            }
        }
        if (System.getProperties().containsKey(key)) {
            return System.getProperty(key);
        }
        return (String) properties.get(key);
    }

    public static boolean containsKey(String key){
        if (environment != null && environment.containsProperty(key)){
            return true;
        }
        return properties.containsKey(key);
    }

    public static void embededInit(){
        if (initialized.compareAndSet(false, true)){
            ClassLoader classLoader = null;
            if (Thread.currentThread().getContextClassLoader() != null) {
                classLoader = Thread.currentThread().getContextClassLoader();
            } else {
                classLoader = ClassUtils.getDefaultClassLoader();
            }
            InputStream inputStream = null;
            try {
                inputStream = classLoader!=null? classLoader.getResourceAsStream("META-INF/application.properties"): ClassLoader.getSystemResourceAsStream("META-INF/application.properties");
                if (inputStream != null){
                    properties.load(inputStream);
                    inputStream.close();
                }
            }catch (Exception e){
                throw new RuntimeException("the default properties of application.properties does not exist in fisrt level of the resource path ", e);
            }finally {
                if (inputStream != null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        //不以static实例以防止过早初始化
                        logger.error("error happen when close the inputstream of application.properties ", e);
                    }
                }
            }

            for (String key : SystemPropertyConstants.KEYS){
                if (!StringUtils.hasText(System.getProperty(key))){
                    if (properties.containsKey(key)){
                        System.setProperty(key, properties.getProperty(key));
                            logger.info("Not find key " + key + " in Java -D argument, put value " + properties.get(key) + " into System");
                    }
                }else {
                    logger.info("Find key " + key + " in Java -D argument, use system value " + System.getProperty(key));
                }
            }

        }
    }

    public static Environment getEnvironment() {
        return environment;
    }

    public static void setEnvironment(Environment environment) {
        if (environmentSetted.compareAndSet(false, true)) {
            Slite2Configuration.environment = environment;

            appName = environment.getProperty(CommonMiddlewareConstants.APP_NAME_KEY);
            if (appName == null) {
                throw new RuntimeException(
                        "Application doesn't has property which key is spring.application.name!");
            }

            if (environment != null) {
                for (String key : SystemPropertyConstants.KEYS) {
                    if (!StringUtils.hasText(System.getProperty(key))) {
                        String value = environment.getProperty(key);
                        if (value != null) {
                            System.setProperty(key, value);
                            logger.info("Not find key " + key + " in Java -D argument, put value " + environment.getProperty(key) + " into System");
                        }
                    } else {
                        logger.info("Find key " + key + " in Java -D argument, use system value " + System.getProperty(key));

                    }
                }
            }
        }
    }

    public static String getAppName() {
        return appName;
    }
}