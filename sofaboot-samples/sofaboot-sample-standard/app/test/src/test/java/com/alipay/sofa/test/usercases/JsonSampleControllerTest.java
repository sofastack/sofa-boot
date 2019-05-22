package com.alipay.sofa.test.usercases;

import com.alipay.sofa.test.base.AbstractTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * JsonSampleControllerTest
 * @author ruoshan
 */
public class JsonSampleControllerTest extends AbstractTestBase {

    @Test
    public void testRequestJson() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(urlHttpPrefix
                                                                              + "/json",
            String.class);
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        String responseBody = responseEntity.getBody();
        LOGGER.info(responseBody);
        Assert.assertTrue(responseBody.contains("zhangsan"));
    }
}
