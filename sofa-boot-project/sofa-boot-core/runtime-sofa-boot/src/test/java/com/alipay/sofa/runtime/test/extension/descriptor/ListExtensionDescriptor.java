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
package com.alipay.sofa.runtime.test.extension.descriptor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.alipay.sofa.common.xmap.annotation.XNodeList;
import com.alipay.sofa.common.xmap.annotation.XObject;

/**
 * @author ruoshan
 * @since 2.6.0
 */
@XObject("testList")
public class ListExtensionDescriptor {

    @XNodeList(value = "value", componentType = String.class, type = ArrayList.class)
    private List<String> values;

    @XNodeList(value = "attribute/value[@id='listTest']", componentType = String.class, type = String[].class)
    private String[]     attributeValues;

    @XNodeList(value = "value", componentType = String.class, type = LinkedList.class)
    private List<String> LinkedListValues;

    @XNodeList(value = "intValue", componentType = int.class, type = int[].class)
    int[]                intValues;

    @XNodeList(value = "longValue", componentType = long.class, type = long[].class)
    long[]               longValues;

    @XNodeList(value = "floatValue", componentType = float.class, type = float[].class)
    float[]              floatValues;

    @XNodeList(value = "doubleValue", componentType = double.class, type = double[].class)
    double[]             doubleValues;

    @XNodeList(value = "booleanValue", componentType = boolean.class, type = boolean[].class)
    boolean[]            booleanValues;

    @XNodeList(value = "charValue", componentType = char.class, type = char[].class)
    char[]               charValues;

    @XNodeList(value = "shortValue", componentType = short.class, type = short[].class)
    short[]              shortValues;

    @XNodeList(value = "byteValue", componentType = byte.class, type = byte[].class)
    byte[]               byteValues;

    public List<String> getValues() {
        return values;
    }

    public String[] getAttributeValues() {
        return attributeValues;
    }

    public int[] getIntValues() {
        return intValues;
    }

    public long[] getLongValues() {
        return longValues;
    }

    public float[] getFloatValues() {
        return floatValues;
    }

    public double[] getDoubleValues() {
        return doubleValues;
    }

    public boolean[] getBooleanValues() {
        return booleanValues;
    }

    public char[] getCharValues() {
        return charValues;
    }

    public short[] getShortValues() {
        return shortValues;
    }

    public byte[] getByteValues() {
        return byteValues;
    }
}
