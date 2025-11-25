package com.codex.blog.auth.web;

import com.codex.blog.auth.domain.Permission;
import com.codex.blog.auth.domain.Role;
import com.codex.blog.auth.service.RoleService;
import com.codex.blog.auth.web.dto.AssignPermissionsRequest;
import com.codex.blog.auth.web.dto.AssignRolesRequest;
import com.codex.blog.auth.web.dto.RoleCreateRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createRole(@RequestBody RoleCreateRequest request) {
        Role role = roleService.createRole(
                request.getCode(),
                request.getName(),
                request.getDescription(),
                request.getPermissionIds());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "角色创建成功");
        response.put("data", role);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateRole(
            @PathVariable Long id,
            @RequestBody RoleCreateRequest request) {
        Role role = roleService.updateRole(id, request.getName(), request.getDescription());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "角色更新成功");
        response.put("data", role);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "角色删除成功");

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> listRoles() {
        List<Role> roles = roleService.getAllRoles();

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", roles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRole(@PathVariable Long id) {
        Role role = roleService.getRole(id);
        List<Permission> permissions = roleService.getRolePermissions(id);

        Map<String, Object> data = new HashMap<>();
        data.put("role", role);
        data.put("permissions", permissions);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Map<String, Object>> assignPermissions(
            @PathVariable Long id,
            @RequestBody AssignPermissionsRequest request) {
        roleService.assignPermissionsToRole(id, request.getPermissionIds());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "权限分配成功");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/roles")
    public ResponseEntity<Map<String, Object>> assignRolesToUser(
            @PathVariable String userId,
            @RequestBody AssignRolesRequest request) {
        roleService.assignRolesToUser(userId, request.getRoleIds());

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "角色分配成功");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/roles")
    public ResponseEntity<Map<String, Object>> getUserRoles(@PathVariable String userId) {
        List<Role> roles = roleService.getUserRoles(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", roles);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/permissions")
    public ResponseEntity<Map<String, Object>> getUserPermissions(@PathVariable String userId) {
        List<Permission> permissions = roleService.getUserPermissions(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 0);
        response.put("message", "成功");
        response.put("data", permissions);

        return ResponseEntity.ok(response);
    }
}
