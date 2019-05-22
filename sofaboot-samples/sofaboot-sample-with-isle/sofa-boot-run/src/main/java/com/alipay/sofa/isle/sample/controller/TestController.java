package com.alipay.sofa.isle.sample.controller;

import com.alipay.sofa.isle.sample.SampleJvmService;
import com.alipay.sofa.runtime.api.annotation.SofaReference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author xuanbei
 * @since 2.4.5
 */
@RestController
public class TestController {
    @SofaReference
    private SampleJvmService sampleJvmService;

    @SofaReference(uniqueId = "annotationImpl")
    private SampleJvmService sampleJvmServiceAnnotationImpl;

    @SofaReference(uniqueId = "serviceClientImpl")
    private SampleJvmService sampleJvmServiceClientImpl;

    @RequestMapping("/serviceWithoutUniqueId")
    public String serviceWithoutUniqueId() throws IOException {
        return sampleJvmService.message();
    }

    @RequestMapping("/annotationImplService")
    public String annotationImplService() throws IOException {
        return sampleJvmServiceAnnotationImpl.message();
    }

    @RequestMapping("/serviceClientImplService")
    public String serviceClientImplService() throws IOException {
        return sampleJvmServiceClientImpl.message();
    }
}
