package com.alipay.sofa.runtime.test.ambush;

import com.alipay.sofa.runtime.ambush.Context;
import com.alipay.sofa.runtime.ambush.Filter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
@EnableAutoConfiguration
@ImportResource("classpath:spring/runtime.xml")
public class JvmFilterTestConfiguration {
    public static int beforeCount = 0;
    public static int afterCount = 0;

    @Bean
    public Filter filter1() {
        return new Filter() {
            @Override
            public void before(Context context) {
                ++beforeCount;
            }

            @Override
            public void after(Context context) {
                ++afterCount;
                context.setInvokeResult("filter1");
            }

            @Override
            public int getOrder() {
                return 0;
            }
        };
    }

    @Bean
    public Filter filter2() {
        return new Filter() {
            @Override
            public void before(Context context) {
                ++beforeCount;
            }

            @Override
            public void after(Context context) {
                ++afterCount;
                context.setInvokeResult("filter2");
            }

            @Override
            public int getOrder() {
                return 100;
            }
        };
    }

    @Bean
    public Filter filter3() {
        return new Filter() {
            @Override
            public void before(Context context) {
                ++beforeCount;
            }

            @Override
            public void after(Context context) {
                ++afterCount;
                context.setInvokeResult("filter3");
            }

            @Override
            public int getOrder() {
                return -100;
            }
        };
    }
}
