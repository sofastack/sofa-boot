# 如何在 SOFABoot 工程集成 SOFAArk 类隔离能力

## 简介
该用例工程演示如何在 SOFABoot 工程中集成 SOFAArk 类隔离能力，以及如何编写运行在 SOFAArk 容器的测试用例；阅读该文档之前，建议先了解 [SOFAArk](https://github.com/alipay/sofa-ark)

## 工程演示
### 集成 SOFAArk 类隔离能力
一个普通的 SOFABoot 工程集成 SOFAArk 类隔离能力，只需要配置 `sofa-ark-maven-plugin` 插件即可，例如：

```java
build>
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
+ `outputDirectory`: 指定执行 `mvn package` 命令后，打出来的 ark 包存放目录，默认存放至 ${project.build.directory}
+ `arkClassifier`: 指定执行 `mvn depleoy` 命令后，发布到仓库的 ark 包的maven坐标的 `classifer` 值, 默认为空
+ `denyImportClasses`: 默认情况下，应用会优先加载 ark plugin 导出的类，使用该配置项，可以禁止应用从 ark plugin 加载其导出类；
+ `denyImportPackages`: 对应上述的 `denyImportClasses`, 提供包级别的禁止导入； 
+ `denyImportResources`: 默认情况下，应用会优先加载 ark plugin 导出的资源，使用该配置项，可以禁止应用从 ark plugin 加载其导出资源；

需要指出的是：该插件配置只负责打包、发布 ark 包，如果需要在 ide 中让 SpringBoot 应用在 SOFAArk 容器之上启动或者在 SOFAArk 容器之上运行测试用例，需要额外添加依赖；

### 打包、发布、安装 
配置 `sofa-ark-maven-plugin` 后，打包、安装、发布和普通的工程没有区别；唯一需要主要的是，插件提供了配置项 `arkClassifier` 来设置 ark 包 maven 坐标的 classifier，默认为空；

### 在 IDE 中启动 SOFAArk 容器
上面提到 `ark-maven-plugin` 插件只负责打包、发布 ark 包，如果需要在 ide 启动 SOFAArk 容器或者在 SOFAArk 容器之上运行测试用例，需要额外添加如下依赖：

```java
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

### 编写测试用例
SpringBoot 官方提供了和 JUnit4 集成的 `SpringRunner`，用于集成测试用例的编写；为了方便在 SOFAArk 容器之上运行测试用例，应用需要额外引入如下依赖：

```java
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>test-sofa-boot-starter</artifactId>
</dependency>
```

SOFABoot 推荐使用 `SofaBootRunner` 替代 `SpringRunner` 编写集成测试用例，使用 `SofaJUnit4Runner` 替代 `JUnit4` 编写单元测试；这样做的好处是，只需要控制添加或者删除依赖：

```java
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

即可完成和 SOFAArk 框架的集成和剥离。当然你可以直接使用原生的 `SpringRunner`，这样做的缺点是，如果需要让应用集成 SOFAArk 框架，不仅需要引入额外的依赖，为了让测试用例运行在 SOFAArk 容器之上，还需要更改测试用例代码。因此为了快速地完成应用和 SOFAArk 框架的集成和剥离，建议使用 `SofaBootRunner` 和 `SofaJUnit4Runner` 编写 SOFABoot 应用的测试用例；

#### SofaBootRunner
示例代码：

```java
@RunWith(SofaBootRunner.class)
@SpringBootTest(classes = SofaBootClassIsolationDemoApplication.class)
public class IntegrationTestCase {

    @Autowired
    private SampleService sampleService;

    @Test
    public void test() {
        Assert.assertTrue("service".equals(sampleService.service()));
    }

}
```

`SofaBootRunner` 会检测应用是否引入

```java
<dependency>
    <groupId>com.alipay.sofa</groupId>
    <artifactId>sofa-ark-springboot-starter</artifactId>
</dependency>
```

依赖；根据 SpringBoot 依赖即服务的原则，如果检测到 `sofa-ark-springboot-starter` 依赖，`SofaBootRunner` 会启动 SOFAArk 容器，否则和原生的 `SpringRunner` 无异；

#### SofaJUnit4Runner
示例代码：

```java
@RunWith(SofaJUnit4Runner.class)
public class UnitTestCase {

    @Test
    public void test() {
        Assert.assertTrue(true);
    }
}
```

`SofaJUnit4Runner` 同样会检测应用是否引入 `sofa-ark-springboot-starter` 依赖；根据 SpringBoot 依赖即服务的原则，如果检测到 `sofa-ark-springboot-starter` 依赖，`SofaJUnit4Runner` 会启动 SOFAArk 容器，否则和原生的 `JUnit4` 无异；

#### 自定义 Runner 
在编写测试用例时，有时需要指定特殊的 Runner，此时需要借助注解 `@DelegateToRunner` 配合 `SofaBootRunner` 和 `SofaJUnit4Runner` 使用，示例代码：

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



