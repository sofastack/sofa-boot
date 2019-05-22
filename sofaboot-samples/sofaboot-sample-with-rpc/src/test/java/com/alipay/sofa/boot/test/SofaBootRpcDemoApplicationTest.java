package com.alipay.sofa.boot.test;

import com.alipay.sofa.boot.examples.demo.rpc.SofaBootRpcDemoApplication;
import com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author liangen
 * @version $Id: SofaBootRpcDemoApplicationTest.java, v 0.1 2018年04月10日 上午10:39 liangen Exp $
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SofaBootRpcDemoApplication.class)
public class SofaBootRpcDemoApplicationTest {

    @Autowired
    private PersonService personReferenceBolt;

    @Autowired
    private PersonService personReferenceRest;

    @Test
    public void test() {
        Assert.assertEquals("hi Bolt!", personReferenceBolt.sayName("Bolt"));
        Assert.assertEquals("hi Rest!", personReferenceRest.sayName("Rest"));
    }
}