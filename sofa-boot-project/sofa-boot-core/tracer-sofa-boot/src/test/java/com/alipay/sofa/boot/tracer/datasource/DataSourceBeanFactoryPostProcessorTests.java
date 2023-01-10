package com.alipay.sofa.boot.tracer.datasource;


import com.alibaba.druid.pool.DruidDataSource;
import com.alipay.sofa.tracer.plugins.datasource.SmartDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.dbcp.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link DataSourceBeanFactoryPostProcessor}.
 * 
 * @author huzijie
 * @version DataSourceBeanFactoryPostProcessorTests.java, v 0.1 2023年01月09日 6:02 PM huzijie Exp $
 */
@ExtendWith(MockitoExtension.class)
public class DataSourceBeanFactoryPostProcessorTests {

    @Test
    public void wrapBeanDefinitions() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            DataSourceBeanFactoryPostProcessor dataSourceBeanFactoryPostProcessor = new DataSourceBeanFactoryPostProcessor();
            dataSourceBeanFactoryPostProcessor.setAppName("testApp");
            context.addBeanFactoryPostProcessor(dataSourceBeanFactoryPostProcessor);
            context.register(DataSourceConfiguration.class);
            context.refresh();
            assertThat(context.getBean("s_t_d_s_DataSource", DataSource.class)).isNotInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("emptyDataSource", DataSource.class)).isNotInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("druidDataSource", DataSource.class)).isInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("dbcpDataSource", DataSource.class)).isInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("tomcatDataSource", DataSource.class)).isInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("c3p0DataSource", DataSource.class)).isInstanceOf(SmartDataSource.class);
            assertThat(context.getBean("hikariDataSource", DataSource.class)).isInstanceOf(SmartDataSource.class);
        }
    }
    
    @Configuration
    static class DataSourceConfiguration {

        private final String testUrl   = "jdbc:oracle:thin:@localhost:1521:orcl";
        
        @Bean
        public DataSource s_t_d_s_DataSource() {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(testUrl);
            return dataSource;
        }

        @Bean
        public DataSource emptyDataSource() {
            return new EmptyDataSource();
        }

        @Bean
        public DataSource druidDataSource() {
            DruidDataSource dataSource = new DruidDataSource();
            dataSource.setUrl(testUrl);
            return dataSource;
        }

        @Bean
        public DataSource dbcpDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setUrl(testUrl);
            return dataSource;
        }

        @Bean
        public DataSource tomcatDataSource() {
            org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
            dataSource.setUrl(testUrl);
            return dataSource;
        }

        @Bean
        public DataSource c3p0DataSource() {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(testUrl);
            return dataSource;
        }

        @Bean
        public DataSource hikariDataSource() {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(testUrl);
            return dataSource;
        }
    }
}
