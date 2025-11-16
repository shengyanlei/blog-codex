package com.codex.blog.admin.service;

import com.codex.blog.admin.domain.SiteSetting;
import com.codex.blog.admin.repository.SiteSettingRepository;
import com.codex.blog.common.exception.BusinessException;
import com.codex.blog.common.exception.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class SiteSettingService {

    private final SiteSettingRepository siteSettingRepository;

    public SiteSettingService(SiteSettingRepository siteSettingRepository) {
        this.siteSettingRepository = siteSettingRepository;
    }

    @Transactional
    public SiteSetting upsert(String key, String value) {
        Optional<SiteSetting> existing = siteSettingRepository.findByTenantIdAndKey("default", key);
        SiteSetting setting = existing.orElseGet(SiteSetting::new);
        setting.setTenantId("default");
        setting.setKey(key);
        setting.setValue(value);
        setting.setUpdatedAt(Instant.now());
        return siteSettingRepository.save(setting);
    }

    @Transactional(readOnly = true)
    public SiteSetting get(String key) {
        return siteSettingRepository.findByTenantIdAndKey("default", key)
                .orElseThrow(() -> new BusinessException(new SimpleError("SETTING_NOT_FOUND", "Setting not found")));
    }

    private static class SimpleError implements ErrorCode {
        private final String code;
        private final String message;

        private SimpleError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String code() {
            return code;
        }

        @Override
        public String message() {
            return message;
        }
    }
}
