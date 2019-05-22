package com.alipay.sofa;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

/**
 * SOFABootWebApplication
 * <p>
 * <p>
 * Created by yangguanchao on 16/12/9.
 */
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SOFABootWebApplication {

    // init the logger
    private static final Logger LOGGER = LoggerFactory.getLogger(SOFABootWebApplication.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SOFABootWebApplication.class, args);
    }
}
