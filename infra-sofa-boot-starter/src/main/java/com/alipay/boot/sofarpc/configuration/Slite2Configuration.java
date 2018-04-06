/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2017 All Rights Reserved.
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