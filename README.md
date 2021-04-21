## SOFABoot

![build](https://github.com/sofastack/sofa-boot/workflows/build/badge.svg)
[![Coverage Status](https://codecov.io/gh/sofastack/sofa-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/sofastack/sofa-boot/branch/master)
![license](https://img.shields.io/badge/license-Apache--2.0-green.svg)
[![Average time to resolve an issue](http://isitmaintained.com/badge/resolution/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Average time to resolve an issue")
[![Percentage of issues still open](http://isitmaintained.com/badge/open/sofastack/sofa-boot.svg)](http://isitmaintained.com/project/sofastack/sofa-boot "Percentage of issues still open")
![maven](https://img.shields.io/github/release/sofastack/sofa-boot.svg)

[中文版本](./README_ZH.md)

SOFABoot is an open source Java development framework based on Spring Boot.

Varieties of enhancements such as application readiness check, Spring context isolation, class isolation, log space separation, etc. are provided out of box.
In addition, SOFABoot accommodates SOFAStack middlewares more comfortably and seamlessly for developers coming from Spring Boot world.

## Background

Spring Boot makes it easy to create stand-alone, production-grade Spring-based applications which "just run". However, some domain-specific issues remain open:

- Spring Boot provides health indicators to reveal the liveness of application but not readiness (aka the capability of servicing requests).
- No built-in class isolation scheme to support finer modular applications.
- Log configurations of all SDKs used by application are repeatedly arranged.

To address the above issues while maintaining the advantages of Spring Boot, Ant Group develops the SOFABoot based on Spring Boot and make it open source.
In SOFABoot, SOFAStack middleware SDKs are packaged as self-contained "starters" to provide the corresponding facet or functionality dependencies. 

## Quick Start
Please refer to SOFAStack Documentation for [SOFABoot quick start guide](https://www.sofastack.tech/en/projects/sofa-boot/quick-start/).

### Demos
Some SOFABoot demo projects to get your hands dirty:
- [Class Isolation](https://github.com/sofastack-guides/sofa-boot-guides/tree/master/sofaboot-sample-with-isolation)
- [Spring Context Isolation](https://github.com/sofastack-guides/sofa-boot-guides/tree/master/sofaboot-sample-with-isolation)
- [SOFA RPC](https://github.com/sofastack-guides/sofa-boot-guides/tree/master/sofaboot-sample-with-rpc)
- [Scheduler with Batch](https://github.com/sofastack-guides/sofa-boot-guides/tree/master/sofaboot-scheduler-batch-sample)

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

### Community
See our community [materials](https://github.com/sofastack/community/blob/master/ROLES-EN.md).

Scan the QR code below with DingTalk(钉钉) to join the SOFAStack user group.
<p align="center">
<img src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*1DklS7SZFNMAAAAAAAAAAAAAARQnAQ" width="200">
</p>

Scan the QR code below with WeChat(微信) to Follow our Official Accounts.
<p align="center">
<img src="https://gw.alipayobjects.com/mdn/sofastack/afts/img/A*LVCnR6KtEfEAAAAAAAAAAABjARQnAQ" width="222">
</p>



## Acknowledgements
The first version of SOFA is created by Felix(阿玺), lots of thanks are given to Felix for laying a solid foundation for SOFA.
It is also very grateful to the people who have contributed codes in the history of SOFA.

## License
Ant Group SOFABoot is distributed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html).
The licenses of third parity dependencies of SOFABoot are explained [here](https://www.sofastack.tech/projects/sofa-boot/notice/).

## Known Users
The SOFABoot users (the names are in no particular order). Please leave a comment [here](https://github.com/sofastack/sofastack.tech/issues/5) to tell us your scenario to make SOFABoot better.
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
