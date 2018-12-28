/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.alipay.sofa.healthcheck.management;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author ruoshan
 * @version $Id: SleepBean.java, v 0.1 2018年12月27日 10:12 PM ruoshan Exp $
 */
@Component
public class SleepBeanListener implements ApplicationListener<ContextRefreshedEvent>, PriorityOrdered {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Value("${management.port}")
    private String managementPort;

    @Autowired
    private ApplicationContext applicationContext;

    private AtomicInteger npeTimes = new AtomicInteger();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        // 只监听 root 上下文的事件
        if (!applicationContext.equals(contextRefreshedEvent.getApplicationContext())){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                ResponseEntity<String> response = testRestTemplate.getForEntity("http://localhost:" + managementPort +
                        "/health/readiness", String.class);
                System.out.println(response.getStatusCode());
            }
        }).start();
        try{
            Thread.sleep(3000);
        }catch (Throwable e){
            //ignore
        }
    }

    public AtomicInteger getNpeTimes() {
        return npeTimes;
    }

    @Override
    public int getOrder() {
        return PriorityOrdered.HIGHEST_PRECEDENCE;
    }
}