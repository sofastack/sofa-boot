package com.alipay.sofa.isle.spring.share;

import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by TomorJM on 2019-10-09.
 */
public class SofaModulePostProcessorShareManager {

    private AbstractApplicationContext context;

    private List<Class> filterClassList = new ArrayList<>();

    private List<String> filterBeanNameList = new ArrayList<>();

    public SofaModulePostProcessorShareManager(AbstractApplicationContext applicationContext) {
        this.context = applicationContext;
        Map<String, SofaModulePostProcessorShareFilter> map = context.getBeansOfType(SofaModulePostProcessorShareFilter.class);
        map.forEach((k, v) -> {
            this.filterClassList.addAll(v.filterBeanFactoryPostProessorClass());
            this.filterClassList.addAll(v.filterBeanPostProessorClass());
            this.filterBeanNameList.addAll(v.filterBeanName());
        });
    }

    public boolean unableToShare(Class<?> cls) {
        return this.filterClassList.contains(cls) || cls.isAnnotationPresent(SofaModulePostProcessorShareUnable.class);
    }

    public boolean unableToShare(String beanName) {
        return this.filterBeanNameList.contains(beanName);
    }

}
