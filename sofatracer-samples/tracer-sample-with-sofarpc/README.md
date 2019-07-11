# 使用 SOFATracer记录 RPC

本示例引入 SOFABoot 基础依赖管控，并且引入 SOFATracer ，演示如何记录 SOFARPC 调用信息.

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
</dependency>
```

最后，在工程的 `application.properties` 文件下添加一个 SOFATracer 要使用的参数，包括`spring.application.name` 用于标示当前应用的名称；`logging.path` 用于指定日志的输出目录。

```
# Application Name
spring.application.name=SOFATracerRPC
# logging path
logging.path=./logs
```

## 运行

可以将工程导入到 IDE 中运行生成的工程里面中的 `main` 方法（DirectClientApplication 这个类中）启动应用，将会在控制台中看到启动打印的日志：

```
2018-06-30 21:37:36.899  INFO 40179 --- [           main] o.s.c.support.DefaultLifecycleProcessor  : Starting beans in phase 0
2018-06-30 21:37:37.297  INFO 40179 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8081 (http)
2018-06-30 21:37:37.302  INFO 40179 --- [           main] c.a.s.t.e.s.d.DirectClientApplication    : Started DirectClientApplication in 8.766 seconds (JVM running for 9.887)
invoke result:direct
direct invoke success
```


## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── rpc-client-digest.log
    ├── rpc-client-stat.log
    ├── rpc-server-digest.log
    └── rpc-server-stat.log

```

只要发起 rpc 调用, SOFATracer 会记录每一次访问的摘要日志.

客户端摘要日志

```java
{"timestamp":"2018-06-30 21:37:37.569","tracerId":"1e1bcdcf1530365857309100140179","spanId":"0","span.kind":"client","local.app":"SOFATracerRPC","protocol":"bolt","service":"com.alipay.sofa.tracer.examples.sofarpc.direct.DirectService:1.0","method":"sayDirect","current.thread.name":"main","invoke.type":"sync","router.record":"DIRECT","remote.ip":"127.0.0.1:12200","local.client.ip":"127.0.0.1","result.code":"00","req.serialize.time":"41","resp.deserialize.time":"59","resp.size":"170","req.size":"582","client.conn.time":"0","client.elapse.time":"104","local.client.port":"63803","baggage":""}

```

客户端统计日志

```java
{"time":"2018-06-30 21:38:33.977","stat.key":{"method":"sayDirect","local.app":"SOFATracerRPC","service":"com.alipay.sofa.tracer.examples.sofarpc.direct.DirectService:1.0"},"count":1,"total.cost.milliseconds":259,"success":"Y"}

```

服务端摘要日志

```java
{"timestamp":"2018-06-30 21:37:37.562","tracerId":"1e1bcdcf1530365857309100140179","spanId":"0","span.kind":"server","service":"com.alipay.sofa.tracer.examples.sofarpc.direct.DirectService:1.0","method":"sayDirect","remote.ip":"127.0.0.1","remote.app":"SOFATracerRPC","protocol":"bolt","local.app":"SOFATracerRPC","current.thread.name":"SOFA-SEV-BOLT-BIZ-12200-5-T1","result.code":"00","server.pool.wait.time":"2","biz.impl.time":"0","resp.serialize.time":"1","req.deserialize.time":"5","resp.size":"170","req.size":"582","baggage":""}

```

服务端统计日志

```java
{"time":"2018-06-30 21:38:33.977","stat.key":{"method":"sayDirect","local.app":"SOFATracerRPC","remote.app":"SOFATracerRPC","service":"com.alipay.sofa.tracer.examples.sofarpc.direct.DirectService:1.0"},"count":1,"total.cost.milliseconds":4,"success":"Y"}
```