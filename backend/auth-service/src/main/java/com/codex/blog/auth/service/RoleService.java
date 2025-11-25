package com.codex.blog.auth.service;

import com.codex.blog.auth.domain.Permission;
import com.codex.blog.auth.domain.Role;
import com.codex.blog.auth.domain.RolePermission;
import com.codex.blog.auth.domain.UserRole;
import com.codex.blog.auth.repository.PermissionRepository;
import com.codex.blog.auth.repository.RolePermissionRepository;
import com.codex.blog.auth.repository.RoleRepository;
import com.codex.blog.auth.repository.UserRoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleService(RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            RolePermissionRepository rolePermissionRepository,
            UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional
    public Role createRole(String code, String name, String description, List<Long> permissionIds) {
        if (roleRepository.existsByCode(code)) {
            throw new IllegalArgumentException("角色代码已存在: " + code);
        }

        Role role = new Role();
        role.setCode(code);
        role.setName(name);
        role.setDescription(description);
        role.setCreatedAt(Instant.now());
        role.setUpdatedAt(Instant.now());

        role = roleRepository.save(role);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            assignPermissionsToRole(role.getId(), permissionIds);
        }

        return role;
    }

    @Transactional
    public Role updateRole(Long roleId, String name, String description) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleId));

        if (name != null) {
            role.setName(name);
        }
        if (description != null) {
            role.setDescription(description);
        }
        role.setUpdatedAt(Instant.now());

        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException("角色不存在: " + roleId);
        }

        // 删除角色-权限关联
        rolePermissionRepository.deleteByRoleId(roleId);

        // 删除用户-角色关联
        userRoleRepository.deleteByRoleId(roleId);

        // 删除角色
        roleRepository.deleteById(roleId);
    }

    public Role getRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在: " + roleId));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public void assignPermissionsToRole(Long roleId, List<Long> permissionIds) {
        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException("角色不存在: " + roleId);
        }

        // 删除现有权限关联
        rolePermissionRepository.deleteByRoleId(roleId);

        // 创建新的权限关联
        for (Long permissionId : permissionIds) {
            if (!permissionRepository.existsById(permissionId)) {
                throw new IllegalArgumentException("权限不存在: " + permissionId);
            }

            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            rp.setAssignedAt(Instant.now());
            rolePermissionRepository.save(rp);
        }
    }

    public List<Permission> getRolePermissions(Long roleId) {
        List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleId(roleId);
        return permissionRepository.findAllById(permissionIds);
    }

    @Transactional
    public void assignRolesToUser(String userId, List<Long> roleIds) {
        // 删除用户现有角色
        userRoleRepository.deleteByUserId(userId);

        // 分配新角色
        for (Long roleId : roleIds) {
            if (!roleRepository.existsById(roleId)) {
                throw new IllegalArgumentException("角色不存在: " + roleId);
            }

            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setAssignedAt(Instant.now());
            userRoleRepository.save(userRole);
        }
    }

    public List<Role> getUserRoles(String userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        return roleRepository.findAllById(roleIds);
    }

    public List<Permission> getUserPermissions(String userId) {
        List<Long> roleIds = userRoleRepository.findRoleIdsByUserId(userId);
        if (roleIds.isEmpty()) {
            return List.of();
        }

        List<Long> permissionIds = rolePermissionRepository.findPermissionIdsByRoleIdIn(roleIds);
        return permissionRepository.findAllById(permissionIds).stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean hasPermission(String userId, String resource) {
        List<Permission> permissions = getUserPermissions(userId);
        return permissions.stream()
                .anyMatch(p -> p.getResource().equals(resource));
    }
}
