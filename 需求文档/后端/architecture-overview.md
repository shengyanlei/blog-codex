# Codex Blog 后端整体架构图

```mermaid
flowchart LR
    subgraph Client
        Web[Web/PWA]
        Mobile[移动端]
        Plugin[插件]
    end

    Client -->|REST/GraphQL/WebSocket| APIGW[API Gateway / BFF]

    subgraph Core Services
        Auth[Auth Service\n(Spring Boot + Security)]
        Content[Content Service]
        Media[Media Service]
        Interaction[Interaction Service]
        Notification[Notification Service]
        SEO[SEO Service]
        Analytics[Analytics Service]
        Admin[Admin & Config]
        PluginSvc[Plugin Ecosystem]
    end

    APIGW --> Auth
    APIGW --> Content
    APIGW --> Media
    APIGW --> Interaction
    APIGW --> Notification
    APIGW --> SEO
    APIGW --> Analytics
    APIGW --> Admin
    APIGW --> PluginSvc

    Auth -- JWT/OAuth Events --> Notification
    Content -- 发布事件 --> SEO
    Content -- 发布事件 --> Notification
    Content -- 指标 --> Analytics
    Interaction -- 评论事件 --> Notification
    Interaction -- 点赞数据 --> Analytics
    Media -- 引用信息 --> Content
    Admin -- 配置 --> APIGW
    PluginSvc -- Hook --> Content

    subgraph Data Layer
        MySQL[(MySQL 8.0\n主从/分库)]
        Redis[(Redis 7)]
        MQ[(RabbitMQ/Kafka)]
        ObjectStore[(S3 兼容对象存储)]
        ES[(Elasticsearch)]
        ClickHouse[(ClickHouse)]
    end

    Core Services --> MySQL
    Core Services --> Redis
    Core Services --> MQ
    Media --> ObjectStore
    SEO --> ObjectStore
    Content --> ES
    Analytics --> ClickHouse

    subgraph Observability
        Prometheus
        Grafana
        ELK
        Jaeger
    end

    Core Services --> Prometheus
    Core Services --> ELK
    Core Services --> Jaeger
    Prometheus --> Grafana
```

## 说明

- **Gateway/BFF**：统一鉴权、限流、缓存、灰度，向终端提供REST/GraphQL。
- **Core Services**：按照领域拆分，支持独立部署与扩展。
- **Data Layer**：MySQL 存核心数据，Redis 用于缓存/会话/限流，MQ 解耦异步任务，ES/ClickHouse 分别覆盖搜索与统计。
- **Observability**：Prometheus & Grafana 监控指标，ELK 处理日志，Jaeger 追踪调用链。
