package com.codex.blog.notification.service;

import com.codex.blog.notification.domain.Notification;
import com.codex.blog.notification.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public Notification send(Notification notification) {
        notification.setStatus("SENT");
        Notification saved = notificationRepository.save(notification);
        log.info("Send notification channel={} recipient={}", notification.getChannel(), notification.getRecipientId());
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Notification> list(String userId) {
        return notificationRepository.findTop20ByRecipientIdOrderByCreatedAtDesc(userId);
    }
}
