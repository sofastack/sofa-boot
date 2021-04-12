## SOFABoot

![build](https://github.com/sofastack/sofa-boot/workflows/build/badge.svg)
[![Coverage Status](https://codecov.io/gh/sofastack/sofa-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/sofastack/sofa-boot/branch/master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Percentage of issues still open")
![maven](https://img.shields.io/github/release/sofastack/sofa-boot.svg)

SOFABoot is an open source Java development framework based on Spring Boot.

Varieties of enhancements such as application readiness check, Spring context isolation, class isolation, log space separation, etc. are provided.
In addition, SOFABoot accommodates SOFAStack middleware more comfortably and seamlessly for developers coming from Spring Boot.

## Background

Spring Boot makes it easy to create stand-alone, production-grade Spring-based applications which "just runs". However, some domain-specific issues remain open:

- Spring Boot provides health indicators to reveal the liveness of application but not readiness (i.e., the capability of servicing requests).
- No built-in class isolation scheme to support finer modular applications.
- Log configurations of all SDKs used by application are repeatedly arranged.

To address above issues while maintain the advantages of Spring Boot, Ant Group develops the SOFABoot based on Spring Boot and make it open source.
In SOFABoot, SOFAStack middleware SDKs are packaged as self-contained "starters" to provide the corresponding facet or functionality dependencies. 

## Quick Start
Please refer to SOFAStack Documentation for [SOFABoot quick start guide](https://www.sofastack.tech/en/projects/sofa-boot/quick-start/).

## Functionality
To supplement the abilities of deploying large-scale microservices in production environment, SOFABoot offers following enhancements:

### Readiness Check

### Spring Context Isolation

### Class Isolation

### Log Space Separation

### Built-in SOFAStack Middlewares


为了解决 Spring Boot 在实施大规模微服务架构时候的问题，SOFABoot 提供了以下的能力：

### 2.1 增强 Spring Boot 的健康检查能力

针对 Spring Boot 缺少 Readiness Check 能力的情况，SOFABoot 增加了 Spring Boot 现有的健康检查的能力，提供了 Readiness Check 的能力。利用 Readiness Check 的能力，SOFA 中间件中的各个组件只有在 Readiness Check 通过之后，才将流量引入到应用的实例中，比如 RPC，只有在 Readiness Check 通过之后，才会向服务注册中心注册，后面来自上游应用的流量才会进入。

除了中间件可以利用 Readiness Check 的事件来控制流量的进入之外，PAAS 系统也可以通过访问 `http://localhost:8080/health/readiness` 来获取应用的 Readiness Check 的状况，用来控制例如负载均衡设备等等流量的进入。

### 2.2 提供类隔离的能力

为了解决 Spring Boot 下的类依赖冲突的问题，SOFABoot 基于 SOFAArk 提供了 Spring Boot 上的类隔离的能力，在一个 SOFABoot 的系统中，只要引入 SOFAArk 相关的依赖，就可以将 SOFA 中间件相关的类和应用相关的类的 ClassLoader 进行隔离，防止出现类冲突。当然，用户也可以基于 SOFAArk，将其他的中间件、第三方的依赖和应用的类进行隔离。

### 2.3 日志空间隔离能力

为了统一大规模微服务场景下的中间件日志的打印，SOFABoot 提供了日志空间隔离的能力给 SOFA 中间件，SOFA 中间件中的各个组件采用日志空间隔离的能力之后，自动就会将本身的日志和应用的普通日志隔离开来，并且打印的日志的路径也是相对固定，非常方便进行统一地监控。

### 2.4 SOFA 中间件的集成管理

基于 Spring Boot 的自动配置能力，SOFABoot 提供了 SOFA 中间件统一易用的编程接口以及 Spring Boot 的 Starter，方便在 Spring Boot 环境下使用 SOFA 中间件，SOFA 中间件中的各个组件都是独立可插拔的，节约开发时间，和后期维护的成本。

### 2.5 模块化开发

SOFABoot 从 2.4.0 版本开始支持基于 Spring 上下文隔离的模块化开发能力，每个 SOFABoot 模块使用独立的 Spring 上下文，避免不同 SOFABoot 模块间的 BeanId 冲突，有效降低企业级多模块开发时团队间的沟通成本。

## Contribution
We love contributions! Before taking any further steps, please take a look at [Contributing to SOFABoot](./CONTRIBUTING.md).

SOFABoot is compiled under JDK 8 currently and needs [Apache Maven 3.2.5](https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/) or higher version.

## 五、感谢

SOFA 的第一个版本是阿玺创造的，感谢阿玺给 SOFA 打下了坚实地基础，也非常感谢在 SOFA 的历史中给 SOFA 贡献过代码的人们。

## 六、示例

SOFABoot 的示例工程 [sofaboot-samples](https://github.com/sofastack-guides/sofa-boot-guides/tree/master) 包含以下 demo 项目：
* [SOFABoot 示例工程](https://github.com/sofastack-guides/sofa-boot-guides/tree/master/sofaboot-sample)
* [SOFABoot 示例工程（包含类隔离能力）](https://github.com/sofastack-guides/sofa-boot-guides/blob/master/sofaboot-sample-with-isolation)
* [SOFABoot 示例工程（包含模块化开发能力）](https://github.com/sofastack-guides/sofa-boot-guides/blob/master/sofaboot-sample-with-isle)
* [SOFABoot 示例工程（使用 SOFARPC）](https://github.com/sofastack-guides/sofa-boot-guides/blob/master/sofaboot-sample-with-rpc)
* [SOFABoot 示例工程（使用定时任务）](https://github.com/sofastack-guides/sofa-boot-guides/blob/master/sofaboot-scheduler-batch-sample)
 
## 七、文档

请参考 [SOFABoot 官方文档](http://www.sofastack.tech/sofa-boot/docs/Home)。

## 八、开源许可

SOFABoot 基于 Apache License 2.0 协议，SOFABoot 依赖了一些三方组件，它们的开源协议参见 [依赖组件版权说明](https://www.sofastack.tech/projects/sofa-boot/notice/)
