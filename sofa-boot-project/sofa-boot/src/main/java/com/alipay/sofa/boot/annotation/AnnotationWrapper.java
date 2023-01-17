package com.alipay.sofa.boot.annotation;

import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * @author huzijie
 * @version AnnotationWrapper.java, v 0.1 2023年01月17日 4:05 PM huzijie Exp $
 */
public class AnnotationWrapper<A extends Annotation> {

    private final Class<A> clazz;

    private Annotation delegate;

    private PlaceHolderBinder binder;

    private Environment environment;

    private AnnotationWrapper(Class<A> clazz) {
        this.clazz = clazz;
    }

    public static <A extends Annotation> AnnotationWrapper<A> create(Class<A> clazz) {
        return new AnnotationWrapper<>(clazz);
    }

    public static <A extends Annotation> AnnotationWrapper<A> create(Annotation annotation) {
        return new AnnotationWrapper(annotation.getClass());
    }

    public AnnotationWrapper<A> withBinder(PlaceHolderBinder binder) {
        this.binder = binder;
        return this;
    }

    public AnnotationWrapper<A> withEnvironment(Environment environment) {
        this.environment = environment;
        return this;
    }

    public A wrap(A annotation) {
        Assert.isInstanceOf(clazz, annotation, "Parameter must be annotation type.");
        this.delegate = annotation;
        return build();
    }

    @SuppressWarnings("unchecked")
    private A build() {
        if (delegate != null) {
            ClassLoader cl = this.getClass().getClassLoader();
            Class<?>[] exposedInterface = {delegate.annotationType(), WrapperAnnotation.class};
            return (A) Proxy.newProxyInstance(cl, exposedInterface,
                    new PlaceHolderAnnotationInvocationHandler(delegate, binder, environment)));
        }
        return null;
    }
}
