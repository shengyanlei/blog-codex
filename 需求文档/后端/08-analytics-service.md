# 统计与分析服务设计（Analytics Service）

## 1. 目标

- 收集 PV/UV、阅读时长、流量来源、互动事件等数据，提供实时与离线分析接口，并支持导出报表。

## 2. 架构

- 前端埋点 SDK → Kafka（或 Pulsar） → ClickHouse（离线） + Redis/InfluxDB（实时）。
- 后端服务负责指标聚合 API、数据导出、热榜计算。

## 3. 数据模型

- ClickHouse 表 `events_all`：
  - `event_time DateTime`, `tenant_id`, `user_id`, `session_id`, `event_type`, `entity_id`, `duration`, `referer`, `client_ip`, `ua`, `extra_json`.
- 聚合表：
  - `post_metrics_daily`：`date`, `post_id`, `pv`, `uv`, `avg_duration`, `like_count`, `comment_count`.
  - `traffic_sources_daily`：`date`, `source`, `medium`, `sessions`.

## 4. 数据管道

1. 前端调用 `POST /api/v1/analytics/track`（匿名 token）或者直接写入 Kafka。
2. 数据清洗（Flink/Spark Streaming）：过滤 bot、解析 UA、补充 Geo。
3. 写入 ClickHouse；实时指标写入 Redis（HLL 计算 UV，SortedSet 记录热度）。

## 5. API

| 功能 | Method & Path | Params | 返回 | 说明 |
| ---- | ------------- | ------ | ---- | ---- |
| 文章指标 | `GET /api/v1/analytics/posts/{id}` | `?from&to&granularity=daily/hourly` | `{pv, uv, avgDuration, trend[]}` | 支持多篇批量 |
| 热门文章 | `GET /api/v1/analytics/posts/hot` | `?range=24h` | `[PostMetricDTO]` | Redis SortedSet |
| 来源分析 | `GET /api/v1/analytics/traffic-sources` | `?from&to` | `{source:count}` | |
| 搜索词统计 | `GET /api/v1/analytics/search-terms` | `?top=20` | `[{"term":"xxx","count":100}]` | 从 Elasticsearch 日志 |
| 导出报表 | `POST /api/v1/analytics/export` | `{type, filters, format}` | `{taskId}` | 异步生成，完成后邮件通知 |

## 6. 指标计算

- 热度评分：`score = pv_weight*log(pv) + uv_weight*log(uv) + like_weight*likes + comment_weight*comments - decay`.
- UV 使用 HyperLogLog（Redis + ClickHouse `uniqState`）。
- 阅读时长：前端上报 `event=read_end` 附 `duration`，取均值/中位数。

## 7. 安全与合规

- IP、User-Agent 等个人数据按照 GDPR 进行匿名化（hash + 截断）。
- 采集的 Cookie/ID 使用同意管理；提供 `DELETE /api/v1/analytics/data-subjects/{id}` 清除个人数据。

## 8. 依赖

- Auth：映射用户信息（仅展示聚合，不暴露个人）。
- Content：获取文章 metadata。
- Notification：导出完成提醒。

## 9. 测试

- [ ] 埋点接口限流、防止滥用
- [ ] Kafka 消费延迟监控
- [ ] 热度排行榜前后端一致
- [ ] 报表导出 CSV/Excel 正确
- [ ] 数据清除接口生效
