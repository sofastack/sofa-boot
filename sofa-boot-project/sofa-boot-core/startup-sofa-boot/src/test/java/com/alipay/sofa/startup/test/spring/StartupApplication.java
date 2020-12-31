package com.alipay.sofa.startup.test.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huzijie
 * @version StartupApplication.java, v 0.1 2021年01月04日 8:41 下午 huzijie Exp $
 */
@SpringBootApplication
public class StartupApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication springApplication = new SpringApplication(StartupApplication.class);
        springApplication.run(args);
    }
}
