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
package com.alipay.sofa.rpc.boot.test.mock;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

/**
 * @author zhaowang
 * @version : HttpMockServer.java, v 0.1 2020年03月10日 9:53 下午 zhaowang Exp $
 */
public class HttpMockServer {

    static HttpServer httpServer;

    /**
     * init first
     *
     * @param port
     * @return
     */
    public static boolean initSever(int port) {

        if (httpServer != null) {

            return false;
        }

        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * add mock
     *
     * @return
     */
    public static boolean addMockPath(String path, final String responseJson) {
        httpServer.createContext(path, new HttpHandler() {
            public void handle(HttpExchange exchange) throws IOException {
                byte[] response = responseJson.getBytes();
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.length);
                exchange.getResponseBody().write(response);
                exchange.close();
            }
        });

        return true;

    }

    /**
     * start server
     *
     * @return
     */
    public static boolean start() {
        if (httpServer != null) {
            httpServer.start();
        } else {
            throw new RuntimeException("please init server first");
        }

        return true;

    }

    /**
     * stop server
     *
     * @return
     */
    public static boolean stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        } else {
            throw new RuntimeException("server has been stopped");
        }
        return true;

    }

}