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
package com.alipay.sofa.boot.annotation;

import org.springframework.core.env.Environment;

/**
 * Implementation of {@link PlaceHolderBinder} to resolve placeholders from environment.
 *
 * @author huzijie
 * @version DefaultPlaceHolderBinder.java, v 0.1 2023年01月17日 3:38 PM huzijie Exp $
 * @since 4.0.0
 */
public class DefaultPlaceHolderBinder implements PlaceHolderBinder {

    public static final DefaultPlaceHolderBinder INSTANCE = new DefaultPlaceHolderBinder();

    @Override
    public String bind(Environment environment, String string) {
        return environment.resolvePlaceholders(string);
    }
}
