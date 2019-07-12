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

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 *
 * @author <a href="mailto:bs@nuxeo.com">Bogdan Stefanescu</a>
 * @author xi.hux@alipay.com
 * @since 2.6.0
 */
public class XAnnotatedParent extends XAnnotatedMember {

    protected XAnnotatedParent(XMap xmap, XSetter setter, XGetter getter) {
        super(xmap, setter, getter);
    }

    @Override
    protected Object getValue(Context ctx, Element base) throws Exception {
        return ctx.getParent();
    }

    @Override
    public void decode(Object instance, Node base, Document document, List<String> filters)
                                                                                           throws Exception {
        // do nothing
    }

}
