## 快速入门

本文档将演示如何在 SOFABoot 环境下应用 SOFAIsle 进行模块化开发，您可以直接在工程下找到本文档的[示例代码](http://gitlab.alipay-inc.com/sofa-open/sofa-isle/tree/open_source/isle-sofa-boot-samples)。

SOFAIsle 一共包括四个模块：

```java
.
│
├── service-facade 
│ 
├── service-provider
│ 
├── service-consumer
│ 
└── isle-sofa-boot-run
```

各个模块的作用如下：

- service-facade: 演示JVM服务的API包；
- service-provider: 演示xml方式、annotation方式、api方式发布JVM服务；
- service-consumer: 演示xml方式、annotation方式、api方式引用JVM服务；
- isle-sofa-boot-run: 启动包含SOFAIsle模块的SOFA Boot应用。

## service-facade

service-facade 包含用于演示SOFAIsle功能的API:

```java
public interface SampleJvmService {
    void message();
}
```

## service-provider

service-provider 是一个SOFAIsle模块，用于演示xml方式、annotation方式、api方式发布JVM服务。

### 定义SOFAIsle模块

为 service-provider 模块增加 isle-module.properties 文件，将其定义为SOFAIsle模块:

```java
Module-Name=com.alipay.sofa.service-provider
```

### XML方式发布服务

实现SampleJvmService接口:

```java
public class SampleJvmServiceImpl implements SampleJvmService {
    private String message;

    @Override
    public void message() {
        System.out.println(message);
    }
															   
	// getters and setters
}
```

增加 META-INF/spring/service-provide.xml 文件，将 SampleJvmServiceImpl 发布为JVM服务:

```java
 <bean id="sampleJvmService" class="com.alipay.sofa.isle.sample.SampleJvmServiceImpl">
     <property name="message" value="Hello, jvm service xml implementation."/>
 </bean>

<sofa:service ref="sampleJvmService" interface="com.alipay.sofa.isle.sample.SampleJvmService"/>
```

### Annotation方式发布服务

实现SampleJvmService接口并增加@SofaJvmService注解:

```java
@SofaJvmService(uniqueId = "annotationImpl")
public class SampleJvmServiceAnnotationImpl implements SampleJvmService {
    @Override
    public void message() {
        System.out.println("Hello, jvm service annotation implementation.");
    }
}
```

为了区分与XML方式发布的JVM服务，注解上需要增加`uniqueId`属性。

在 META-INF/spring/service-provide.xml 文件配置Spring Bean:

```java
<bean id="sampleJvmServiceAnnotation" class="com.alipay.sofa.isle.sample.SampleJvmServiceAnnotationImpl"/>
```

### API方式发布服务

增加PublishServiceWithClient类，演示API方式发布服务:

```java
public class PublishServiceWithClient implements ClientFactoryAware {
    private ClientFactory clientFactory;

    public void init() {
        ServiceClient serviceClient = clientFactory.getClient(ServiceClient.class);
        ServiceParam serviceParam = new ServiceParam();
        serviceParam.setInstance(new SampleJvmServiceImpl(
            "Hello, jvm service service client implementation."));
        serviceParam.setInterfaceType(SampleService.class);
        serviceParam.setUniqueId("serviceClientImpl");
        serviceClient.service(serviceParam);
    }

    @Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
```

将PublishServiceWithClient配置为Spring Bean，并设置init-method，使PublishServiceWithClient在Spring刷新时发布服务:

```java
<bean id="publishServiceWithClient" class="com.alipay.sofa.isle.sample.PublishServiceWithClient" init-method="init"/>
```

## service-consumer

service-consumer 是一个SOFAIsle模块，用于演示xml方式、annotation方式、api方式引用JVM服务。

### 定义SOFAIsle模块

为 service-consumer 模块增加 isle-module.properties 文件，将其定义为SOFAIsle模块:

```java
Module-Name=com.alipay.sofa.service-consumer
Require-Module=com.alipay.sofa.service-provider
```

在isle-module.properties 文件中需要指定`Require-Module`，保证`service-provider`模块在`service-consumer`模块之前刷新。

### XML方式引用服务

增加 META-INF/spring/service-consumer.xml 文件，引用`service-provider`模块发布的服务:

```java
<sofa:reference id="sampleJvmService" interface="com.alipay.sofa.isle.sample.SampleJvmService"/>
```

### Annotation方式引用服务

定义JvmServiceConsumer类，并在其`sampleJvmServiceAnnotationImpl`属性上增加@SofaJvmReference注解: 

```java
public class JvmServiceConsumer implements ClientFactoryAware {
	@SofaJvmReference(uniqueId = "annotationImpl")
    private SampleJvmService sampleJvmServiceAnnotationImpl;
}
```

将 JvmServiceConsumer 配置成一个Spring Bean，保证@SofaJvmReference注解生效:

```java
<bean id="consumer" class="com.alipay.sofa.isle.sample.JvmServiceConsumer" init-method="init" />
```

### API方式引用服务

JvmServiceConsumer 实现ClientFactoryAware接口，并在其init方法中引用jvm服务:

```java
public class JvmServiceConsumer implements ClientFactoryAware {
    private ClientFactory    clientFactory;

    public void init() {
        ReferenceClient referenceClient = clientFactory.getClient(ReferenceClient.class);
        ReferenceParam<SampleJvmService> referenceParam = new ReferenceParam<>();
        referenceParam.setInterfaceType(SampleJvmService.class);
        referenceParam.setUniqueId("serviceClientImpl");
        SampleJvmService sampleJvmServiceClientImpl = referenceClient.reference(referenceParam);
        sampleJvmServiceClientImpl.message();
    }
															   
	@Override
    public void setClientFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }
}
```

## isle-sofa-boot-run

将模块parent配置为SOFABoot:

```java
<parent>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofaboot-dependencies</artifactId>
    <version>2.3.2</version>
</parent>
```

为模块增加`isle-sofa-boot-starter`及`service-provider`、`service-consumer`依赖:

```java
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>isle-sofa-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>service-provider</artifactId>
</dependency>
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>service-consumer</artifactId>
</dependency>
```

启动 `ApplicationRun` 类，控制台将看到以下输出:

```java
Hello, jvm service xml implementation.
Hello, jvm service annotation implementation.
Hello, jvm service service client implementation.
```