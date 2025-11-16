package com.codex.blog.plugin.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "plugins")
public class Plugin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String version;

    @Column(name = "manifest", columnDefinition = "TEXT")
    private String manifest;

    @Column(name = "status", length = 20)
    private String status = "DISABLED";

    @Column(name = "installed_at", nullable = false)
    private Instant installedAt = Instant.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getManifest() {
        return manifest;
    }

    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getInstalledAt() {
        return installedAt;
    }

    public void setInstalledAt(Instant installedAt) {
        this.installedAt = installedAt;
    }
}

