package com.alipay.sofa.smoke.tests.tracer.sample;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author huzijie
 * @version RestTemplateConfiguration.java, v 0.1 2023年02月24日 5:58 PM huzijie Exp $
 */
@Configuration
public class RestTemplateConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
