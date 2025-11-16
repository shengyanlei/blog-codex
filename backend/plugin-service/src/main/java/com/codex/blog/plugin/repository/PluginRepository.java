package com.codex.blog.plugin.repository;

import com.codex.blog.plugin.domain.Plugin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PluginRepository extends JpaRepository<Plugin, Long> {

    Optional<Plugin> findByName(String name);
}
