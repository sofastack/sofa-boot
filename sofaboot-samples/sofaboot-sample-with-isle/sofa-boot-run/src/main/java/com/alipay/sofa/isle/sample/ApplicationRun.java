package com.alipay.sofa.isle.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author xuanbei 18/5/5
 */
@SpringBootApplication
public class ApplicationRun {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(ApplicationRun.class);
        springApplication.run(args);
    }
}
