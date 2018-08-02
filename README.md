## SOFABoot

[![Build Status](https://travis-ci.org/alipay/sofa-boot.svg?branch=master)](https://travis-ci.org/alipay/sofa-boot)
[![Coverage Status](https://coveralls.io/repos/github/alipay/sofa-boot/badge.svg?branch=master)](https://coveralls.io/github/alipay/sofa-boot?branch=master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
![maven](https://img.shields.io/github/release/alipay/sofa-boot.svg)

SOFABoot 是蚂蚁金服开源的基于 Spring Boot 的研发框架，它在 Spring Boot 的基础上，提供了诸如 Readiness Check，类隔离，日志空间隔离等等能力。在增强了 Spring Boot 的同时，SOFABoot 提供了让用户可以在 Spring Boot 中非常方便地使用 SOFA 中间件的能力。

## 3.0.0 SNAPSHOT ! 🎉🎉🎉
为了方便社区同学能够基于 SOFABoot 使用 Spring Boot 2.0 进行开发，我们拉了 3.0.0-SNAPSHOT 快照分支，该版本是基于 Spring Boot 2.0.3.RELEASE。

## 一、背景

Spring Boot 是一个非常优秀的开源框架，可以非常方便地就构建出一个基于 Spring 的应用程序，但是在使用过程中，还是会遇到一些问题：

* Spring Boot 提供了一个基础的健康检查的能力，中间件和应用都可以扩展来实现自己的健康检查逻辑。但是 Spring Boot 的健康检查只有 Liveness Check 的能力，缺少 Readiness Check 的能力，这样会有比较致命的问题。当一个微服务应用启动的时候，必须要先保证启动后应用是健康的，才可以将上游的流量放进来（来自于 RPC，网关，定时任务等等流量），否则就可能会导致一定时间内大量的错误发生。
* Spring Boot 虽然通过依赖管理（Dependency Management）的方式最大程度的保证了 Spring Boot 管理的 JAR 包之间的兼容性，但是不可避免的，当引入一些其他的 JAR 包的时候，还是可能会遇到冲突，而且很多时候这种冲突解决起来并不是这么容易，一个例子是当冲突的包是序列化相关的类库时，比如说 Hessian，如果应用中的一个组件需要使用 Hessian 3，而另一个则必须要使用 Hessian 4，由于 Hessian 3 和 Hessian 4 之间的不兼容性，并且序列化还涉及到微服务中的上下游服务，要把 Hessian 统一到一个版本绝非易事。
* 在超大规模微服务运维的场景下，运维能力的平台化是一定要解决的问题，而监控又是其中非常主要的一个点，针对于日志监控这种情况，Spring Boot 并没有提供任何解决方案。大部分的开源组件，具体要打印哪些日志，打印到什么路径，什么文件下面，都是由应用的使用者来决定，这样会导致每一个应用的日志配置都各式各样，每一个应用都需要去监控系统中配置自己应用的日志监控，导致关键的监控的实施成本特别高。
* 在企业级应用场景，模块化开发是解决多团队沟通成本的有效解决方案，每个业务团队专注于开发自己的应用模块，每个模块自包含，便于开发及自测，减少团队间的沟通成本。但是 Spring Boot 默认不支持模块化开发，所有 Bean 共用一个 Spring 上下文，在多团队开发时，如果不同团队定义了相同 BeanId，运行时将出现 BeanId 冲突错误。

为了解决以上的问题，又因为 SOFA 中间件中的各个组件本身就需要集成 Spring Boot，所以蚂蚁金服基于 Spring Boot 开发并开源了 SOFABoot，来解决以上的问题，也方便使用者在 Spring Boot 中方便地去使用 SOFA 中间件。

## 二、功能简介

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

## 三、快速开始

请查看文档中的[快速开始](http://www.sofastack.tech/sofa-boot/docs/QuickStart)来了解如何快速上手使用 SOFABoot。

## 四、如何贡献

在贡献代码之前，请阅读[如何贡献](./CONTRIBUTING.md)来了解如何向 SOFABoot 贡献代码。

SOFABoot 的编译环境的要求为 JDK7 或者 JDK8，需要采用 [Apache Maven 3.2.5](https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/) 或者更高的版本进行编译。

## 五、感谢

SOFA 的第一个版本是阿玺创造的，感谢阿玺给 SOFA 打下了坚实地基础，也非常感谢在 SOFA 的历史中给 SOFA 贡献过代码的人们。

## 六、示例

在此工程的 `sofaboot-samples` 目录下的是 SOFABoot 的示例工程，分别为：

* [SOFABoot 示例工程](./sofaboot-samples/sofaboot-sample)
* [SOFABoot 示例工程（包含类隔离能力）](./sofaboot-samples/sofaboot-sample-with-isolation)
* [SOFABoot 示例工程（包含模块化开发能力）](./sofaboot-samples/sofaboot-sample-with-isle)
* [SOFABoot 示例工程（使用 SOFARPC）](./sofaboot-samples/sofaboot-sample-with-rpc)
 
## 七、文档

请参考 [SOFABoot 官方文档](http://www.sofastack.tech/sofa-boot/docs/Home)。
