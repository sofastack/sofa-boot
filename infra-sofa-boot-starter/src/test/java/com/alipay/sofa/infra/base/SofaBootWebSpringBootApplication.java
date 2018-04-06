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
package com.alipay.sofa.infra.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

/**
 * SofaBootWebSpringBootApplication
 * <p/>
 * Created by yangguanchao on 16/7/22.
 */
@ImportResource({ "classpath*:META-INF/sofaboot-web-test/*.xml" })
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SofaBootWebSpringBootApplication {

    // 在Java类中创建 logger 实例
    private static final Logger logger = LoggerFactory.getLogger(SofaBootWebSpringBootApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication = new SpringApplication(SofaBootWebSpringBootApplication.class);
        ApplicationContext applicationContext = springApplication.run(args);
    }
}
