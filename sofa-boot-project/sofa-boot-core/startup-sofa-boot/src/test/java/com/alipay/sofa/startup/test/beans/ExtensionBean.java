package com.alipay.sofa.startup.test.beans;

import com.alipay.sofa.service.api.component.Extension;
import com.alipay.sofa.startup.test.beans.facade.TestService;

/**
 * @author huzijie
 * @version ExtensionBean.java, v 0.1 2021年01月05日 11:17 上午 huzijie Exp $
 */
public class ExtensionBean implements TestService {
    private String word;

    @Override
    public void test() {
        System.out.println(word);
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void registerExtension(Extension extension) throws Exception {
        Object[] contributions = extension.getContributions();
        String extensionPoint = extension.getExtensionPoint();

        if (contributions == null) {
            return;
        }

        for (Object contribution : contributions) {
            if ("word".equals(extensionPoint)) {
                setWord(((ExtensionDescriptor) contribution).getValue());
            }
        }
    }
}
