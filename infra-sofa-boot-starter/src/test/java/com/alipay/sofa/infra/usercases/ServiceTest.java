/**
 * Copyright Notice: This software is developed by Ant Small and Micro Financial Services Group Co., Ltd. This software and all the relevant information, including but not limited to any signs, images, photographs, animations, text, interface design,
 *  audios and videos, and printed materials, are protected by copyright laws and other intellectual property laws and treaties.
 *  The use of this software shall abide by the laws and regulations as well as Software Installation License Agreement/Software Use Agreement updated from time to time.
 *   Without authorization from Ant Small and Micro Financial Services Group Co., Ltd., no one may conduct the following actions:
 *
 *   1) reproduce, spread, present, set up a mirror of, upload, download this software;
 *
 *   2) reverse engineer, decompile the source code of this software or try to find the source code in any other ways;
 *
 *   3) modify, translate and adapt this software, or develop derivative products, works, and services based on this software;
 *
 *   4) distribute, lease, rent, sub-license, demise or transfer any rights in relation to this software, or authorize the reproduction of this software on other’s computers.
 */
package com.alipay.sofa.infra.usercases;

import com.alipay.sofa.infra.base.AbstractTestBase;
import com.alipay.sofa.infra.endpoint.SofaBootVersionEndpoint;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * ServiceTest
 *
 * @author yangguanchao
 * @since 2018/01/04
 */
public class ServiceTest extends AbstractTestBase {

    @Test
    public void testServiceGet() throws Exception {

        assertNotNull(urlHttpPrefix);
        String sofaBootVersionUrl = urlHttpPrefix + SofaBootVersionEndpoint.SOFA_BOOT_VERSION_PREFIX;

        //TODO 注意 RestSampleFacadeResp 一定要有默认构造函数
        ResponseEntity<List> result = testRestTemplate.getForEntity(sofaBootVersionUrl, List.class);
        assertTrue(result.getStatusCode().value() == (HttpStatus.OK.value()));
        assertTrue(result != null);
        System.err.println(result);
        //        RestSampleFacadeResp restSampleFacadeResp = response.getBody();
        //        assertTrue(restSampleFacadeResp.isSuccess());
        // TODO 注意:这里只是测试使用,使用的是 jackson 不支持泛型的反序列化,所以被反序列化为 map,这里是仅供测试使用,使用详情参考文档:http://docs.spring.io/spring-boot/docs/1.4.2.RELEASE/reference/htmlsingle/#boot-features-testing
        //        Map<String, Integer> demoUserModel = (Map<String, Integer>) restSampleFacadeResp.getData();
        //        assertTrue(demoUserModel.get("userId") >= 0);

    }
}
