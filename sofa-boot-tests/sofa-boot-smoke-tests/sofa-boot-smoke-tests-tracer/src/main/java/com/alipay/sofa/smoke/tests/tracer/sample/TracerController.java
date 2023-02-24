package com.alipay.sofa.smoke.tests.tracer.sample;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huzijie
 * @version TracerController.java, v 0.1 2023年02月24日 5:55 PM huzijie Exp $
 */
@RestController
public class TracerController {

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }
}
