# 使用 SOFATracer 记录 DataSource

本示例引入 SOFABoot 基础依赖管控，并且引入 SOFATracer ，演示如何记录 DataSource 调用信息.

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
spring.application.name=SOFATracerDataSource
# logging path
logging.path=./logs
```

## 运行

可以将工程导入到 IDE 中运行生成的工程里面中的 `main` 方法（DirectClientApplication 这个类中）启动应用，将会在控制台中看到启动打印的日志：

```
2018-08-29 20:12:26.270  INFO 31488 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Bean with name 'simpleDataSource' has been autodetected for JMX exposure
2018-08-29 20:12:26.273  INFO 31488 --- [           main] o.s.j.e.a.AnnotationMBeanExporter        : Located MBean 'simpleDataSource': registering with JMX server as MBean [com.alibaba.druid.pool:name=simpleDataSource,type=DruidDataSource]
2018-08-29 20:12:26.327  INFO 31488 --- [           main] s.b.c.e.t.TomcatEmbeddedServletContainer : Tomcat started on port(s): 8080 (http)
2018-08-29 20:12:26.331  INFO 31488 --- [           main] c.a.s.t.e.datasource.DemoApplication     : Started DemoApplication in 3.799 seconds (JVM running for 4.745)
```

该样例工程使用 H2 内存数据库，并暴露 /create 用于新建一张数据表，用户访问 /create 即可执行创建数据库表 SQL 语句：
```sql
DROP TABLE IF EXISTS TEST;
CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255));
```


## 查看日志

在上面的 `application.properties` 里面，我们配置的日志打印目录是 `./logs` 即当前应用的根目录（我们可以根据自己的实践需要配置），在当前工程的根目录下可以看到类似如下结构的日志文件：

```
./logs
├── spring.log
└── tracelog
    ├── datasource-client-digest.log
    └── datasource-client-stat.log

```

只要发起 SQL 调用, SOFATracer 会记录每一次访问的摘要日志.

客户端摘要日志

```java
"time":"2018-08-29 19:57:31.990","local.app":"SOFATracerDataSource","traceId":"0a0fe8691535543846625100130768","spanId":"0.1","database.name":"h2DataSource","sql":"DROP TABLE IF EXISTS TEST;
CREATE TABLE TEST(ID INT PRIMARY KEY%2C NAME VARCHAR(255));","result.code":"success","total.time":"2114ms","connection.establish.span":"242ms","db.execute.cost":"1866ms","database.type":"MYSQL","database.endpoint":"jdbc:h2:~/test:-1","current.thread.name":"http-nio-8080-exec-1","baggage":""}
```

客户端统计日志

```java
{"time":"2018-08-29 19:57:03.454","stat.key":{"local.app":"SOFATracerDataSource","database.name":"h2DataSource"},"count":1,"total.cost.milliseconds":262,"success":"true","load.test":"F"}
```
