package com.codex.blog.plugin.service;

import com.codex.blog.common.exception.BusinessException;
import com.codex.blog.common.exception.error.ErrorCode;
import com.codex.blog.plugin.domain.Plugin;
import com.codex.blog.plugin.repository.PluginRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PluginService {

    private final PluginRepository pluginRepository;

    public PluginService(PluginRepository pluginRepository) {
        this.pluginRepository = pluginRepository;
    }

    @Transactional
    public Plugin install(Plugin plugin) {
        pluginRepository.findByName(plugin.getName()).ifPresent(existing -> {
            throw new BusinessException(new SimpleError("PLUGIN_EXISTS", "Plugin already installed"));
        });
        plugin.setStatus("DISABLED");
        return pluginRepository.save(plugin);
    }

    @Transactional
    public Plugin toggle(Long id, boolean enabled) {
        Plugin plugin = pluginRepository.findById(id)
                .orElseThrow(() -> new BusinessException(new SimpleError("PLUGIN_NOT_FOUND", "Plugin not found")));
        plugin.setStatus(enabled ? "ENABLED" : "DISABLED");
        return plugin;
    }

    @Transactional(readOnly = true)
    public List<Plugin> list() {
        return pluginRepository.findAll();
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
