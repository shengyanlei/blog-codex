package com.codex.blog.notification.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.notification.domain.Notification;
import com.codex.blog.notification.service.NotificationService;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Notification>> send(@Valid @RequestBody SendNotificationRequest request) {
        Notification notification = new Notification();
        notification.setRecipientId(request.getRecipientId());
        notification.setChannel(request.getChannel());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        return ResponseEntity.ok(ApiResponse.success(notificationService.send(notification)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> list(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.list(userId)));
    }

    public static class SendNotificationRequest {
        @NotBlank
        private String recipientId;
        @NotBlank
        private String channel;
        @NotBlank
        private String subject;
        @NotBlank
        private String content;

        public String getRecipientId() {
            return recipientId;
        }

        public void setRecipientId(String recipientId) {
            this.recipientId = recipientId;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}

