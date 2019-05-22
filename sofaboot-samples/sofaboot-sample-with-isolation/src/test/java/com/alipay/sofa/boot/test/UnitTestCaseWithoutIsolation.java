package com.alipay.sofa.boot.test;

import com.alipay.sofa.ark.support.common.DelegateArkContainer;
import com.alipay.sofa.test.annotation.DelegateToRunner;
import com.alipay.sofa.test.runner.SofaJUnit4Runner;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author qilong.zql
 * @since 2.3.0
 *
 */
@RunWith(SofaJUnit4Runner.class)
@DelegateToRunner(JUnit4.class)
public class UnitTestCaseWithoutIsolation {

    @Test
    public void test() {
        Assert.assertFalse(DelegateArkContainer.isStarted());
    }

}