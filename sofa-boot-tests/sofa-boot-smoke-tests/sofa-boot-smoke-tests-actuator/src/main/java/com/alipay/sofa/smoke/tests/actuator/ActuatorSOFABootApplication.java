package com.alipay.sofa.smoke.tests.actuator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huzijie
 * @version ActuatorSOFABootApplication.java, v 0.1 2023年01月04日 11:12 AM huzijie Exp $
 */
@SpringBootApplication
public class ActuatorSOFABootApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication();
        application.run(args);
    }
}
