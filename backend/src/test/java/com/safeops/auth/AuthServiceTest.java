package com.safeops.auth;

import com.google.firebase.auth.FirebaseToken;
import com.safeops.auth.dto.AdminRegistrationRequest;
import com.safeops.auth.dto.AdminRegistrationResponse;
import com.safeops.auth.dto.ReferralCodeResponse;
import com.safeops.auth.dto.StaffRegistrationRequest;
import com.safeops.customer.Customer;
import com.safeops.customer.CustomerRepository;
import com.safeops.membership.MembershipStatus;
import com.safeops.membership.OrganizationMembership;
import com.safeops.membership.OrganizationMembershipRepository;
import com.safeops.membership.ProjectMembershipRepository;
import com.safeops.membership.SiteMembershipRepository;
import com.safeops.organization.Organization;
import com.safeops.organization.OrganizationRepository;
import com.safeops.permission.RolePermissionRepository;
import com.safeops.role.Role;
import com.safeops.role.RoleName;
import com.safeops.role.RoleRepository;
import com.safeops.security.FirebaseAuthenticationException;
import com.safeops.security.SafeOpsPrincipal;
import com.safeops.user.User;
import com.safeops.user.UserRepository;
import com.safeops.user.UserService;
import com.safeops.user.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private UserService userService;
    @Mock private CustomerRepository customerRepository;
    @Mock private OrganizationRepository organizationRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private OrganizationMembershipRepository orgMembershipRepo;
    @Mock private ProjectMembershipRepository projectMembershipRepo;
    @Mock private SiteMembershipRepository siteMembershipRepo;
    @Mock private RolePermissionRepository rolePermissionRepo;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
            userRepository, userService, customerRepository,
            organizationRepository, roleRepository, orgMembershipRepo,
            projectMembershipRepo, siteMembershipRepo, rolePermissionRepo
        );
    }

    private FirebaseToken createFirebaseToken(String uid, String email) {
        try {
            Constructor<FirebaseToken> constructor = FirebaseToken.class.getDeclaredConstructor(Map.class);
            constructor.setAccessible(true);
            Map<String, Object> claims = new HashMap<>();
            claims.put("sub", uid);
            claims.put("email", email);
            claims.put("email_verified", true);
            return constructor.newInstance(claims);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct FirebaseToken for test", e);
        }
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundInDatabase() {
        FirebaseToken token = createFirebaseToken("firebase-uid-unknown", "unknown@test.com");
        when(userRepository.findByFirebaseUid("firebase-uid-unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.buildPrincipal(token))
            .isInstanceOf(FirebaseAuthenticationException.class)
            .hasMessageContaining("User not registered in SafeOps");
    }

    @Test
    void shouldRejectSuspendedUser() {
        String uid = "suspended-uid";
        FirebaseToken token = createFirebaseToken(uid, "suspended@safeops.app");
        User user = new User(uid, "suspended@safeops.app");
        user.setStatus(UserStatus.SUSPENDED);

        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> authService.buildPrincipal(token))
            .isInstanceOf(FirebaseAuthenticationException.class)
            .hasMessageContaining("Account is suspended");
    }

    @Test
    void shouldBuildPrincipalForRegisteredUser() {
        String uid = "firebase-uid-known";
        FirebaseToken token = createFirebaseToken(uid, "user@test.com");
        User user = new User(uid, "user@test.com");
        user.setStatus(UserStatus.ACTIVE);

        when(userRepository.findByFirebaseUid(uid)).thenReturn(Optional.of(user));
        when(orgMembershipRepo.findByUserId(user.getId())).thenReturn(List.of());
        when(projectMembershipRepo.findByUserId(user.getId())).thenReturn(List.of());
        when(siteMembershipRepo.findByUserId(user.getId())).thenReturn(List.of());

        SafeOpsPrincipal principal = authService.buildPrincipal(token);

        assertThat(principal).isNotNull();
        assertThat(principal.getFirebaseUid()).isEqualTo(uid);
        assertThat(principal.getEmail()).isEqualTo("user@test.com");
        assertThat(principal.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(principal.getOrganizations()).isEmpty();
        assertThat(principal.getProjects()).isEmpty();
        assertThat(principal.getSites()).isEmpty();
    }

    @Test
    void shouldValidateReferralCodeSuccessfully() {
        Customer customer = new Customer("Metro Corp", "metro-corp");
        Organization org = new Organization("Metro Corp", "metro-corp", "METRO2026", customer);

        when(organizationRepository.findByReferralCode("METRO2026")).thenReturn(Optional.of(org));

        ReferralCodeResponse response = authService.validateReferralCode("metro2026");

        assertThat(response.isValid()).isTrue();
        assertThat(response.getOrganizationName()).isEqualTo("Metro Corp");
        assertThat(response.getOrgId()).isEqualTo("metro-corp");
    }

    @Test
    void shouldRejectInvalidReferralCode() {
        when(organizationRepository.findByReferralCode("BADCODE")).thenReturn(Optional.empty());

        ReferralCodeResponse response = authService.validateReferralCode("badcode");

        assertThat(response.isValid()).isFalse();
        assertThat(response.getErrorMessage()).contains("Invalid or inactive referral code");
    }

    @Test
    void shouldRegisterAdminAndCreateOrganization() {
        FirebaseToken token = createFirebaseToken("admin-uid", "admin@safeops.app");
        when(userRepository.findByFirebaseUid("admin-uid")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("admin@safeops.app")).thenReturn(Optional.empty());
        when(organizationRepository.existsBySlug("skyline")).thenReturn(false);
        when(organizationRepository.existsByReferralCode("SKYLINE")).thenReturn(false);

        Customer savedCustomer = new Customer("Skyline Corp", "skyline");
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Organization savedOrg = new Organization("Skyline Corp", "skyline", "SKYLINE", savedCustomer);
        when(organizationRepository.save(any(Organization.class))).thenReturn(savedOrg);

        User savedUser = new User("admin-uid", "admin@safeops.app");
        savedUser.setDisplayName("John Boss");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        Role adminRole = new Role(RoleName.ADMIN, "Admin");
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));

        AdminRegistrationRequest req = new AdminRegistrationRequest();
        req.setFullName("John Boss");
        req.setOrganizationName("Skyline Corp");
        req.setOrganizationId("SKYLINE");

        AdminRegistrationResponse res = authService.registerAdmin(token, req);

        assertThat(res).isNotNull();
        assertThat(res.getOrganizationName()).isEqualTo("Skyline Corp");
        assertThat(res.getReferralCode()).isEqualTo("SKYLINE");
        verify(orgMembershipRepo, times(1)).save(any(OrganizationMembership.class));
    }

    @Test
    void shouldRegisterStaffWithPendingMembership() {
        FirebaseToken token = createFirebaseToken("worker-uid", "worker@safeops.app");

        Customer customer = new Customer("Skyline Corp", "skyline");
        Organization org = new Organization("Skyline Corp", "skyline", "SKYLINE", customer);
        when(organizationRepository.findByReferralCode("SKYLINE")).thenReturn(Optional.of(org));
        when(userRepository.findByFirebaseUid("worker-uid")).thenReturn(Optional.empty());

        User savedUser = new User("worker-uid", "worker@safeops.app");
        savedUser.setStatus(UserStatus.PENDING);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(orgMembershipRepo.existsByUserIdAndOrganizationId(any(), any())).thenReturn(false);

        Role workerRole = new Role(RoleName.WORKER, "Worker");
        when(roleRepository.findByName(RoleName.WORKER)).thenReturn(Optional.of(workerRole));

        StaffRegistrationRequest req = new StaffRegistrationRequest();
        req.setReferralCode("SKYLINE");
        req.setFullName("David Field");
        req.setWorkerRole("Field Worker");
        req.setWorkLocation("Zone A");

        authService.registerStaff(token, req);

        verify(orgMembershipRepo, times(1)).save(argThat(m ->
            m.getStatus() == MembershipStatus.PENDING && !m.isActive() && "Zone A".equals(m.getWorkLocation())
        ));
    }
}
