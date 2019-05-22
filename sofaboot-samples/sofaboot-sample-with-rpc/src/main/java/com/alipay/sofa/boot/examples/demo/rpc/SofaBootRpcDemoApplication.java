package com.alipay.sofa.boot.examples.demo.rpc;

import com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({ "classpath*:rpc-starter-example.xml" })
public class SofaBootRpcDemoApplication {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext applicationContext = SpringApplication.run(
            SofaBootRpcDemoApplication.class, args);

        PersonService personBolt = (PersonService) applicationContext
            .getBean("personReferenceBolt");
        PersonService personRest = (PersonService) applicationContext
            .getBean("personReferenceRest");

        System.out.println(personBolt.sayName("bolt"));
        System.out.println(personRest.sayName("rest"));

    }
}
