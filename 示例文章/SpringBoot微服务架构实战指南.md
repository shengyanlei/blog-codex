# Spring Boot 微服务架构实战指南

微服务架构已成为现代企业级应用的主流选择。本文将基于 Spring Boot 和 Spring Cloud，详细介绍如何构建一个完整的微服务系统。

## 1. 微服务架构概述

### 1.1 什么是微服务

微服务是一种架构风格，将单一应用程序开发为一组小型服务，每个服务运行在自己的进程中，通过轻量级机制（通常是 HTTP REST API）进行通信。

### 1.2 微服务的优势

- **独立部署**: 每个服务可以独立部署和扩展
- **技术异构性**: 不同服务可以使用不同的技术栈
- **故障隔离**: 单个服务的故障不会影响整个系统
- **团队自治**: 小团队可以独立负责一个服务

## 2. 技术栈选择

### 2.1 核心框架

- **Spring Boot 3.0**: 简化 Spring 应用开发
- **Spring Cloud 2022**: 微服务治理框架
- **Spring Cloud Gateway**: API 网关
- **Spring Cloud OpenFeign**: 声明式 HTTP 客户端

### 2.2 服务注册与发现

使用 Nacos 或 Consul 作为服务注册中心：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: dev
```

## 3. 服务拆分原则

### 3.1 按业务能力拆分

```
用户服务 (User Service)
├── 用户注册
├── 用户登录
└── 用户信息管理

订单服务 (Order Service)
├── 创建订单
├── 订单查询
└── 订单状态管理

商品服务 (Product Service)
├── 商品管理
├── 库存管理
└── 商品查询
```

### 3.2 数据库设计

每个微服务应该有自己独立的数据库：

```sql
-- 用户服务数据库
CREATE DATABASE user_service;

-- 订单服务数据库
CREATE DATABASE order_service;

-- 商品服务数据库
CREATE DATABASE product_service;
```

## 4. API 网关设计

### 4.1 Gateway 配置

```java
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user_route", r -> r.path("/api/users/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://user-service"))
            .route("order_route", r -> r.path("/api/orders/**")
                .filters(f -> f.stripPrefix(1))
                .uri("lb://order-service"))
            .build();
    }
}
```

### 4.2 统一认证

使用 JWT 实现统一认证：

```java
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (!validateToken(token)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        return chain.filter(exchange);
    }
    
    @Override
    public int getOrder() {
        return -100;
    }
}
```

## 5. 服务间通信

### 5.1 使用 OpenFeign

```java
@FeignClient(name = "user-service")
public interface UserClient {
    
    @GetMapping("/users/{id}")
    UserDTO getUserById(@PathVariable("id") Long id);
    
    @PostMapping("/users")
    UserDTO createUser(@RequestBody UserDTO user);
}
```

### 5.2 服务调用示例

```java
@Service
public class OrderService {
    
    @Autowired
    private UserClient userClient;
    
    public OrderDTO createOrder(OrderDTO order) {
        // 调用用户服务验证用户
        UserDTO user = userClient.getUserById(order.getUserId());
        
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        
        // 创建订单逻辑
        return saveOrder(order);
    }
}
```

## 6. 分布式事务

### 6.1 Seata 集成

```yaml
seata:
  enabled: true
  application-id: order-service
  tx-service-group: my_tx_group
  service:
    vgroup-mapping:
      my_tx_group: default
```

### 6.2 使用 @GlobalTransactional

```java
@Service
public class OrderService {
    
    @GlobalTransactional
    public void createOrder(OrderDTO order) {
        // 1. 创建订单
        orderRepository.save(order);
        
        // 2. 扣减库存（调用商品服务）
        productClient.decreaseStock(order.getProductId(), order.getQuantity());
        
        // 3. 扣减余额（调用用户服务）
        userClient.decreaseBalance(order.getUserId(), order.getAmount());
    }
}
```

## 7. 配置管理

### 7.1 使用 Nacos Config

```yaml
spring:
  cloud:
    nacos:
      config:
        server-addr: localhost:8848
        file-extension: yaml
        namespace: dev
        group: DEFAULT_GROUP
```

### 7.2 动态配置刷新

```java
@RefreshScope
@RestController
public class ConfigController {
    
    @Value("${app.message:default}")
    private String message;
    
    @GetMapping("/config")
    public String getConfig() {
        return message;
    }
}
```

## 8. 监控与链路追踪

### 8.1 Spring Boot Actuator

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 8.2 Sleuth + Zipkin

```yaml
spring:
  sleuth:
    sampler:
      probability: 1.0
  zipkin:
    base-url: http://localhost:9411
```

## 9. 容器化部署

### 9.1 Dockerfile

```dockerfile
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 9.2 Docker Compose

```yaml
version: '3.8'
services:
  gateway:
    build: ./gateway
    ports:
      - "8080:8080"
    depends_on:
      - user-service
      - order-service
  
  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
  
  order-service:
    build: ./order-service
    ports:
      - "8082:8082"
```

## 10. 最佳实践

1. **服务粒度**: 不要过度拆分，保持合理的服务粒度
2. **API 版本管理**: 使用版本号管理 API 变更
3. **熔断降级**: 使用 Sentinel 实现服务保护
4. **日志聚合**: 使用 ELK 统一收集日志
5. **持续集成**: 建立 CI/CD 流水线

## 总结

构建微服务架构是一个系统工程，需要考虑服务拆分、通信、事务、配置、监控等多个方面。Spring Cloud 生态提供了完整的解决方案，但也需要根据实际业务场景进行调整和优化。

---

**标签**: Spring Boot, 微服务, Spring Cloud, 后端架构
**分类**: 架构设计
**阅读时间**: 15 分钟
