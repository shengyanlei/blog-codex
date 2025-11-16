package com.codex.blog.plugin.web;

import com.codex.blog.common.dto.ApiResponse;
import com.codex.blog.plugin.domain.Plugin;
import com.codex.blog.plugin.service.PluginService;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/plugins")
public class PluginController {

    private final PluginService pluginService;

    public PluginController(PluginService pluginService) {
        this.pluginService = pluginService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Plugin>>> list() {
        return ResponseEntity.ok(ApiResponse.success(pluginService.list()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Plugin>> install(@Valid @RequestBody Plugin plugin) {
        return ResponseEntity.ok(ApiResponse.success(pluginService.install(plugin)));
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<ApiResponse<Plugin>> toggle(@PathVariable Long id,
                                                      @RequestParam(defaultValue = "true") boolean enabled) {
        return ResponseEntity.ok(ApiResponse.success(pluginService.toggle(id, enabled)));
    }
}

