package com.alipay.sofa.startup.test.beans;

import com.alipay.sofa.common.xmap.annotation.XNode;
import com.alipay.sofa.common.xmap.annotation.XObject;

/**
 * @author huzijie
 * @version ExtensionDescriptor.java, v 0.1 2021年01月05日 11:18 上午 huzijie Exp $
 */
@XObject("word")
public class ExtensionDescriptor {

    @XNode("value")
    private String value;

    public String getValue() {
        return value;
    }
}
