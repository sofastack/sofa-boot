package com.alipay.sofa.smoke.tests.ark;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author huzijie
 * @version ArkContainerTests.java, v 0.1 2023年04月06日 7:14 PM huzijie Exp $
 */
@SpringBootTest(classes = ArkSofaBootApplication.class)
public class ArkContainerTests {

    static {
        System.setProperty("sofa.ark.embed.enable", "true");
    }

    @Test
    public void check() {
        System.out.println("afa");
    }
}
