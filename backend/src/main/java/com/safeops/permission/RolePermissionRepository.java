package com.safeops.permission;

import com.safeops.role.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRoleId(UUID roleId);

    @Query("SELECT rp.permission.name FROM RolePermission rp WHERE rp.role.name = :roleName")
    List<PermissionName> findPermissionNamesByRoleName(@Param("roleName") RoleName roleName);
}
