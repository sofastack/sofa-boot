# 如何在 SOFABoot 环境中使用 SOFARPC

## 简介
该用例工程演示如何在 SOFABoot 环境中使用 SOFARPC，阅读该文档之前，建议先了解 [SOFARPC](https://github.com/alipay/sofa-rpc)

## 引入 SOFABoot 依赖
SOFABoot 提供了如健康检查，上下文隔离等基础能力，同时提供了多种中间件进行选择使用。SOFABoot 对这些提供这些能力的依赖利用如下pom进行了管控，将工程的parent设为该pom。
```java
<parent>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofaboot-dependencies</artifactId>
    <version>2.3.0</version>
</parent>
```

## 引入 SOFARPC Starter
SOFARPC Starter 是 SOFARPC 基于 SOFABoot 实现的框架，能够将 SOFARPC 的能力以统一的编程界面和简单的操作形式提供给使用者。该依赖已被 SOFABoot 管控，用户只需要引入如下依赖：
```java
<dependency>
     <groupId>com.alipay.sofa</groupId>
     <artifactId>rpc-sofa-boot-starter</artifactId>
 </dependency>
```

## 声明 SOFABoot 的xsd文件
在要使用的XML配置文件中将头部xsd文件的声明设置为如下。这样就能够使用 SOFABoot 定义的XML元素进行开发。
```java
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
在XML中配置如下，就能够发布一个 SOFARPC 服务。
```java
<sofa:service ref="personServiceImpl" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService">
        <sofa:binding.bolt/>
        <sofa:binding.rest/>
        <sofa:binding.dubbo/>
</sofa:service>
```
其中service元素表示发布该服务，三个binding元素声明了该服务提供的调用协议。

## 服务引用
在XML中配置如下，就能够引用 SOFARPC 服务。
```java
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService" local-first="false">
        <sofa:binding.bolt/>
</sofa:reference>

<sofa:reference id="personReferenceRest" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService" local-first="false">
        <sofa:binding.rest/>
</sofa:reference>
<sofa:reference id="personReferenceDubbo" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService" local-first="false">
        <sofa:binding.dubbo/>
</sofa:reference>
```
其中reference元素表示引用该服务，binding元素声明了该服务引用的调用的协议。如上就在Spring上下文中构建了两个服务的远程代理类，名字分别为personReferenceBolt和personReferenceRest。local-first属性表示是否优先调用本地发布的服务，这里设为false，通过网络进行调用。

## 服务调用
从Spring上下文中获取到需要的服务引用，发起远程调用。
```java
PersonService personBolt = (PersonService) applicationContext.getBean("personReferenceBolt");
PersonService personRest = (PersonService) applicationContext.getBean("personReferenceRest");

System.out.println(personBolt.sayName("bolt"));
System.out.println(personRest.sayName("rest"));
```

## 参数设置
在声明服务发布或引用的同时也可以设置需要的参数。
```java
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService" local-first="false">
     <sofa:binding.bolt>
          <sofa:global-attrs timeout="3000" address-wait-time="2000"/>
          <sofa:route target-url="127.0.0.1:22000"/>
          <sofa:method name="sayName" timeout="3000"/>
     </sofa:binding.bolt>
</sofa:reference>
```
如上示例，global-attrs元素中可以设置调用超时，地址等待时间等参数；target-url能够设置直连调用的地址；method标签能够设置方法级别参数。

## Filter配置
在 SOFABoot 环境中可以方便的进行Filter的配置。
1.全局生效方式。通过rpc-global-filter元素配置一个对所有服务都会生效的Filter。
```java
<bean id="personFilter" class="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonServiceFilter"/>
  <sofa:rpc-global-filter ref="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonFilter"/>
```  
2.服务生效方式。只对指定的服务生效。
```java
<bean id="personFilter" class="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonServiceFilter"/>
<sofa:reference id="personReferenceBolt" interface="com.alipay.sofa.boot.examples.demo.rpc.bean.PersonService" local-first="false">
     <sofa:binding.bolt>
          <sofa:global-attrs filter="personFilter"/>
     </sofa:binding.bolt>
</sofa:reference>
```

