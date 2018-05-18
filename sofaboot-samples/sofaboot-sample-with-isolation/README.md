# 如何使用 SOFABoot 类隔离能力

## 简介
该用例工程演示如何使用 SOFABoot 类隔离能力，阅读该文档之前，建议先了解 SOFABoot 提供的类隔离框架 [SOFAArk](https://github.com/alipay/sofa-ark)

## 工程演示
SOFABoot 提供了类隔离框架 SOFAArk，弥补了 Spring Boot 在类隔离能力上的缺失，用以解决在实际开发中常见的类冲突、包冲突问题；使用 SOFABoot 类隔离能力只需两步操作；配置 `sofa-ark-maven-plugin` 打包插件以及引入 `sofa-ark-springboot-starter` 依赖；

### 配置 Maven 打包插件
SOFABoot 官方提供了 `Maven` 插件 - `sofa-ark-maven-plugin` ，只需要简单的配置项，即可将 Spring Boot 工程打包成标准格式规范的可执行 Ark 包，插件坐标为：

```xml
<plugin>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-maven-plugin</artifactId>
</plugin>
```

配置模板如下：

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.alipay.sofa</groupId>
            <artifactId>sofa-ark-maven-plugin</artifactId>
            <executions>
                <execution>
                    <id>default-cli</id>
                    
                    <!--goal executed to generate executable-ark-jar -->
                    <goals>
                        <goal>repackage</goal>
                    </goals>
                    
                    <configuration>
                        <!--specify destination where executable-ark-jar will be saved, default saved to ${project.build.directory}-->
                        <outputDirectory>./target</outputDirectory>
                        
                        <!--default none-->
                        <arkClassifier>executable-ark</arkClassifier>
                        
                        <!-- all class exported by ark plugin would be resolved by ark biz in default, if 
                        configure denyImportClasses, then it would prefer to load them by ark biz itself -->
                        <denyImportClasses>
                            <class>com.alipay.sofa.SampleClass1</class>
                            <class>com.alipay.sofa.SampleClass2</class>
                        </denyImportClasses>
                        
                        <!-- Corresponding to denyImportClasses, denyImportPackages is package-level -->
                        <denyImportPackages>
                            <package>com.alipay.sofa</package>
                            <package>org.springframework</package>
                        </denyImportPackages>
                        
                        <!-- denyImportResources can prevent resource exported by ark plugin with accurate 
                        name to be resolved -->
                        <denyImportResources>
                            <resource>META-INF/spring/test1.xml</resource>
                            <resource>META-INF/spring/test2.xml</resource>
                        </denyImportResources>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
插件配置项解释：

+ `outputDirectory`: 执行 `mvn package` 命令后，指定打出来的 ark 包存放目录，默认存放至 ${project.build.directory}
+ `arkClassifier`: 执行 `mvn depleoy` 命令后，指定发布到仓库的 ark 包的maven坐标的 `classifer` 值, 默认为空；我们推荐配置此配置项用于和普通的 Fat Jar 加以名字上区别；
+ `denyImportClasses`: 默认情况下，应用会优先加载 ark plugin 导出的类，使用该配置项，可以禁止应用从 ark plugin 加载其导出类；
+ `denyImportPackages`: 对应上述的 `denyImportClasses`, 提供包级别的禁止导入； 
+ `denyImportResources`: 默认情况下，应用会优先加载 ark plugin 导出的资源，使用该配置项，可以禁止应用从 ark plugin 加载其导出资源；

需要指出的是：该插件配置只负责打包、发布 ark 包，如果需要在 ide 中启动 SOFAArk 容器或者在 SOFAArk 容器之上运行测试用例，需要额外添加类隔离框架依赖；

## 添加类隔离框架依赖

在实际开发中，为了在 SOFAArk 容器之上运行测试用例或者本地IDE中使用 SOFABoot 类隔离能力，需要引入如下依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

### 编写测试用例
Spring Boot 官方提供了和 JUnit4 集成的 `SpringRunner`，用于集成测试用例的编写；为了方便运行测试用例时使用 SOFABoot 类隔离能力，应用需要额外引入如下测试依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>test-sofa-boot-starter</artifactId>
</dependency>
```

SOFABoot 推荐使用 `SofaBootRunner` 替代 `SpringRunner` 编写集成测试用例，使用 `SofaJUnit4Runner` 替代 `JUnit4` 编写单元测试；这样做的好处是，只需要控制添加或者删除类隔离依赖：

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

即可完成 SOFABoot 类隔离能力的集成和剥离，使用详情如下：

#### SofaBootRunner
集成测试示例代码：

```java
@RunWith(SofaBootRunner.class)
@SpringBootTest(classes = SofaBootClassIsolationDemoApplication.class)
public class IntegrationTestCaseWithIsolation {

    @Autowired
    private SampleService sampleService;

    @Test
    public void test() {
        Assert.assertTrue("service".equals(sampleService.service()));
    }

}
```

`SofaBootRunner` 会检测应用是否引入

```xml
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

依赖；根据 Spring Boot 依赖即服务的原则，如果检测到 `sofa-ark-springboot-starter` 依赖，`SofaBootRunner` 会使用 SOFABoot 类隔离能力，否则和原生的 `SpringRunner` 无异；

#### SofaJUnit4Runner
示例代码：

```java
@RunWith(SofaJUnit4Runner.class)
public class UnitTestCaseWithIsolation {
    public static final String testClassloader = "com.alipay.sofa.ark.container.test.TestClassLoader";

    @Test
    public void test() {
        ClassLoader currentClassLoader = this.getClass().getClassLoader();
        Assert.assertTrue(currentClassLoader.getClass().getCanonicalName().equals(testClassloader));
    }
}
```

`SofaJUnit4Runner` 同样会检测应用是否引入 `sofa-ark-springboot-starter` 依赖；根据 Spring Boot 依赖即服务的原则，如果检测到 `sofa-ark-springboot-starter` 依赖，`SofaJUnit4Runner` 会使用 SOFABoot 类隔离能力，否则和原生的 `JUnit4` 无异；

#### 自定义 Runner 
在编写测试用例时，有时需要指定特殊的 Runner，为了统一编码风格，可以借助注解 `@DelegateToRunner` 配合 `SofaBootRunner` 和 `SofaJUnit4Runner` 使用，示例代码：

```java
@RunWith(SofaJUnit4Runner.class)
@DelegateToRunner(BlockJUnit4ClassRunner.class)
public class UnitTestCaseWithoutArk {

    @Test
    public void test() {
        Assert.assertFalse(true);
    }

}
```

相当于如下使用

```java
@RunWith(BlockJUnit4ClassRunner.class)
public class UnitTestCaseWithoutArk {

    @Test
    public void test() {
        Assert.assertFalse(true);
    }

}
```


