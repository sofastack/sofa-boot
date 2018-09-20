## 快速入门
本文档旨在演示如何在 SOFABoot 多模块中使用数据源，使用 h2database 内存数据库，执行了简单插入、查询、删除的数据库操作。项目的目录结构划分如下：
```text
app
│
├── biz
│   ├── service-impl (模块)
│   └── shared (模块)
│
├── common 
│   ├── dal (模块)
│   └── service 
│       └── facade (模块)
│ 
├── test (模块)
│ 
└── web (模块)
```

上图中未标注 `(模块)` 均为目录，各个模块的作用如下：
- facade (模块)：定义了 `NewsReadService` 和 `NewsWriteService` 两个 JVM 服务接口
- dal (模块)：定义了数据源，并发布了 `DataSource` 和 `INewsManageDao` JVM服务
- service-impl (模块): 实现了 `NewsWriteService` 服务接口，并将 dal(模块) 设置为 Parent，通过 Spring 依赖注入的方式引用了 `INewsManageDao`
- shared (模块): 实现了 `NewsReadService` 服务接口，使用 JVM 服务的方式引用了 `INewsManageDao`

在这里不详述如何发布引用 JVM 服务，可以参见其他演示工程。这里演示如何运行该 Demo. 在该工程中暴露了4个 Rest 服务：
- `localhost:8080/create`：在 h2database 创建一张新闻表，为了简单演示，新闻只包含作者和标题信息。需要注意一点，启动应用之后，首次需要访问这个服务。可以通过 `http://localhost:8080/h2-console` 查看 h2database 当前状态。(登入名：sofa, 密码：123456)
- `localhost:8080/insert/{author}/{title}`: 插入新闻纪录，例如 `localhost:8080/insert/zhangsan/如何先挣一个亿`
- `localhost:8080/delete/{author}`: 删除指定作者的所有新闻纪录，例如 `localhost:8080/delete/zhangsan`
- `localhost:8080/query/{author}`: 查询指定作者的所有新闻纪录，例如 `localhost:8080/query/zhangsan`
