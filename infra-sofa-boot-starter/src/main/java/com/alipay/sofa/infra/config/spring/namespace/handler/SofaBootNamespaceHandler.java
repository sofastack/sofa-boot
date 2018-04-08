package com.alipay.sofa.infra.config.spring.namespace.handler;

import com.alipay.sofa.infra.config.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.ServiceLoader;

/**
 * SofaBootNamespaceHandler
 *
 * @author yangguanchao
 * @since 2018/04/08
 */
public class SofaBootNamespaceHandler extends NamespaceHandlerSupport {

    private static final Logger logger = InfraHealthCheckLoggerFactory.getLogger(SofaBootNamespaceHandler.class);

    @Override
    public void init() {
        ServiceLoader<SofaBootTagNameSupport> serviceLoaderSofaBoot = ServiceLoader
                .load(SofaBootTagNameSupport.class);
        //SOFABoot
        for (SofaBootTagNameSupport tagNameSupport : serviceLoaderSofaBoot) {
            this.registerTagParser(tagNameSupport);
        }
    }

    private void registerTagParser(SofaBootTagNameSupport tagNameSupport) {
        if (!(tagNameSupport instanceof BeanDefinitionParser)) {
            logger.error(tagNameSupport.getClass() + " tag name supported ["
                    + tagNameSupport.supportTagName() + "] parser are not instance of "
                    + BeanDefinitionParser.class);
            return;
        }
        String tagName = tagNameSupport.supportTagName();
        registerBeanDefinitionParser(tagName, (BeanDefinitionParser) tagNameSupport);
    }
}
