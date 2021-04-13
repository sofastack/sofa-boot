## SOFABoot

![build](https://github.com/sofastack/sofa-boot/workflows/build/badge.svg)
[![Coverage Status](https://codecov.io/gh/sofastack/sofa-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/sofastack/sofa-boot/branch/master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Percentage of issues still open")
![maven](https://img.shields.io/github/release/sofastack/sofa-boot.svg)

SOFABoot is an open source Java development framework based on Spring Boot.

Varieties of enhancements such as application readiness check, Spring context isolation, class isolation, log space separation, etc. are provided out of box.
In addition, SOFABoot accommodates SOFAStack middleware more comfortably and seamlessly for developers coming from Spring Boot world.

## Background

Spring Boot makes it easy to create stand-alone, production-grade Spring-based applications which "just runs". However, some domain-specific issues remain open:

- Spring Boot provides health indicators to reveal the liveness of application but not readiness (i.e., the capability of servicing requests).
- No built-in class isolation scheme to support finer modular applications.
- Log configurations of all SDKs used by application are repeatedly arranged.

To address the above issues while maintain the advantages of Spring Boot, Ant Group develops the SOFABoot based on Spring Boot and make it open source.
In SOFABoot, SOFAStack middleware SDKs are packaged as self-contained "starters" to provide the corresponding facet or functionality dependencies. 

## Quick Start
Please refer to SOFAStack Documentation for [SOFABoot quick start guide](https://www.sofastack.tech/en/projects/sofa-boot/quick-start/).

## Functionality
To supplement the abilities of deploying large-scale microservices in production environment for Spring Boot, SOFABoot offers following enhancements:

### Readiness Check
If request traffic reaches service instance before it is fully initialized, requests are subject to timeout or exceptions.
While Spring Boot health indicators are practical real-time exposure of application health, it doesn't help determine when services are available. 
Therefore, readiness check is an indispensable part of deployment automation in production environment and SOFABoot provides the readiness check for application out of box.
For reliable application startup, all SOFAStack middleware services won't reveal themselves (e.g., RPC services publishing to Service Registry) until readiness check passes.

Platform PAAS can also make use of the readiness check result via URL `http://localhost:8080/health/readiness` to control gracefully external traffic originating such as gateway, load balancer, etc.

### Class Isolation
Aimed to solve class or dependency conflicts, [SOFAArk](https://github.com/sofastack/sofa-ark) is created.
Compared with unwieldy OSGi class isolation implementation, SOFAArk is a light-weight scheme and focuses on the point of class loading between application and middleware modules.
Also, it is easy to make a third party SDK into SOFAArk module because the high extensibility of SOFAArk.

See further on [SOFAArk documentation](https://www.sofastack.tech/en/projects/sofa-boot/sofa-ark-readme/).

### Spring Context Isolation
Two common forms of modularization are popular in Java world:
1. Modularization based on code organization: different functional codes are organized under separate Java projects and packaged into different JARs. All Java classes are loaded by same classloader when running.
2. Modularization based on classloader: each module has its own classloader and classpath between different modules differs.

SOFABoot supplies a third option with degree of modularity between above two, which is built upon Spring Context.
Different modules owns by itself a distinct Spring Context and all contexts forms a simple dependency tree.
Bean resolution of dependency injection happens in the path up to the tree root.
It is obvious that bean and configuration conflicts are avoided between different modules, communication between teams during enterprise-level multi-module development is reduced effectively.

More details about SOFABoot modularization are introduced in this [article](https://www.sofastack.tech/posts/2018-07-25-01).

### Unified Logging
In Spring Boot, the responsibility of log configurations is left to users: all the users of a certain SDK need to configure for its logging, but the configurations are basically the same in most cases.
In order to save the repeated configuration, SOFABoot utilizes the [sofa-common-tools](https://github.com/sofastack/sofa-common-tools) to provide for each SDKs basic log configurations.
Users don't need to worry about SDk logging anymore.
Besides, every SDK has a different logging directory to separate it from application logs to assist handy monitoring based on logging. 

### Built-in SOFAStack Middlewares
Based on the auto-configuring and dependencies descriptor (aka starter) in Spring Boot, SOFABoot offers easy-to-use programming interface for all SOFAStack middlewares.
All of them are packaged as self-contained "starters" to provide the corresponding facet dependencies and are independently pluggable.

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
