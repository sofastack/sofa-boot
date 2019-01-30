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
package com.alipay.sofa.common.xmap;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public final class PrimitiveArrays {

    private PrimitiveArrays() {
    }

    @SuppressWarnings({ "ObjectEquality" })
    public static Object toPrimitiveArray(Collection<Object> col, Class primitiveArrayType) {
        if (primitiveArrayType == Integer.TYPE) {
            return toIntArray(col);
        } else if (primitiveArrayType == Long.TYPE) {
            return toLongArray(col);
        } else if (primitiveArrayType == Double.TYPE) {
            return toDoubleArray(col);
        } else if (primitiveArrayType == Float.TYPE) {
            return toFloatArray(col);
        } else if (primitiveArrayType == Boolean.TYPE) {
            return toBooleanArray(col);
        } else if (primitiveArrayType == Byte.TYPE) {
            return toByteArray(col);
        } else if (primitiveArrayType == Character.TYPE) {
            return toCharArray(col);
        } else if (primitiveArrayType == Short.TYPE) {
            return toShortArray(col);
        }
        return null;
    }

    public static int[] toIntArray(Collection col) {
        int size = col.size();
        int[] ar = new int[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Integer) it.next();
        }
        return ar;
    }

    public static long[] toLongArray(Collection col) {
        int size = col.size();
        long[] ar = new long[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Long) it.next();
        }
        return ar;
    }

    public static double[] toDoubleArray(Collection col) {
        int size = col.size();
        double[] ar = new double[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Double) it.next();
        }
        return ar;
    }

    public static float[] toFloatArray(Collection col) {
        int size = col.size();
        float[] ar = new float[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Float) it.next();
        }
        return ar;
    }

    public static boolean[] toBooleanArray(Collection col) {
        int size = col.size();
        boolean[] ar = new boolean[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Boolean) it.next();
        }
        return ar;
    }

    public static short[] toShortArray(Collection col) {
        int size = col.size();
        short[] ar = new short[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Short) it.next();
        }
        return ar;
    }

    public static byte[] toByteArray(Collection col) {
        int size = col.size();
        byte[] ar = new byte[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Byte) it.next();
        }
        return ar;
    }

    public static char[] toCharArray(Collection col) {
        int size = col.size();
        char[] ar = new char[size];
        Iterator it = col.iterator();
        int i = 0;
        while (it.hasNext()) {
            ar[i++] = (Character) it.next();
        }
        return ar;
    }

}
