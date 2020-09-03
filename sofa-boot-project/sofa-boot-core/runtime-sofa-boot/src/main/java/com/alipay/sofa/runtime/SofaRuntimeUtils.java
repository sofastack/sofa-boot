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
package com.alipay.sofa.runtime;

import com.alipay.sofa.ark.api.ArkClient;
import com.alipay.sofa.ark.api.ArkConfigs;
import com.alipay.sofa.ark.spi.constant.Constants;
import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.runtime.log.SofaLogger;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author <a href="mailto:guaner.zzx@alipay.com">Alaneuler</a>
 * Created on 2020/9/2
 */
public class SofaRuntimeUtils {
    private static final String ARK_BIZ_CLASSLOADER_NAME = "com.alipay.sofa.ark.container.service.classloader.BizClassLoader";
    private static Object       masterBiz;

    public static boolean isArkEnvironment() {
        ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        return tccl != null && ARK_BIZ_CLASSLOADER_NAME.equals(tccl.getClass().getName());
    }

    /**
     * Invoke this method only in Ark environment!
     * @return true if on master biz in Ark environment, otherwise false
     */
    public static boolean onMasterBiz() {
        try {
            if (masterBiz == null) {
                String masterBizName = ArkConfigs.getStringValue(Constants.MASTER_BIZ);
                List<Biz> biz = ArkClient.getBizManagerService().getBiz(masterBizName);
                Assert.isTrue(biz.size() == 1, "master biz should have and only have one.");
                masterBiz = biz.get(0);
            }

            ClassLoader tccl = Thread.currentThread().getContextClassLoader();
            return tccl.equals(((Biz) masterBiz).getBizClassLoader());
        } catch (Throwable e) {
            // For catching ClassNotFound exception
            SofaLogger
                .info(
                    "Assume normal SOFABoot environment because Loading of master biz fails with error:",
                    e);
            throw e;
        }
    }
}
