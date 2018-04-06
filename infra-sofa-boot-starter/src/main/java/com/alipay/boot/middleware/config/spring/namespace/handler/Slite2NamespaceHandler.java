/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 * audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 * The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 * Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 * <p>
 * 1) reproduce, spread, present, set up a mirror of, upload, download this software;
 * <p>
 * 2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 * <p>
 * 3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 * <p>
 * 4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.boot.middleware.config.spring.namespace.handler;

import com.alipay.boot.middleware.config.spring.namespace.spi.Slite2MiddlewareTagNameSupport;
import com.alipay.sofa.infra.config.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.infra.log.InfraHealthCheckLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import java.util.ServiceLoader;

/**
 * SOFA Boot 命名空间解析
 * <p>
 * Created by yangguanchao on 16/9/1.
 */
public class Slite2NamespaceHandler extends NamespaceHandlerSupport {

    private static final Logger logger = InfraHealthCheckLoggerFactory.getLogger(Slite2NamespaceHandler.class);

    @Override
    public void init() {
        ServiceLoader<Slite2MiddlewareTagNameSupport> serviceLoader = ServiceLoader.load(Slite2MiddlewareTagNameSupport.class);
        ServiceLoader<SofaBootTagNameSupport> serviceLoaderSofaBoot = ServiceLoader.load(SofaBootTagNameSupport.class);

        //compatible
        for (Slite2MiddlewareTagNameSupport tagNameSupport : serviceLoader) {
            this.registerTagParser(tagNameSupport);
        }
        //SOFA Boot
        for (Slite2MiddlewareTagNameSupport tagNameSupport : serviceLoaderSofaBoot) {
            this.registerTagParser(tagNameSupport);
        }
    }

    private void registerTagParser(Slite2MiddlewareTagNameSupport tagNameSupport) {
        if (!(tagNameSupport instanceof BeanDefinitionParser)) {
            logger.error(tagNameSupport.getClass() + " tag name supported [" + tagNameSupport.supportTagName() + "] parser are not instance of " + BeanDefinitionParser.class);
            return;
        }
        String tagName = tagNameSupport.supportTagName();
        registerBeanDefinitionParser(tagName, (BeanDefinitionParser) tagNameSupport);
    }
}
