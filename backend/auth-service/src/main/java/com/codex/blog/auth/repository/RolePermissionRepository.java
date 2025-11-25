package com.codex.blog.auth.repository;

import com.codex.blog.auth.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.RolePermissionId> {

    List<RolePermission> findByRoleId(Long roleId);

    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId = :roleId")
    List<Long> findPermissionIdsByRoleId(@Param("roleId") Long roleId);

    @Query("SELECT rp.permissionId FROM RolePermission rp WHERE rp.roleId IN :roleIds")
    List<Long> findPermissionIdsByRoleIdIn(@Param("roleIds") List<Long> roleIds);

    void deleteByRoleId(Long roleId);

    void deleteByRoleIdAndPermissionId(Long roleId, Long permissionId);
}
