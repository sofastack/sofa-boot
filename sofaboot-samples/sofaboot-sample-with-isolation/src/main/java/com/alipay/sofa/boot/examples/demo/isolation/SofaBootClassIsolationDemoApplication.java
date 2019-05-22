package com.alipay.sofa.boot.examples.demo.isolation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@ImportResource({ "classpath*:spring/bean.xml" })
@SpringBootApplication
public class SofaBootClassIsolationDemoApplication {

    public static void main(String[] args) {
        //SOFABoot Isolation
        SpringApplication.run(SofaBootClassIsolationDemoApplication.class, args);
    }
}
