package com.alipay.sofa.isle.sample.test.processor;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.boot.util.BeanDefinitionUtil;
import com.alipay.sofa.isle.sample.ApplicationRun;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareFilter;
import com.alipay.sofa.isle.spring.share.SofaModulePostProcessorShareUnable;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by TomorJM on 2019-10-09.
 */
@SpringBootTest(classes = {ApplicationRun.class})
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PostProcessorShareTest.ProcessorConfig.class)
public class PostProcessorShareTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void test() {
        Map<String, BeanDefinition> processors = (Map<String, BeanDefinition>) context.getBean(SofaBootConstants.PROCESSORS_OF_ROOT_APPLICATION_CONTEXT);
        processors.forEach((k, v) -> {
            Class cls = BeanDefinitionUtil.resolveBeanClassType(v);
            Assert.assertTrue(!ProcessorConfig.TestA.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestB.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestC.class.equals(cls));
            Assert.assertTrue(!ProcessorConfig.TestD.class.equals(cls));
        });
        Assert.assertTrue(processors.keySet().contains("testE"));
    }

    @Configuration
    static class ProcessorConfig {

        @Bean
        @ConditionalOnMissingBean
        public TestFilter testFilter() {
            return new TestFilter();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestA testA() {
            return new TestA();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestB testB() {
            return new TestB();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestC testC() {
            return new TestC();
        }

        @Bean(value = "testD")
        @ConditionalOnMissingBean
        public TestD testD() {
            return new TestD();
        }

        @Bean
        @ConditionalOnMissingBean
        public TestE testE() {
            return new TestE();
        }

        public class TestFilter implements SofaModulePostProcessorShareFilter {

            @Override
            public List<Class<? extends BeanPostProcessor>> filterBeanPostProessorClass() {
                return Arrays.asList(ProcessorConfig.TestA.class);
            }

            @Override
            public List<Class<? extends BeanFactoryPostProcessor>> filterBeanFactoryPostProessorClass() {
                return Arrays.asList(TestB.class);
            }

            @Override
            public List<String> filterBeanName() {
                return Arrays.asList("testD");
            }
        }

        public class TestA implements BeanPostProcessor {
        }

        public class TestB implements BeanFactoryPostProcessor {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                int i = 0;
            }
        }

        @SofaModulePostProcessorShareUnable
        public class TestC {

        }

        static class TestD implements BeanPostProcessor {

        }

        public class TestE implements BeanPostProcessor {

        }
    }


}
