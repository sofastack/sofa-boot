# 使用 SOFATracer

本示例引入 SOFABoot 基础依赖管控，并且引入 SOFATracer ，演示如何快速上手 SOFATracer。

## 环境准备

要使用 SOFABoot，需要先准备好基础环境，SOFABoot 依赖以下环境：
- JDK7 或 JDK8
- 需要采用 Apache Maven 3.2.5 或者以上的版本来编译

## 引入 SOFABoot

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

然后，添加一个 SOFATracer 依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>tracer-sofa-boot-starter</artifactId>
    <!-- SOFA Boot 版本统一管控 -->
</dependency>
```

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括 `spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

```properties
# Application Name
spring.application.name=SOFATracerSpringMVC
# logging path
logging.path=./logs
```

## 添加一个最简单的 Controller

在工程代码中，添加一个最简单的 Controller，例如：

```java
@RestController
public class SampleRestController {

    private static final String TEMPLATE = "Hello, %s!";

    private final AtomicLong    counter  = new AtomicLong();

    /***
     * http://localhost:8080/springmvc
     * @param name name
     * @return map
     */
    @RequestMapping("/springmvc")
    public Map<String, Object> springmvc(@RequestParam(value = "name", defaultValue = "SOFATracer SpringMVC DEMO") String name) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("success", true);
        resultMap.put("id", counter.incrementAndGet());
        resultMap.put("content", String.format(TEMPLATE, name));
        return resultMap;
    }
}
```

## 运行

可以将工程导入到 IDE 中运行生成的工程里面中的 `main` 方法（一般上在 XXXApplication 这个类中）启动应用，也可以直接在该工程的根目录下运行 `mvn spring-boot:run`，将会在控制台中看到启动打印的日志：

```
2018-05-11 11:55:11.932  INFO 66490 --- [ost-startStop-1] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'SpringMvcOpenTracingFilter' to urls: [/*]
2018-05-11 11:55:13.961  INFO 66490 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2018-05-11 11:55:13.970  INFO 66490 --- [           main] c.a.s.t.e.springmvc.DemoApplication      : Started DemoApplication in 8.361 seconds (JVM running for 9.34)
```

可以通过在浏览器中输入 [http://localhost:8080/springmvc](http://localhost:8080/springmvc) 来访问 REST 服务，结果类似如下：

```json
{
	content: "Hello, SOFATracer SpringMVC DEMO!",
	id: 1,
	success: true
}
```

## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── spring-mvc-digest.log
    ├── spring-mvc-stat.log
    ├── static-info.log
    └── tracer-self.log

```

通过访问 [http://localhost:8080/springmvc](http://localhost:8080/springmvc) SOFATracer 会记录每一次访问的摘要日志，可以打开 `spring-mvc-digest.log` 看到具体的输出内容，而对于每一个输出字段的含义可以看 SOFATracer 的说明文档。

```json
{"time":"2018-05-17 22:20:34.279","local.app":"SOFATracerSpringMVC","traceId":"0a0fe9391526566833985100139443","spanId":"0","request.url":"http://localhost:8080/springmvc","method":"GET","result.code":"200","req.size.bytes":-1,"resp.size.bytes":69,"time.cost.milliseconds":284,"current.thread.name":"http-nio-8080-exec-1","baggage":""}
```

## 对于标准 servlet 容器的支持（ tomcat/jetty 等）

sofa-tracer-springmvc-plugin 插件提供了对标准 servlet-api 的支持，因此，凡基于标准 servlet 规范的容器，也均可以使用此插件来收集链路数据。

### 依赖引入

上面的案例是基于 springboot/SOFABoot 使用的；如果在非 springboot/SOFABoot 中使用，仅需引入插件依赖即可：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-tracer-springmvc-plugin</artifactId>
    <version>2.2.0</version>
</dependency>
```

### 配置 filter

在工程的 web.xml  配置 filter

```xml
<filter>
  <filter-name>SpringMvcSofaTracerFilter</filter-name>
  <filter-class>com.alipay.sofa.tracer.plugins.springmvc.SpringMvcSofaTracerFilter</filter-class>
</filter>
<filter-mapping>
  <filter-name>SpringMvcSofaTracerFilter</filter-name>
  <url-pattern>/*</url-pattern>
</filter-mapping>

```
这样应用通过 tomcat 或者 jetty 启动时，即可将 servlet 容器的链路数据打印到日志中了。