package com.alipay.sofa.boot.test;

import com.alipay.sofa.boot.examples.demo.isolation.SofaBootClassIsolationDemoApplication;
import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;
import com.alipay.sofa.test.annotation.DelegateToRunner;
import com.alipay.sofa.test.runner.SofaBootRunner;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@RunWith(SofaBootRunner.class)
@DelegateToRunner(SpringRunner.class)
@SpringBootTest(classes = SofaBootClassIsolationDemoApplication.class)
public class IntegrationTestCaseRunWithoutIsolation {
    @Autowired
    private SampleService sampleService;

    /**
     * Warn: Before run this test method, you should remove the dependency of sofa-ark-springboot-starter,
     * otherwise {@link RuntimeException} would be threw.
     */
    //    @Test
    //    public void test() {
    //        Assert.assertTrue("service".equals(sampleService.service()));
    //    }
}