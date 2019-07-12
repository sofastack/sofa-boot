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
package com.alipay.sofa.tracer.boot.zipkin;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import com.alipay.common.tracer.core.context.span.SofaTracerSpanContext;
import com.alipay.common.tracer.core.context.trace.SofaTraceContext;
import com.alipay.common.tracer.core.generator.TraceIdGenerator;
import com.alipay.common.tracer.core.holder.SofaTraceContextHolder;
import com.alipay.common.tracer.core.listener.SpanReportListener;
import com.alipay.common.tracer.core.listener.SpanReportListenerHolder;
import com.alipay.common.tracer.core.span.SofaTracerSpan;
import com.alipay.sofa.tracer.boot.zipkin.mock.MockAbstractTracer;
import com.alipay.sofa.tracer.boot.zipkin.properties.ZipkinSofaTracerProperties;
import com.alipay.sofa.tracer.plugins.zipkin.ZipkinSofaTracerRestTemplateCustomizer;
import com.alipay.sofa.tracer.plugins.zipkin.ZipkinSofaTracerSpanRemoteReporter;
import com.alipay.sofa.tracer.plugins.zipkin.adapter.ZipkinV2SpanAdapter;

/**
 * ZipkinSofaTracerSpanRemoteReporter Tester.
 *
 * @author guolei.sgl
 * @since v2.3.0
 */
public class ZipkinSofaTracerSpanRemoteReporterTest {

    private MockAbstractTracer  remoteTracer;
    private ZipkinV2SpanAdapter zipkinV2SpanAdapter;

    @Before
    public void before() throws Exception {
        zipkinV2SpanAdapter = new ZipkinV2SpanAdapter();
        remoteTracer = new MockAbstractTracer("mockSendTracerSpan");
        RestTemplate restTemplate = new RestTemplate();
        ZipkinSofaTracerProperties zipkinProperties = new ZipkinSofaTracerProperties();
        zipkinProperties.setBaseUrl("http://localhost:9411");
        zipkinProperties.setEnabled(true);
        zipkinProperties.setGzipped(true);
        ZipkinSofaTracerRestTemplateCustomizer restTemplateCustomizer = new ZipkinSofaTracerRestTemplateCustomizer(
            zipkinProperties.isGzipped());
        restTemplateCustomizer.customize(restTemplate);
        //host http://zipkin-cloud-3.inc.host.net:9411
        String baseUrl = "http://zipkin-cloud-3.inc.host.net:9411";
        SpanReportListener spanReportListener = new ZipkinSofaTracerSpanRemoteReporter(
            restTemplate, baseUrl);
        SpanReportListenerHolder.addSpanReportListener(spanReportListener);
    }

    @After
    public void after() throws Exception {
        SpanReportListenerHolder.clear();
    }

    @Test
    public void testHexStringToHex() throws Exception {
        long time = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            String traceId = TraceIdGenerator.generate();
            long idValue = ZipkinSofaTracerSpanRemoteReporter.traceIdToId(traceId);
            //有了末位的累加数
            assertTrue(idValue > time);
        }
        //8 位以内
        String hex1 = "0a";
        assertEquals(10, ZipkinSofaTracerSpanRemoteReporter.traceIdToId(hex1));
        String hex2 = "0e";
        assertEquals(14, ZipkinSofaTracerSpanRemoteReporter.traceIdToId(hex2));
        String hex3 = "0a0a";
        assertEquals(10 + 10 * (16 * 16), ZipkinSofaTracerSpanRemoteReporter.traceIdToId(hex3));
        //大于 8 位且非进程号结尾
        String hex4 = "0a0c0d0e100";
        assertEquals(100, ZipkinSofaTracerSpanRemoteReporter.traceIdToId(hex4));
        String hex5 = "0a0c0d0e9999";
        assertEquals(9999, ZipkinSofaTracerSpanRemoteReporter.traceIdToId(hex5));
        //exception
        boolean isException = false;
        try {
            ZipkinSofaTracerSpanRemoteReporter.traceIdToId("");
        } catch (IllegalArgumentException ex) {
            isException = true;
        }
        assertTrue(isException);
    }

    /***
     * 测试 FNV64HashCode 算法性能,如果不满足性能要求,那么考虑更优秀算法
     * 目标: 200 万数据务必在 10s 内完成且没有碰撞发生
     * hash FNVHash64 : http://www.isthe.com/chongo/tech/comp/fnv/index.html#FNV-param
     * @throws Exception 异常
     */
    @Test
    public void testFNV64HashCode() throws Exception {
        //one million no redundant
        long startTime = System.currentTimeMillis();
        //100
        int iLong = 20;
        int jLong = 50;
        int kLong = 50;
        int lLong = 50;
        Map<Long, Long> mapHash = new HashMap<Long, Long>();
        for (int i = 0; i < iLong; i++) {
            String spanId1 = "" + i;
            //hash
            entranceHash(mapHash, spanId1);
            //100000
            for (int j = 0; j < jLong; j++) {
                String spanId2 = i + "." + j;
                //hash
                entranceHash(mapHash, spanId2);
                for (int k = 0; k < kLong; k++) {
                    String spanId3 = i + "." + j + "." + k;
                    //hash
                    entranceHash(mapHash, spanId3);
                    for (int l = 0; l < lLong; l++) {
                        String spanId4 = i + "." + j + "." + k + "." + l;
                        //hash
                        entranceHash(mapHash, spanId4);
                    }
                }
            }
        }
        long cost = System.currentTimeMillis() - startTime;
        long count = (iLong * jLong * kLong * lLong) + (iLong * jLong * kLong) + (iLong * jLong)
                     + iLong;
        //目标: 200 万数据务必在 10s 内完成
        assertTrue("Count = " + count + ",FNV64HashCode Cost = " + cost + " ms", cost < 10 * 1000);
        //重复
        Map<Long, Long> redundantMap = getRedundant(mapHash);
        assertTrue("FNV64HashCode Redundant Size = " + redundantMap.size() + " ; Redundant = "
                   + redundantMap, redundantMap.size() <= 0);
    }

    private void entranceHash(Map<Long, Long> map, String data) {
        long hashCode = zipkinV2SpanAdapter.FNV64HashCode(data);
        this.putMap(hashCode, map);
    }

    private void putMap(long hashCode, Map<Long, Long> map) {
        if (map.containsKey(hashCode)) {
            long count = map.get(hashCode);
            map.put(hashCode, ++count);
        } else {
            map.put(hashCode, 1L);
        }
    }

    private Map<Long, Long> getRedundant(Map<Long, Long> originMap) {
        Map<Long, Long> resultMap = new HashMap<Long, Long>();
        for (Map.Entry<Long, Long> entry : originMap.entrySet()) {
            Long value = entry.getValue();
            if (value > 1) {
                Long key = entry.getKey();
                resultMap.put(key, value);
            }
        }
        return resultMap;
    }

    @Test
    public void testDoServerReport() throws Exception {
        //sr TL
        SofaTracerSpan sofaTracerServerSpan = this.remoteTracer.serverReceive();
        sofaTracerServerSpan.setOperationName("mockOperationName");
        //ss TL
        this.remoteTracer.serverSend("0");
        //异步汇报,所以 sleep 1s
        Thread.sleep(1000);
        //assert
        SofaTraceContext sofaTraceContext = SofaTraceContextHolder.getSofaTraceContext();
        assertTrue(sofaTraceContext.isEmpty());
    }

    @Test
    public void testDoClientReport() throws Exception {
        SofaTraceContext sofaTraceContext = SofaTraceContextHolder.getSofaTraceContext();
        sofaTraceContext.clear();
        //cs
        SofaTracerSpan clientSpan = this.remoteTracer.clientSend("testDoClientReport");
        assertTrue(clientSpan != null);
        //cr
        this.remoteTracer.clientReceive("0");

        //assert
        assertTrue(sofaTraceContext.isEmpty());
    }

    /**
     * Method: doReport(SofaTracerSpan span)
     */
    @Test
    public void testDoReport() throws Exception {
        SofaTraceContext sofaTraceContext = SofaTraceContextHolder.getSofaTraceContext();
        //sr TL
        SofaTracerSpan sofaTracerServerSpan = this.remoteTracer.serverReceive();
        sofaTracerServerSpan.setOperationName("ServerReveive0");
        //assert
        assertEquals(sofaTracerServerSpan, sofaTraceContext.getCurrentSpan());
        //cs
        SofaTracerSpan clientSpan = this.remoteTracer.clientSend("ClientSend0");
        //assert
        assertEquals(clientSpan, sofaTraceContext.getCurrentSpan());
        //mock sync remote call
        this.mockRemoteCall();
        //cr
        this.remoteTracer.clientReceive("0");
        assertEquals(sofaTracerServerSpan, sofaTraceContext.getCurrentSpan());
        //ss TL
        this.remoteTracer.serverSend("0");
        //异步汇报,所以 sleep 1s
        Thread.sleep(1000);
        //assert
        assertTrue(sofaTraceContext.isEmpty());
    }

    public void mockRemoteCall() throws Exception {
        SofaTraceContext sofaTraceContext = SofaTraceContextHolder.getSofaTraceContext();
        SofaTracerSpan originSofaTracerSpan = sofaTraceContext.getCurrentSpan();
        SofaTracerSpanContext spanContext = originSofaTracerSpan.getSofaTracerSpanContext();
        sofaTraceContext.clear();
        //sr
        SofaTracerSpan srSpan = this.remoteTracer.serverReceive(spanContext);
        srSpan.setOperationName("ServerReceive1");
        assertEquals(srSpan, sofaTraceContext.getCurrentSpan());
        //ss
        this.remoteTracer.serverSend("0");
        assertTrue(sofaTraceContext.getThreadLocalSpanSize() == 0);
        //mock restore
        sofaTraceContext.push(originSofaTracerSpan);
    }
}
