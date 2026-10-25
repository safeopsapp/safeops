package com.safeops.security;

import com.safeops.permission.PermissionName;
import com.safeops.role.RoleName;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests that the SafeOpsPrincipal correctly enforces tenant isolation.
 * 
 * Critical Security Requirement:
 * A user belonging to Organization A must NOT be able to access Organization B resources.
 */
class TenantIsolationTest {

    @Test
    void userShouldBelongToTheirOrganizationOnly() {
        UUID orgAId = UUID.randomUUID();
        UUID orgBId = UUID.randomUUID();

        // User A belongs to Organization A
        SafeOpsPrincipal userA = SafeOpsPrincipal.builder()
            .firebaseUid("firebase-user-a")
            .userId(UUID.randomUUID())
            .email("user-a@orgA.com")
            .organizations(List.of(
                new SafeOpsPrincipal.OrganizationContext(orgAId, "Organization A", RoleName.ENGINEER)
            ))
            .projects(List.of())
            .sites(List.of())
            .roles(Set.of(RoleName.ENGINEER))
            .permissions(Set.of(PermissionName.PERMIT_CREATE, PermissionName.PERMIT_VIEW))
            .build();

        // User B belongs to Organization B
        SafeOpsPrincipal userB = SafeOpsPrincipal.builder()
            .firebaseUid("firebase-user-b")
            .userId(UUID.randomUUID())
            .email("user-b@orgB.com")
            .organizations(List.of(
                new SafeOpsPrincipal.OrganizationContext(orgBId, "Organization B", RoleName.SAFETY_OFFICER)
            ))
            .projects(List.of())
            .sites(List.of())
            .roles(Set.of(RoleName.SAFETY_OFFICER))
            .permissions(Set.of(PermissionName.PERMIT_APPROVE, PermissionName.PERMIT_REJECT))
            .build();

        // User A belongs to Org A
        assertThat(userA.belongsToOrganization(orgAId)).isTrue();
        
        // User A does NOT belong to Org B
        assertThat(userA.belongsToOrganization(orgBId)).isFalse();
        
        // User B belongs to Org B
        assertThat(userB.belongsToOrganization(orgBId)).isTrue();
        
        // User B does NOT belong to Org A
        assertThat(userB.belongsToOrganization(orgAId)).isFalse();
    }

    @Test
    void userShouldOnlyHavePermissionsForTheirRole() {
        SafeOpsPrincipal worker = SafeOpsPrincipal.builder()
            .firebaseUid("firebase-worker")
            .userId(UUID.randomUUID())
            .email("worker@site.com")
            .organizations(List.of())
            .projects(List.of())
            .sites(List.of())
            .roles(Set.of(RoleName.WORKER))
            .permissions(Set.of(PermissionName.PERMIT_VIEW, PermissionName.HAZARD_CREATE, PermissionName.HAZARD_VIEW))
            .build();

        // Worker can view permits
        assertThat(worker.hasPermission(PermissionName.PERMIT_VIEW)).isTrue();
        
        // Worker CANNOT approve permits
        assertThat(worker.hasPermission(PermissionName.PERMIT_APPROVE)).isFalse();
        
        // Worker CANNOT manage users
        assertThat(worker.hasPermission(PermissionName.USER_MANAGE)).isFalse();
        
        // Worker has WORKER role
        assertThat(worker.hasRole(RoleName.WORKER)).isTrue();
        
        // Worker does NOT have ADMIN role
        assertThat(worker.hasRole(RoleName.ADMIN)).isFalse();
    }

    @Test
    void userShouldNotAccessAnotherTenantProject() {
        UUID projectAId = UUID.randomUUID();
        UUID projectBId = UUID.randomUUID();

        SafeOpsPrincipal engineerOnProjectA = SafeOpsPrincipal.builder()
            .firebaseUid("firebase-engineer")
            .userId(UUID.randomUUID())
            .email("engineer@tenant.com")
            .organizations(List.of())
            .projects(List.of(
                new SafeOpsPrincipal.ProjectContext(projectAId, "Project Alpha", RoleName.ENGINEER)
            ))
            .sites(List.of())
            .roles(Set.of(RoleName.ENGINEER))
            .permissions(Set.of(PermissionName.PERMIT_CREATE))
            .build();

        // Engineer has access to Project A
        assertThat(engineerOnProjectA.belongsToProject(projectAId)).isTrue();
        
        // Engineer does NOT have access to Project B
        assertThat(engineerOnProjectA.belongsToProject(projectBId)).isFalse();
    }
}
