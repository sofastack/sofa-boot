

**框架层面的基础 starter 依赖，此基础功能模块会被其他中间件依赖和使用。定义标准和规范，需要中间件 owner 按照标准实现和注册相应信息。**


## 1. 优雅上下线

### 1.1 优雅上线标准

利用我们的健康检查接口,再检查通过后，我们可以对外提供服务，这里我们中间件引入一个健康检查的依赖：

```java
<-- 基准的健康检查依赖 -->
<dependency>
  <groupId>com.alipay.sofa</groupId>
  <artifactId>healthcheck-sofa-boot-starter</artifactId>
</dependency>
```
按照我们的检查的周期实现相应的流量入口、流量出口等接口并完成注册即可：[具体参考文档](https://lark.alipay.com/middleware/sofaboot/health-check),
当我们的健康检查通过后（所有组件均通过），我们才会对外提供服务。当然这里为了达到检查失败，发布部署失败或者不能对外提供服务，我们会有技术栈做相应的配置
来一起完成这个健康检查的功能。

### 1.2 优雅下线标准

 考虑到我们的中间件都是基于标准 Spring 进行我们的配置解析，在解析为对应的模型后，做相应的中间件的初始化并对外提供中间件的能力。
 就目前来说，我们的中间件也是利用 Spring 的相关事件完成一些启动和初始化动作，我们同样可以利用 Spring 的一个事件完成我们的优雅关闭操作，
 即当 jvm 准备退出时，Spring 会发布一个 `ContextClosedEvent`我们可以利用此事件完成我们的优雅关闭的操作，类似的代码可以如下：

 ```java
 @Component
 public class SofaContextCloserListener implements ApplicationListener<ContextClosedEvent> {
     @Override
     public void onApplicationEvent(ContextClosedEvent event) {
         // We do something when shutdown!!!
         System.err.println("com.alipay.slite2.web.event.SofaContextCloserListener is closed!!!");
     }
 }
 ```

在这个事件监听器中可以完成我们的优雅关闭的操作。当然，如果我们对优雅关闭的事件在最后执行时，也需要有顺序的要求时，我们可以直接实现
`org.springframework.core.Ordered` 接口，并配置相应的值完成事件执行器的排序操作。

> 当然这里是针对我们目前 SOFA Boot 的运行机制的方案。当我们的类隔离方案、以及之后的在类隔离的方案之上做多个 Spring 上下文隔离，事件
模式需要做相应调整变化

## 2.版本信息汇总

* 相关依赖：

```java
 <dependency>
     <groupId>com.alipay.sofa</groupId>
     <artifactId>infra-sofa-boot-starter</artifactId>
 </dependency>
```

为了让我们的 SOFA Boot 的用户对使用相关的中间件版本能够有一个运行时更为形象的理解，同时我们排查问题时也可以让其提供运行时更为"可信赖"的信息。我们的中间件
同学只需要继承我们的这个抽象类：`AbstractSofaBootMiddlewareVersionFacade` 并将其实现注册到 Spring 上下文即可。
其中有几个方法大家一定要实现。同时也提供了可以运行是定制的一些信息，大家可以根据实际需要放置一些信息，大家可以通过覆盖此方法实现 `AbstractSofaBootMiddlewareVersionFacade.getRuntimeInfo`。
实现后，框架层面会将其注册为 Endpoint 以便能通过 HTTP 访问，具体的访问效果类似如下：

```java
[
{
authors: [
"guanchao.ygc"
],
docs: "https://www.cloud.alipay.com/docs",
name: "SOFA Boot",
runtimeInfo: null,
version: "1.0.1.SOFABoot"
},
{
authors: [
"guanchao.ygc"
],
docs: "https://www.cloud.alipay.com/docs",
name: "SOFA REST",
runtimeInfo: null,
version: "1.0.1.SOFAREST"
},
{
authors: [
"guanchao.ygc"
],
docs: "https://www.cloud.alipay.com/docs",
name: "SOFA RPC",
runtimeInfo: null,
version: "1.0.1.SOFARPC"
}
]
```

> 关于后续的统一的数据上报汇总等，通过我们的框架能力也会统一做掉，大家无需再主动通过代码感知。

## 3.日志

此基础设施相应的日志目录结果为：

```java
├── infra
│   ├── common-default.log
│   └── common-error.log
```


## 4.总结一下需要汇报的相关信息

### 4.1 Endpoint

* 中间件的版本汇总信息：如，sofarpc-spring-boot-starter: 2.2.5 版本，AbstractSofaBootMiddlewareVersionFacade 接口提供
* 中间件的依赖信息:将 classpath 的 Lib 依赖信息进行汇报，方便我们控制台做中枢的规则管控；同时提供查询能力，即本地可以根据 curl http:// 来获取到相应的信息。
同时控制台提供依赖查询信息 todo
* 汇报健康检查的状态信息，lookout-sofa-boot-starter 等信息会汇总汇报
* 汇报启动过程中的 bean 信息 lookout-sofa-boot-starter 等信息会汇总汇报


