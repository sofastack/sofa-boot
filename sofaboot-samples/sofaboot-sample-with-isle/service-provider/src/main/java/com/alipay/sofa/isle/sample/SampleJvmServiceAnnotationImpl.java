package com.alipay.sofa.isle.sample;

import com.alipay.sofa.runtime.api.annotation.SofaService;

/**
 * @author xuanbei 18/5/5
 */
@SofaService(uniqueId = "annotationImpl")
public class SampleJvmServiceAnnotationImpl implements SampleJvmService {
    @Override
    public String message() {
        String message = "Hello, jvm service annotation implementation.";
        System.out.println(message);
        return message;
    }
}
