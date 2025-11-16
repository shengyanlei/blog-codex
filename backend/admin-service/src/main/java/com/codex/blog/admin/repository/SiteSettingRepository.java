package com.codex.blog.admin.repository;

import com.codex.blog.admin.domain.SiteSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SiteSettingRepository extends JpaRepository<SiteSetting, Long> {

    Optional<SiteSetting> findByTenantIdAndKey(String tenantId, String key);
}
