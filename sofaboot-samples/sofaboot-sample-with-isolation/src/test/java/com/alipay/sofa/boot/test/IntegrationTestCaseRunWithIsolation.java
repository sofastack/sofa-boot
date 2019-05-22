package com.alipay.sofa.boot.test;

import com.alipay.sofa.boot.examples.demo.isolation.SofaBootClassIsolationDemoApplication;
import com.alipay.sofa.boot.examples.demo.service.facade.SampleService;
import com.alipay.sofa.test.runner.SofaBootRunner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author qilong.zql
 * @since 2.3.0
 */
@RunWith(SofaBootRunner.class)
@SpringBootTest(classes = SofaBootClassIsolationDemoApplication.class)
public class IntegrationTestCaseRunWithIsolation {

    @Autowired
    private SampleService sampleService;

    @Test
    public void test() {
        Assert.assertTrue("service".equals(sampleService.service()));
    }

}