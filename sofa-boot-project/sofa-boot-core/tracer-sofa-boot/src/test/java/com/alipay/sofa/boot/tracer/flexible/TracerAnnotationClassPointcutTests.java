package com.alipay.sofa.boot.tracer.flexible;

import com.alipay.sofa.tracer.plugin.flexible.annotations.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link TracerAnnotationClassPointcut}.
 *
 * @author huzijie
 * @version TracerAnnotationClassPointcutTests.java, v 0.1 2023年01月10日 8:19 PM huzijie Exp $
 */
public class TracerAnnotationClassPointcutTests {

    @Test
    public void classFilterMatch() {
        TracerAnnotationClassPointcut tracerAnnotationClassPointcut = new TracerAnnotationClassPointcut();
        ClassFilter classFilter = tracerAnnotationClassPointcut.getClassFilter();
        assertThat(classFilter.matches(A.class)).isTrue();
        assertThat(classFilter.matches(B.class)).isFalse();
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
