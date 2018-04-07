package com.alipay.sofa.boot.examples.demo.isolation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SofaBootClassIsolationDemoApplication {

	public static void main(String[] args) {
		//SOFA Boot Isolation
		SpringApplication.run(SofaBootClassIsolationDemoApplication.class, args);
	}
}
