# 使用 SOFATracer 记录 HttpClient 链路调用数据

本示例演示如何在集成了 SOFATracer 的应用，通过配置 HttpClient 的使用方式将链路数据记录在文件中。

## 环境准备

要使用 SOFABoot，需要先准备好基础环境，SOFABoot 依赖以下环境：

- JDK8
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

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>
```

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括`spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

```
# Application Name
spring.application.name=HttpClientDemo
# logging path
logging.path=./logs
```

## 添加 HttpClient、SOFATracer 依赖和 SOFATracer 的 HttpClient 插件

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-tracer-httpclient-plugin</artifactId>
</dependency>
<!-- HttpClient Dependency -->
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <!-- 版本 4.5.X 系列 -->
    <version>4.5.3</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpasyncclient</artifactId>
    <!-- 版本 4.5.X 系列 -->
    <version>4.1.3</version>
</dependency>
```


## 添加一个提供 RESTful 服务的 Controller

```java
@RestController
public class SampleRestController {

    private final AtomicLong counter = new AtomicLong(0);

    /**
     * Request http://localhost:8080/httpclient?name=
     * @param name name
     * @return Map of Result
     */
    @RequestMapping("/httpclient")
    public Map<String, Object> greeting(@RequestParam(value = "name", defaultValue = "httpclient") String name) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("count", counter.incrementAndGet());
        map.put("name", name);
        return map;
    }
}
```


## 构造 HttpClient 发起一次对上文的 RESTful 服务的调用

构造 HttpClient 的过程不详细展开，具体可以参见本示例中 `HttpClientInstance`(同步) 和 `HttpAsyncClientInstance`（异步）构造方法，仅供参考，其中需要重点强调的是，
为了能够使得 HttpClient 这个第三方开源组件能够支持  SOFATracer 的链路调用，SOFATracer 提供了 HttpClient 的插件扩展即 sofa-tracer-httpclient-plugin ，为了使得用 SOFATracer 的 HttpCliet 正确的埋点，我们需要使用 HttpClientBuilder 去构造 HttpClient 的实例，
并需要显示的调用 SofaTracerHttpClientBuilder.clientBuilder(httpClientBuilder) 来构造出一个经过 SOFATracer 埋点的 HttpClientBuilder ，关键代码示例如下

* 构造 HttpClient 同步调用实例：

```java
HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
//SOFATracer
SofaTracerHttpClientBuilder.clientBuilder(httpClientBuilder);
CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(connManager).disableAutomaticRetries()
                .build();
```

* 构造 HttpClient 异步调用实例：

```java
RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(6000).setConnectTimeout(6000).setConnectionRequestTimeout(6000).build();
HttpAsyncClientBuilder httpAsyncClientBuilder = HttpAsyncClientBuilder.create();
//tracer
SofaTracerHttpClientBuilder.asyncClientBuilder(httpAsyncClientBuilder);
CloseableHttpAsyncClient asyncHttpclient = httpAsyncClientBuilder.setDefaultRequestConfig(requestConfig).build();
```

通过 SofaTracerHttpClientBuilder(clientBuilder 方法构造同步，asyncClientBuilder 方法构造异步) 构造的 HttpClient 在发起对上文的 RESTful 服务调用的时候，就会埋点 SOFATracer 的链路的数据。

## 运行

可以将工程导入到 IDE 中运行工程里面中的 `main` 方法（本实例 main 方法在 HttpClientDemoApplication 中）启动应用，在控制台中看到启动打印的日志如下：

```
2018-09-27 20:31:21.465  INFO 33277 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Registering beans for JMX exposure on startup
2018-09-27 20:31:21.599  INFO 33277 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2018-09-27 20:31:21.608  INFO 33277 --- [           main] c.a.s.t.e.h.HttpClientDemoApplication    : Started HttpClientDemoApplication in 5.949 seconds (JVM running for 6.573)
```

当有类似如下的日志时，说明 HttpClient 的调用成功：

```
2018-09-27 20:31:22.336  INFO 33277 --- [           main] c.a.s.t.e.h.HttpClientDemoApplication    : Response is {"count":1,"name":"httpclient"}
2018-09-27 20:31:22.453  INFO 33277 --- [           main] c.a.s.t.e.h.HttpClientDemoApplication    : Async Response is {"count":2,"name":"httpclient"}
```


## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要进行配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── httpclient-digest.log
    ├── httpclient-stat.log
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log

```

示例中通过构造两个 HttpClient（一个同步一个异步） 发起对同一个 RESTful 服务的调用，调用完成后可以在 httpclient-digest.log 中看到类似如下的日志，而对于每一个输出字段的含义可以看 SOFATracer 的说明文档：

```
{"time":"2018-09-27 20:31:22.328","local.app":"HttpClientDemo","traceId":"0a0fe8801538051482054100133277","spanId":"0","request.url":"http://localhost:8080/httpclient","method":"GET","result.code":"200","req.size.bytes":0,"resp.size.bytes":-1,"time.cost.milliseconds":274,"current.thread.name":"main","remote.app":"","baggage":""}
{"time":"2018-09-27 20:31:22.443","local.app":"HttpClientDemo","traceId":"0a0fe8801538051482410100233277","spanId":"0","request.url":"http://localhost:8080/httpclient","method":"GET","result.code":"200","req.size.bytes":0,"resp.size.bytes":-1,"time.cost.milliseconds":33,"current.thread.name":"I/O dispatcher 1","remote.app":"","baggage":""}
```
