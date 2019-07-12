# 使用 SOFATracer 在应用日志中打印 TraceId 和 SpanId

本示例演示如何在集成了 SOFATracer 的应用，在打印日志时如何配置打印 TraceId 和 SpanId。

## 环境准备

要使用 SOFABoot，需要先准备好基础环境，SOFABoot 依赖以下环境：
- JDK7 或 JDK8
- 需要采用 Apache Maven 3.2.5 或者以上的版本来编译

## 引入 SOFATracer

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

然后，在工程中添加 SOFATracer 依赖：

```
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>
```

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括`spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

```
# Application Name
spring.application.name=SOFATracerSLF4JDemo

# logging path
logging.path=./logs
```

## 配置 PatternLayout

此示例工程使用的日志实现框架是 Logback，在日志配置文件 `logback-spring.xml` 中新增了一个 `appender` 即 `MDC-EXAMPLE-APPENDER`，之后配置 Logback 的 [PatternLayoutEncoder](https://logback.qos.ch/)：

```xml
<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
    <!--output format：%d is for date，%thread is for thread name，%-5level：loglevel with 5 character  %msg：log message，%n line breaker-->
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level [%X{SOFA-TraceId},%X{SOFA-SpanId}]  %logger{50} - %msg%n</pattern>
    <!-- encoding -->
    <charset>UTF-8</charset>
 </encoder>
```
## 应用中面向 SLF4J 编程接口打印日志

应用的日志打印需要面向编程接口 SLF4j，即添加依赖：

```xml
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
</dependency>
```

通过使用 SLF4J 的 API 获取日志实现实例，示例工程中配置的日志名称是 `MDC-EXAMPLE`，即可以通过如下方式获取日志实现：

```java
private static Logger       logger   = LoggerFactory.getLogger("MDC-EXAMPLE");
```

通过获取的日志实例直接在应用打印日志即可，如：

```java
logger.info("SOFATracer Print TraceId and SpanId");
```

## 运行

可以将工程导入到 IDE 中运行生成的工程里面中的 `main` 方法（一般上在 XXXApplication 这个类中）启动应用，也可以直接在该工程的根目录下运行 `mvn spring-boot:run`，将会在控制台中看到启动打印的日志：

```
11:32:21.128 INFO  org.springframework.boot.web.servlet.FilterRegistrationBean - Mapping filter: 'SpringMvcSofaTracerFilter' to urls: [/*]
11:32:22.190 INFO  o.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/slf4j]}" onto public java.util.Map<java.lang.String, java.lang.Object> com.alipay.sofa.tracer.examples.slf4j.controller.SampleRestController.slf4j(java.lang.String)
11:32:23.501 INFO  org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer - Tomcat started on port(s): 8080 (http)
```

可以通过在浏览器中输入 [http://localhost:8080/slf4j](http://localhost:8080/slf4j) 来访问 REST 服务，结果类似如下：

```json
{
	content: "Hello, SOFATracer SLF4J MDC DEMO!",
	id: 1,
	success: true
}
```

## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── SOFATracerSLF4JDemo
│   ├── common-default.log
│   ├── common-error.log
│   └── mdc-example.log
└── tracelog
    ├── shadow
    │   ├── spring-mvc-digest.log
    │   └── spring-mvc-stat.log
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log
```

通过访问 [http://localhost:8080/slf4j](http://localhost:8080/slf4j) 可以看到应用日志 `mdc-example.log` 有如下内容，即默认输出了 `TraceId` 和 `SpanId` 信息 `[0a0fe8fd1526095994169100175777,0]`：

```
2018-05-12 11:33:14.237 [http-nio-8080-exec-1] INFO  [0a0fe8fd1526095994169100175777,0]  MDC-EXAMPLE - SOFATracer Print TraceId and SpanId
```


