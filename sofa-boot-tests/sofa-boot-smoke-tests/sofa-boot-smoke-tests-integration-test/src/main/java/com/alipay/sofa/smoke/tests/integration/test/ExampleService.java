package com.alipay.sofa.smoke.tests.integration.test;

public interface ExampleService {
    String execute(String target, Object... args);
    Object getDependency(String name);
}