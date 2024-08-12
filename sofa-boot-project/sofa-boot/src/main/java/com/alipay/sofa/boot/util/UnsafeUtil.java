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
package com.alipay.sofa.boot.util;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * @author huazhongming
 * @date 2024/8/7 16
 * @since 4.4.0
 */
public class UnsafeUtil {
    private static Unsafe               UNSAFE;
    private static MethodHandles.Lookup IMPL_LOOKUP;

    public static Unsafe unsafe() {
        if (UNSAFE == null) {
            Unsafe unsafe = null;
            try {
                Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                theUnsafeField.setAccessible(true);
                unsafe = (Unsafe) theUnsafeField.get(null);
            } catch (Throwable ignored) {
                // ignored
            }
            UNSAFE = unsafe;
        }

        return UNSAFE;
    }

    public static MethodHandles.Lookup implLookup() {
        if (IMPL_LOOKUP == null) {
            Class<MethodHandles.Lookup> lookupClass = MethodHandles.Lookup.class;

            try {
                Field implLookupField = lookupClass.getDeclaredField("IMPL_LOOKUP");
                long offset = unsafe().staticFieldOffset(implLookupField);
                IMPL_LOOKUP = (MethodHandles.Lookup) unsafe().getObject(
                    unsafe().staticFieldBase(implLookupField), offset);
            } catch (Throwable e) {
                // ignored
            }
        }
        return IMPL_LOOKUP;
    }
}
