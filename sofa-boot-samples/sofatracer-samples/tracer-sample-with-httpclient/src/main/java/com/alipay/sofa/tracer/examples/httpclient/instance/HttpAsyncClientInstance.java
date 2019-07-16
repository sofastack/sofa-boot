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
package com.alipay.sofa.tracer.examples.httpclient.instance;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.util.EntityUtils;

import com.alipay.common.tracer.core.utils.StringUtils;
import com.alipay.sofa.tracer.plugins.httpclient.SofaTracerHttpClientBuilder;

/**
 * HttpAsyncClientInstance
 *
 * @author yangguanchao
 * @since 2018/09/27
 */
public class HttpAsyncClientInstance {

    private static final String DEFAULT_CHARSET = "UTF-8";

    public String executeGet(String url) throws Exception {
        return asyncRequest(HttpGet.METHOD_NAME, url, null, null);
    }

    public String executeGet(String url, Map<String, String> params) throws Exception {
        return asyncRequest(HttpGet.METHOD_NAME, url, params, null);
    }

    public String executePost(String url) throws Exception {
        return asyncRequest(HttpPost.METHOD_NAME, url, null, null);
    }

    public String executePost(String url, String content) throws Exception {
        return asyncRequest(HttpPost.METHOD_NAME, url, content, null, null);
    }

    public String asyncRequest(String method, String url, Map<String, String> params,
                               Map<String, String> headers) throws Exception {
        return asyncRequest(method, url, null, params, headers);
    }

    public String asyncRequest(String method, String url, String content,
                               Map<String, String> params, Map<String, String> headers)
                                                                                       throws Exception {

        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000)
            .setConnectTimeout(6000).setConnectionRequestTimeout(6000).build();
        HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClientBuilder.create();
        //tracer
        SofaTracerHttpClientBuilder.asyncClientBuilder(httpAsyncClientBuilder);
        CloseableHttpAsyncClient asyncHttpclient = httpAsyncClientBuilder.setDefaultRequestConfig(
            requestConfig).build();
        try {
            asyncHttpclient.start();

            RequestBuilder builder = RequestBuilder.create(method.toUpperCase()).setUri(url);
            builder.setCharset(Consts.UTF_8);
            builder.setConfig(requestConfig);
            if (params != null && !params.isEmpty()) {
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    builder.addParameter(key, params.get(key));
                }
            }
            if (headers != null && !headers.isEmpty()) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    builder.addHeader(key, String.valueOf(headers.get(key)));
                }
            }
            if (StringUtils.isNotBlank(content)) {
                StringEntity body = new StringEntity(content);
                body.setContentType("application/json");
                builder.setEntity(body);
            }

            HttpUriRequest request = builder.build();
            Future<HttpResponse> future = asyncHttpclient.execute(request, null);
            HttpResponse response = future.get(6000, TimeUnit.MILLISECONDS);
            return EntityUtils.toString(response.getEntity(), DEFAULT_CHARSET);
        } finally {
            asyncHttpclient.close();
        }
    }
}
