package com.codex.blog.notification.repository;

import com.codex.blog.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop20ByRecipientIdOrderByCreatedAtDesc(String recipientId);
}
