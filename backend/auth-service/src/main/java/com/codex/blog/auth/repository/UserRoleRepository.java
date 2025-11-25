package com.codex.blog.auth.repository;

import com.codex.blog.auth.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRole.UserRoleId> {

    List<UserRole> findByUserId(String userId);

    List<UserRole> findByRoleId(Long roleId);

    @Query("SELECT ur.roleId FROM UserRole ur WHERE ur.userId = :userId")
    List<Long> findRoleIdsByUserId(@Param("userId") String userId);

    void deleteByUserId(String userId);

    void deleteByRoleId(Long roleId);

    void deleteByUserIdAndRoleId(String userId, Long roleId);
}
