## SOFABoot

![build](https://github.com/sofastack/sofa-boot/workflows/build/badge.svg)
[![Coverage Status](https://codecov.io/gh/sofastack/sofa-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/sofastack/sofa-boot/branch/master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Percentage of issues still open")
![maven](https://img.shields.io/github/release/sofastack/sofa-boot.svg)

SOFABoot 是蚂蚁集团开源的基于 Spring Boot 的研发框架，它在 Spring Boot 的基础上，提供了诸如 Readiness Check，上下文隔离，类隔离，日志空间隔离等等能力。在增强了 Spring Boot 的同时，SOFABoot 提供了让用户可以在 Spring Boot 中非常方便地使用 SOFA 中间件的能力。

## 一、背景

Spring Boot 是一个非常优秀的开源框架，可以非常方便地就构建出一个基于 Spring 的应用程序，但是在使用过程中，还是会遇到一些问题：

* Spring Boot 提供了一个基础的健康检查的能力，中间件和应用都可以扩展来实现自己的健康检查逻辑。但是 Spring Boot 的健康检查只有 Liveness Check 的能力，缺少 Readiness Check 的能力，这样会有比较致命的问题。当一个微服务应用启动的时候，必须要先保证启动后应用是健康的，才可以将上游的流量放进来（来自于 RPC，网关，定时任务等等流量），否则就可能会导致一定时间内大量的错误发生。
* Spring Boot 虽然通过依赖管理（Dependency Management）的方式最大程度的保证了 Spring Boot 管理的 JAR 包之间的兼容性，但是不可避免的，当引入一些其他的 JAR 包的时候，还是可能会遇到冲突，而且很多时候这种冲突解决起来并不是这么容易，一个例子是当冲突的包是序列化相关的类库时，比如说 Hessian，如果应用中的一个组件需要使用 Hessian 3，而另一个则必须要使用 Hessian 4，由于 Hessian 3 和 Hessian 4 之间的不兼容性，并且序列化还涉及到微服务中的上下游服务，要把 Hessian 统一到一个版本绝非易事。
* 在超大规模微服务运维的场景下，运维能力的平台化是一定要解决的问题，而监控又是其中非常主要的一个点，针对于日志监控这种情况，Spring Boot 并没有提供任何解决方案。大部分的开源组件，具体要打印哪些日志，打印到什么路径，什么文件下面，都是由应用的使用者来决定，这样会导致每一个应用的日志配置都各式各样，每一个应用都需要去监控系统中配置自己应用的日志监控，导致关键的监控的实施成本特别高。
* 在企业级应用场景，模块化开发是解决多团队沟通成本的有效解决方案，每个业务团队专注于开发自己的应用模块，每个模块自包含，便于开发及自测，减少团队间的沟通成本。但是 Spring Boot 默认不支持模块化开发，所有 Bean 共用一个 Spring 上下文，在多团队开发时，如果不同团队定义了相同 BeanId，运行时将出现 BeanId 冲突错误。

为了解决以上的问题，又因为 SOFA 中间件中的各个组件本身就需要集成 Spring Boot，所以蚂蚁集团基于 Spring Boot 开发并开源了 SOFABoot，来解决以上的问题，也方便使用者在 Spring Boot 中方便地去使用 SOFA 中间件。

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

SOFABoot 的编译环境的要求为 JDK8，需要采用 [Apache Maven 3.2.5](https://archive.apache.org/dist/maven/maven-3/3.2.5/binaries/) 或者更高的版本进行编译。

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

## 九、已知用户
此处列出了已知在生产环境使用了 SOFAStack 全部或者部分组件的公司或组织，大家可以通过 [SOFAStack 使用者登记](https://github.com/sofastack/sofastack.tech/issues/5)进行登记。
以下排名不分先后：
<div>
<img alt="蚂蚁集团" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*aK79TJUJykkAAAAAAAAAAAAAARQnAQ" height="60" />
<img alt="网商银行" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*uAmFRZQ0Z4YAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="恒生电子" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*_iGLRq0Ih-IAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="数立信息" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*JAgIRpjz-IgAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="Paytm" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*a0fvTKJ1Xv8AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="天弘基金" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*99OQT7lDBsMAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="中国人保" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*P1BARJxwv1sAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="信美相互" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*jAzWQpIgFUAAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="南京银行" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*q9PMQI7hs8sAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="民生银行" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*GnUuSKmOtS4AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="重庆农商行" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*FKrxSYhdi2wAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="中信证券" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*t-xbQb3WSjcAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="富滇银行" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*JCDYT6u6_asAAAAAAAAAAAAAARQnAQ" height="60" />
<img alt="挖财" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*lVrVT4dpSDEAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="拍拍贷" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*TAePS6j56LsAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="OPPO金融" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*mU40QaJkwZYAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="运满满" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*_kBbQYUYdIQAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="译筑科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*wuKSTpZSEkEAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="杭州米雅信息科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*b-o5SITMKu0AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="邦道科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*nsw1S5bt9DkAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="申通快递" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*C3ncSpDsjS8AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="深圳大头兄弟文化" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*8AYmRowxSC0AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="烽火科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*MjuuT4omCrwAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="亚信科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*vBBIRomYoEUAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="成都云智天下科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*p0OkQbC5RvsAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="上海溢米辅导" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*mJdtTJsn1PwAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="态赋科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*sfLDRL5TJx8AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="风一科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*EGeMR4qprnkAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="武汉易企盈" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*31WRQ7zg3HIAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="极致医疗" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*cPOiS5q8NCwAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="京东" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*INhuS44qO8YAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="小象生鲜" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*K5ERQYbCRBgAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="北京云族佳" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*qzxjSZ2tlmIAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="欣亿云网" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*huOKQKvoLzwAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="山东网聪" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*INUFR7XIH1gAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="深圳市诺安赛威" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*eVGbR7RhDDkAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="上扬软件" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*VsqMT7n7p0AAAAAAAAAAAABjARQnAQ" height="60" />
<img alt="长沙点三" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*2eEzSqdPIc0AAAAAAAAAAABjARQnAQ" height="60" />
<img alt="网易云音乐" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*66KbQ6seDqoAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="虎牙直播" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*uzr3RLUZ3RwAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="中国移动" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*vEo-T55XTOAAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="无纸科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*9aFQSLfyPhMAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="黄金钱包" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*tYZJRpANxNoAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="独木桥网络" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*GW6oTLIlAbcAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="wueasy" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*4uFWQacI-RwAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="北京攸乐科技" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*CD5VT50FXqMAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="易宝支付" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*oy0ZSquXXjAAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="威马汽车" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*LPf2TbTeJPwAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="亿通国际" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*tlq4R7QqUaEAAAAAAAAAAABkARQnAQ" height="60" />
<img alt="新华三" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*gw9uTbZvsbAAAAAAAAAAAAAAARQnAQ" height="60" />
<img alt="klilalagroup" src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*0cskToqBSi8AAAAAAAAAAAAAARQnAQ" height="60" />
</div>

