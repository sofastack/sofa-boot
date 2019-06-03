/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.rpc.boot.runtime.parser;

import com.alipay.sofa.infra.config.spring.namespace.spi.SofaBootTagNameSupport;
import com.alipay.sofa.rpc.boot.container.RpcFilterContainer;
import com.alipay.sofa.rpc.boot.log.SofaBootRpcLoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 解析全局 Filter
 *
 * @author <a href="mailto:lw111072@antfin.com">LiWei</a>
 */
public class GlobalFilterParser extends AbstractSimpleBeanDefinitionParser implements SofaBootTagNameSupport {

    private static final Logger LOGGER            = SofaBootRpcLoggerFactory.getLogger(GlobalFilterParser.class);
    private static final String TAG_GLOBAL_FILTER = "rpc-global-filter";
    private static final String TAG_REF           = "ref";
    private static final String TAG_CLASS         = "class";

    /**
     * 从 XML 解析全局 Filter。
     *
     * @param element
     * @param parserContext
     * @param builder
     */
    @Override
    protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {

        String filterId = element.getAttribute(TAG_REF);
        String filterClass = element.getAttribute(TAG_CLASS);

        if (StringUtils.hasText(filterId)) {
            RpcFilterContainer.getInstance().addFilterId(filterId);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("global filter take effect[" + filterId + "]");
            }
            return;
        }
        if (StringUtils.hasText(filterClass)) {
            RpcFilterContainer.getInstance().addFilterClass(filterClass);
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("global filter take effect[" + filterClass + "]");
            }
            return;
        }

        if (LOGGER.isWarnEnabled()) {
            LOGGER.warn("both the ref attr and class attr is blank, this rpc global filter is invalid");
        }

    }

    /**
     * 支持的 tag 名字
     *
     * @return 支持的 tag 名字
     */
    @Override
    public String supportTagName() {
        return TAG_GLOBAL_FILTER;
    }

    @Override
    protected boolean shouldGenerateIdAsFallback() {
        return true;
    }

    @Override
    protected Class<?> getBeanClass(Element element) {
        return Object.class;
    }

}