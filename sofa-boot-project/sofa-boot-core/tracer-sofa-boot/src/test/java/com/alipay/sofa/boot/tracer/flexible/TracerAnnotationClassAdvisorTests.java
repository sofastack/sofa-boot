package com.alipay.sofa.boot.tracer.flexible;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TracerAnnotationClassAdvisor}.
 *
 * @author huzijie
 * @version TracerAnnotationClassAdvisorTests.java, v 0.1 2023年01月10日 8:25 PM huzijie Exp $
 */
public class TracerAnnotationClassAdvisorTests {

    @Test
    public void getPointAndAdvice() {
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Nullable
            @Override
            public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
                return invocation.proceed();
            }
        };
        TracerAnnotationClassAdvisor advisor = new TracerAnnotationClassAdvisor(methodInterceptor);
        assertThat(advisor.getAdvice()).isEqualTo(methodInterceptor);
        assertThat(advisor.getPointcut()).isInstanceOf(TracerAnnotationClassPointcut.class);
    }
}
