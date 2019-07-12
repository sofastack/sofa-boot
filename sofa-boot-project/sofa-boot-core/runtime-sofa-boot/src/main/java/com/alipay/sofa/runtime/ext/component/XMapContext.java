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
package com.alipay.sofa.runtime.ext.component;

import java.net.URL;

import com.alipay.sofa.common.xmap.Context;

/**
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 *
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XMapContext extends Context {

    private static final long serialVersionUID = -7194560385886298218L;

    private ClassLoader       appClassLoader;

    /**
     *
     * @param appClassLoader
     */
    public XMapContext(ClassLoader appClassLoader) {
        this.appClassLoader = appClassLoader;
    }

    /**
     *
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        return appClassLoader.loadClass(className);
    }

    /**
     *
     * @param name
     * @return
     */
    @Override
    public URL getResource(String name) {
        return appClassLoader.getResource(name);
    }

}
