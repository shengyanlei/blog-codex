package com.codex.blog.admin.web;

import com.codex.blog.admin.domain.SiteSetting;
import com.codex.blog.admin.domain.Theme;
import com.codex.blog.admin.service.SiteSettingService;
import com.codex.blog.admin.service.ThemeService;
import com.codex.blog.common.dto.ApiResponse;
import javax.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final SiteSettingService siteSettingService;
    private final ThemeService themeService;

    public AdminController(SiteSettingService siteSettingService, ThemeService themeService) {
        this.siteSettingService = siteSettingService;
        this.themeService = themeService;
    }

    @GetMapping("/settings")
    public ResponseEntity<ApiResponse<SiteSetting>> getSetting(@RequestParam @NotBlank String key) {
        return ResponseEntity.ok(ApiResponse.success(siteSettingService.get(key)));
    }

    @PostMapping("/settings")
    public ResponseEntity<ApiResponse<SiteSetting>> upsert(@RequestBody Map<String, String> body) {
        SiteSetting setting = siteSettingService.upsert(body.get("key"), body.get("value"));
        return ResponseEntity.ok(ApiResponse.success(setting));
    }

    @GetMapping("/themes")
    public ResponseEntity<ApiResponse<List<Theme>>> listThemes() {
        return ResponseEntity.ok(ApiResponse.success(themeService.list()));
    }

    @PostMapping("/themes")
    public ResponseEntity<ApiResponse<Theme>> upload(@RequestBody Theme theme) {
        return ResponseEntity.ok(ApiResponse.success(themeService.upload(theme)));
    }

    @PostMapping("/themes/{id}/activate")
    public ResponseEntity<ApiResponse<Theme>> activate(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(themeService.activate(id)));
    }
}
