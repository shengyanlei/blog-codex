package com.codex.blog.admin.service;

import com.codex.blog.admin.domain.Theme;
import com.codex.blog.admin.repository.ThemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ThemeService {

    private final ThemeRepository themeRepository;

    public ThemeService(ThemeRepository themeRepository) {
        this.themeRepository = themeRepository;
    }

    @Transactional
    public Theme upload(Theme theme) {
        theme.setActive(false);
        return themeRepository.save(theme);
    }

    @Transactional
    public Theme activate(Long id) {
        List<Theme> themes = themeRepository.findAll();
        for (Theme theme : themes) {
            theme.setActive(theme.getId().equals(id));
        }
        themeRepository.saveAll(themes);
        return themeRepository.findById(id).orElseThrow();
    }

    @Transactional(readOnly = true)
    public List<Theme> list() {
        return themeRepository.findAll();
    }
}
