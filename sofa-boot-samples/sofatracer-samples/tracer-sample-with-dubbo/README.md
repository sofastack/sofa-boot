## 使用 SOFATracer 集成 Dubbo 埋点

本案例使用的各框架组件的版本如下：

* SOFABoot 3.1.1/SpringBoot 2.1.0.RELEASE
* SOFATracer 2.4.0/3.0.4 
* JDK 8

本案例包括三个子模块：

* tracer-sample-with-dubbo-consumer     服务调用方
* tracer-sample-with-dubbo-provider     服务提供方
* tracer-sample-with-dubbo-facade       接口

## 新建 SOFABoot 工程作为父工程

在创建好一个 Spring Boot 的工程之后，接下来就需要引入 SOFABoot 的依赖，首先，需要将上文中生成的 Spring Boot 工程的 `zip` 包解压后，修改 Maven 项目的配置文件 `pom.xml`，将

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>${spring.boot.version}</version>
    <relativePath/>
</parent>
```

替换为：

```xml
<parent>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofaboot-dependencies</artifactId>
    <version>${sofa.boot.version}</version>
</parent>
```
这里的 ${sofa.boot.version} 指定具体的 SOFABoot 版本，参考[发布历史](https://github.com/alipay/sofa-build/releases)。

## 新建 tracer-sample-with-dubbo-facade

提供一个接口

```java
public interface HelloService {
    String SayHello(String name);
}
```

## 新建 tracer-sample-with-dubbo-provider

* 在工程模块的 pom 文件中添加 SOFATracer 依赖

    ```xml
    <dependency>
        <groupId>com.alipay.sofa</groupId>
        <artifactId>tracer-sofa-boot-starter</artifactId>
    </dependency>
    ```
    > SOFATracer 版本受 SOFABoot 版本管控，如果使用的 SOFABoot 版本不匹配，则需要手动指定 tracer 版本，且版本需高于 2.4.0.

* 在工程的 `application.properties` 文件下添加相关参数

    ```properties
    # Spring boot application
    spring.application.name=dubbo-provider
    # Base packages to scan Dubbo Component: @org.apache.dubbo.config.annotation.Service
    dubbo.scan.base-packages=com.alipay.sofa.tracer.samples.dubbo.impl
    ##  Filter
    dubbo.provider.filter=dubboSofaTracerFilter
    # Dubbo Protocol
    dubbo.protocol.name=dubbo
    ## Dubbo Registry
    dubbo.registry.address=zookeeper://localhost:2181
    logging.path=./logs
    ```
* 使用注解方式发布 Dubbo 服务

    ```java
    @Service
    public class HelloServiceImpl implements HelloService {
        @Override
        public String SayHello(String name) {
            return "Hello , "+name;
        }
    }
    ```
 
## 新建 tracer-sample-with-dubbo-consumer

* 在工程模块的 pom 文件中添加 SOFATracer 依赖

    ```xml
    <dependency>
        <groupId>com.alipay.sofa</groupId>
        <artifactId>tracer-sofa-boot-starter</artifactId>
    </dependency>
    ```

* 在工程的 `application.properties` 文件下添加相关参数

    ```properties
    spring.application.name=dubbo-consumer
    dubbo.registry.address=zookeeper://localhost:2181
    dubbo.consumer.filter=dubboSofaTracerFilter
    logging.path=./logs
    ```
* 服务引用

    ```java
    @Reference(async = false)
    public HelloService helloService;
    
    @Bean
    public ApplicationRunner runner() {
        return args -> {
            logger.info(helloService.SayHello("sofa"));
        };
    }
    ```

## 测试

先后启动 tracer-sample-with-dubbo-provider 和 tracer-sample-with-dubbo-consumer 两个工程; 然后查看日志：

* dubbo-client-digest.log
```json
{"time":"2019-04-03 11:36:01.909","traceId":"0a0fe8451554262561656100126684","spanId":"0","span.kind":"client","local.app":"dubbo-consumer","protocol":"dubbo","service":"com.alipay.sofa.tracer.examples.dubbo.facade.HelloService","method":"SayHello","invoke.type":"sync","remote.host":"10.15.232.69","remote.port":"20880","local.host":"10.15.232.69","client.serialize.time":35,"client.deserialize.time":0,"req.size.bytes":323,"resp.size.bytes":323,"result.code":"00","current.thread.name":"main","time.cost.milliseconds":252,"baggage":""}
```

* dubbo-server-digest.log
```json
{"time":"2019-04-03 11:36:01.880","traceId":"0a0fe8451554262561656100126684","spanId":"0","span.kind":"server","local.app":"dubbo-provider","service":"com.alipay.sofa.tracer.examples.dubbo.facade.HelloService","method":"SayHello","local.host":"10.15.232.69","local.port":"54178","protocol":"dubbo","server.serialize.time":0,"server.deserialize.time":27,"result.code":"00","current.thread.name":"DubboServerHandler-10.15.232.69:20880-thread-2","time.cost.milliseconds":3,"baggage":""}
```

* dubbo-client-stat.log
```json
{"time":"2019-04-03 11:37:01.650","stat.key":{"method":"SayHello","local.app":"dubbo-consumer","service":"com.alipay.sofa.tracer.examples.dubbo.facade.HelloService"},"count":1,"total.cost.milliseconds":252,"success":"Y"}
```

* dubbo-server-stat.log
```json
{"time":"2019-04-03 11:37:01.872","stat.key":{"method":"SayHello","remote.app":"dubbo-provider","service":"com.alipay.sofa.tracer.examples.dubbo.facade.HelloService"},"count":1,"total.cost.milliseconds":3,"success":"Y"}
```