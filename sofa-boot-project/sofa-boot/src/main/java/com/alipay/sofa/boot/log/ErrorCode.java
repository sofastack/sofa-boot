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
package com.alipay.sofa.boot.log;

import com.alipay.sofa.boot.constant.SofaBootConstants;
import com.alipay.sofa.common.code.LogCode2Description;

/**
 * Use {@link LogCode2Description} to transform error code.
 *
 * @author huzijie
 * @version ErrorCode.java, v 0.1 2021年08月30日 11:29 上午 huzijie Exp $
 */
public class ErrorCode {

    private static final LogCode2Description LCD = LogCode2Description
                                                     .create(SofaBootConstants.SOFA_BOOT_SPACE_NAME);

    public static String convert(String code) {
        return LCD.convert(code);
    }

    public static String convert(String code, Object... args) {
        return LCD.convert(code, args);
    }

}
