package com.codex.blog.auth.repository;

import com.codex.blog.auth.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByResource(String resource);

    List<Permission> findByPermissionGroup(String permissionGroup);

    boolean existsByResource(String resource);
}
