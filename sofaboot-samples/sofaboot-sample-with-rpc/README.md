# 如何在 SOFABoot 环境中使用 SOFARPC

## 简介
该用例工程演示如何在 SOFABoot 环境中使用 SOFARPC，阅读该文档之前，建议先了解 [SOFARPC](https://github.com/alipay/sofa-rpc)

## 引入 SOFABoot 依赖
SOFABoot 提供了如健康检查，上下文隔离等基础能力，同时提供了多种中间件进行选择使用。 SOFABoot 对这些提供这些能力的依赖利用如下 pom 进行了管控，将工程的 parent 设为该 pom 。
```xml
<parent>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofaboot-dependencies</artifactId>
    <version>2.5.0</version>
</parent>
```

## 引入 SOFARPC Starter
SOFARPC Starter 是 SOFARPC 基于 SOFABoot 实现的框架，能够将 SOFARPC 的能力以统一的编程界面和简单的操作形式提供给使用者。该依赖已被 SOFABoot 管控，用户只需要引入如下依赖：
```xml
<dependency>
     <groupId>com.alipay.sofa</groupId>
     <artifactId>rpc-sofa-boot-starter</artifactId>
 </dependency>
```

## 声明 SOFABoot 的 xsd 文件
在要使用的 XML 配置文件中将头部 xsd 文件的声明设置为如下。这样就能够使用 SOFABoot 定义的 XML 元素进行开发。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:sofa="http://sofastack.io/schema/sofaboot"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
            http://sofastack.io/schema/sofaboot   http://sofastack.io/schema/sofaboot.xsd"
       default-autowire="byName">
```

## 服务发布
在 XML 中配置如下，就能够发布一个 SOFARPC 服务。
```xml
<sofa:service ref="personServiceImpl" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.bolt/>
    <sofa:binding.rest/>
    <sofa:binding.dubbo/>
</sofa:service>
```
其中 service 元素表示发布该服务，三个 binding 元素声明了该服务提供的调用协议。

## 服务引用
在 XML 中配置如下，就能够引用 SOFARPC 服务。
```xml
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.bolt/>
</sofa:reference>

<sofa:reference id="personReferenceRest" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.rest/>
</sofa:reference>
<sofa:reference id="personReferenceDubbo" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.dubbo/>
</sofa:reference>
```
其中 reference 元素表示引用该服务，binding 元素声明了该服务引用的调用的协议。如上就在 Spring 上下文中构建了两个服务的远程代理类，名字分别为 personReferenceBolt 和 personReferenceRest 。

## 服务调用
从 Spring 上下文中获取到需要的服务引用，发起远程调用。
```java
PersonService personBolt = (PersonService) applicationContext.getBean("personReferenceBolt");
PersonService personRest = (PersonService) applicationContext.getBean("personReferenceRest");

System.out.println(personBolt.sayName("bolt"));
System.out.println(personRest.sayName("rest"));
```

## 参数设置
在声明服务发布或引用的同时也可以设置需要的参数。
```xml
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.bolt>
        <sofa:global-attrs timeout="3000" address-wait-time="2000"/>
        <sofa:route target-url="127.0.0.1:22000"/>
        <sofa:method name="sayName" timeout="3000"/>
    </sofa:binding.bolt>
</sofa:reference>
```
如上示例， global-attrs 元素中可以设置调用超时，地址等待时间等参数； target-url 能够设置直连调用的地址； method 标签能够设置方法级别参数。

## Filter配置
在 SOFABoot 环境中可以方便的进行 Filter 的配置。

1.全局生效方式。通过 rpc-global-filter 元素配置一个对所有服务都会生效的 Filter 。
```xml
<bean id="personFilter" class="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonServiceFilter"/>
<sofa:rpc-global-filter ref="personFilter"/>
```  
2.服务生效方式。只对指定的服务生效。
```xml
<bean id="personFilter" class="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonServiceFilter"/>
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
    <sofa:binding.bolt>
        <sofa:global-attrs filter="personFilter"/>
    </sofa:binding.bolt>
</sofa:reference>
```

