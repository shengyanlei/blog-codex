# Codex Blog Backend Framework

该目录包含基于 Java 11 + Spring Boot 3 的后端多模块骨架，映射架构中的各领域服务。整体采用 Maven 多模块结构，通过父子 POM 统一依赖管理、代码规范与 CI 流程。

## 模块一览

| 模块                | 说明                                 |
| ------------------- | ------------------------------------ |
| `common`            | 通用工具、DTO、错误码、基础配置       |
| `gateway`           | API Gateway / BFF，实现聚合接口       |
| `auth-service`      | 账号与权限                           |
| `content-service`   | 文章与内容管理                       |
| `media-service`     | 媒体上传/处理                        |
| `interaction-service` | 评论、点赞、收藏                    |
| `notification-service` | 通知与订阅                        |
| `seo-service`       | SEO 与分享                           |
| `analytics-service` | 统计分析 API                         |
| `admin-service`     | 站点配置、主题、插件管理             |
| `plugin-service`    | 插件生态运行时/Hook                  |
| `admin-ui`          | 纯静态管理端 Demo（HTML/JS）         |

## 技术栈

- Java 11, Spring Boot 3.3.x, Spring Cloud 2023.x
- Maven 3.9+, JUnit 5, Testcontainers
- MySQL 8.0, Redis 7, RabbitMQ, Elasticsearch, ClickHouse

## 快速开始

```bash
cd backend
mvn clean install
```

各服务可通过 Spring Boot 插件启动（示例：`mvn -pl auth-service spring-boot:run`）。

详细接口定义见 `api-spec.md`。
