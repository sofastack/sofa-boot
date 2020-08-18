package com.alipay.sofa.runtime.spring;

import com.alipay.sofa.runtime.ambush.Filter;
import com.alipay.sofa.runtime.ambush.FilterHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/8/18
 */
public class JvmFilterPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Filter) {
            FilterHolder.addFilter((Filter) bean);
        }
        return bean;
    }
}
