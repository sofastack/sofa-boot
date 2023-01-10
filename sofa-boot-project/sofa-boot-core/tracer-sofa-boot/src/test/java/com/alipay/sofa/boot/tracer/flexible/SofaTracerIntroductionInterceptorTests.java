package com.alipay.sofa.boot.tracer.flexible;

import com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
* Tests for {@link SofaTracerIntroductionInterceptor}.
*
* @author huzijie
* @version SofaTracerIntroductionInterceptorTests.java, v 0.1 2023年01月10日 8:58 PM huzijie Exp $
*/
@ExtendWith(MockitoExtension.class)
public class SofaTracerIntroductionInterceptorTests {

    @InjectMocks
    private SofaTracerIntroductionInterceptor sofaTracerIntroductionInterceptor;

    @Mock
    private MethodInvocationProcessor sofaTracerIntroductionProcessor;

    @Mock
    private MethodInvocation methodInvocation;

    private final Method methodA = ReflectionUtils.findMethod(A.class ,"hello");

    private final Method methodB = ReflectionUtils.findMethod(B.class ,"hello");

    @Test
    public void invokeWithAnnotation() throws Throwable {
        Mockito.when(methodInvocation.getMethod()).thenReturn(methodA);
        Mockito.when(methodInvocation.getThis()).thenReturn(new A());
        Mockito.when(sofaTracerIntroductionProcessor.process(methodInvocation, getTracerAnnotation())).thenReturn("Hello");
        Object result = sofaTracerIntroductionInterceptor.invoke(methodInvocation);
        assertThat(result).isEqualTo("Hello");
    }

     @Test
     public void invokeWithNoAnnotation() throws Throwable {
         Mockito.when(methodInvocation.getMethod()).thenReturn(methodB);
         Mockito.when(methodInvocation.getThis()).thenReturn(new B());
         Mockito.when(methodInvocation.proceed()).thenReturn("Hello");
         Object result = sofaTracerIntroductionInterceptor.invoke(methodInvocation);
         assertThat(result).isEqualTo("Hello");
     }

    private com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer getTracerAnnotation() {
        Method method = ReflectionUtils.findMethod(SofaTracerIntroductionInterceptorTests.A.class ,"hello");
        return AnnotationUtils.findAnnotation(method, com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer.class);
    }


     static class A {

         @Tracer
         public void hello() {
         }

     }

     static class B {

         public void hello() {
         }

     }
}
