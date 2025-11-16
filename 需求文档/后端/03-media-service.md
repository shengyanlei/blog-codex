# 媒体管理服务设计（Media Service）

## 1. 模块目标

- 提供统一的文件上传、存储、处理（压缩/裁剪/转码）与引用统计，支持 S3 兼容存储、CDN、防盗链。

## 2. 能力范围

- 上传（表单/分片/直传）、下载授权、媒体库检索、图像处理、外链监控、清理策略。
- 业务侧仅存储文件元信息，实际文件存对象存储（MinIO/OSS/COS）。

## 3. 数据模型

- `media_assets`: `id`, `tenant_id`, `original_name`, `mime_type`, `size`, `hash`, `storage_bucket`, `storage_key`, `width`, `height`, `duration`, `uploader_id`, `status`, `ref_count`, `created_at`
- `media_derivatives`: 派生版本（缩略图、压缩包）`id`, `media_id`, `type`, `storage_key`
- `media_links`: 记录媒体被文章/评论引用情况（`media_id`, `entity_type`, `entity_id`）

索引：`hash` 唯一（去重）；`tenant_id + created_at`；`uploader_id`。

## 4. 上传流程

1. 客户端请求上传凭证：`POST /api/v1/media/upload-policy`，传递 `type`, `size`。
2. 服务端校验大小/MIME → 生成临时 AK/SK 或预签名 URL → 返回直传信息。
3. 客户端直传对象存储 → 回调 `POST /api/v1/media/confirm` 通知完成。
4. Media Service 校验 hash，如果重复则复用已有资源，并增加引用。

## 5. 处理任务

- 触发器：上传完成事件写入 MQ `media.uploaded`。
- Worker（Java + Spring Batch）读取任务，执行：
  - 图片压缩（ImageMagick）→ 生成不同规格 → 写 `media_derivatives`
  - 视频转码（FFmpeg）→ 输出 HLS/MP4
  - 图片审核（AI）→ 不通过则标记 `status=blocked` 并发通知

## 6. API

| 功能 | Method & Path | Request | Response | 说明 |
| ---- | ------------- | ------- | -------- | ---- |
| 获取上传策略 | `POST /api/v1/media/upload-policy` | `{fileName, mimeType, size}` | `{uploadId, url, headers, expireAt}` | 需鉴权 |
| 上传完成确认 | `POST /api/v1/media/confirm` | `{uploadId, hash, width?, height?}` | `{mediaId}` | 包含客户端计算的 hash |
| 查询媒体 | `GET /api/v1/media` | `?page&size&keyword&tag` | `Paged<Media>` | 支持标签过滤 |
| 删除媒体 | `DELETE /api/v1/media/{id}` |  | `{status}` | 若 `ref_count>0` 则标记为 `soft_deleted` |
| 获取临时访问 URL | `GET /api/v1/media/{id}/access-url` | `?style=thumb` | `{url, expireAt}` | 生成带签名链接 |

## 7. 防盗链与安全

- 访问 URL 默认 5 分钟过期，采用 S3 v4 签名；可配置 Referer 白名单。
- 上传大小限制：图片 < 20MB、视频 < 500MB，可配置。
- 病毒扫描：集成 ClamAV，在异步任务中进行。

## 8. 依赖

- Auth Service：鉴权、获取 uploader 信息。
- Content/Interaction：通过 `media_links` 记录引用；删除实体时回调减少引用计数。
- Notification：违规内容时发告警。

## 9. 测试

- [ ] 分片上传失败重传
- [ ] hash 冲突检测
- [ ] 生成不同规格的图片 URL
- [ ] 删除时引用计数校验
- [ ] 防盗链配置（无 Referer、黑名单）验证
