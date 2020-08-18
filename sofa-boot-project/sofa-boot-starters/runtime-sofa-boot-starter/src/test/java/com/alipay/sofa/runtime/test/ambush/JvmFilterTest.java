package com.alipay.sofa.runtime.test.ambush;

import com.alipay.sofa.runtime.ambush.FilterHolder;
import com.alipay.sofa.runtime.test.RuntimeTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
@SpringBootTest(classes = JvmFilterTestConfiguration.class, properties = "spring.application.name=filterTest")
public class JvmFilterTest extends RuntimeTestBase {
    @Autowired
    private Service myService;

    @Test
    public void test() {
        Assert.assertEquals("filter2", myService.say());
        Assert.assertEquals(3, FilterHolder.getFilters().size());
        Assert.assertEquals(3, JvmFilterTestConfiguration.beforeCount);
        Assert.assertEquals(3, JvmFilterTestConfiguration.afterCount);
    }
}
