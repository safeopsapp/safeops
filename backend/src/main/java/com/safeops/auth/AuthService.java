package com.safeops.auth;

import com.google.firebase.auth.FirebaseToken;
import com.safeops.auth.dto.*;
import com.safeops.common.exception.ForbiddenException;
import com.safeops.common.exception.ResourceNotFoundException;
import com.safeops.common.exception.ValidationException;
import com.safeops.customer.Customer;
import com.safeops.customer.CustomerRepository;
import com.safeops.membership.MembershipStatus;
import com.safeops.membership.OrganizationMembership;
import com.safeops.membership.OrganizationMembershipRepository;
import com.safeops.membership.ProjectMembershipRepository;
import com.safeops.membership.SiteMembershipRepository;
import com.safeops.organization.Organization;
import com.safeops.organization.OrganizationRepository;
import com.safeops.permission.PermissionName;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final OrganizationMembershipRepository organizationMembershipRepository;
    private final ProjectMembershipRepository projectMembershipRepository;
    private final SiteMembershipRepository siteMembershipRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            CustomerRepository customerRepository,
            OrganizationRepository organizationRepository,
            RoleRepository roleRepository,
            OrganizationMembershipRepository organizationMembershipRepository,
            ProjectMembershipRepository projectMembershipRepository,
            SiteMembershipRepository siteMembershipRepository,
            RolePermissionRepository rolePermissionRepository
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.customerRepository = customerRepository;
        this.organizationRepository = organizationRepository;
        this.roleRepository = roleRepository;
        this.organizationMembershipRepository = organizationMembershipRepository;
        this.projectMembershipRepository = projectMembershipRepository;
        this.siteMembershipRepository = siteMembershipRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    @Transactional
    public SafeOpsPrincipal buildPrincipal(FirebaseToken firebaseToken) {
        String firebaseUid = firebaseToken.getUid();

        User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseGet(() -> {
                if (firebaseToken.getEmail() != null && !firebaseToken.getEmail().isBlank()) {
                    return userRepository.findByEmail(firebaseToken.getEmail())
                        .map(existingUser -> {
                            log.info("Linking firebaseUid={} to existing user email={}", firebaseUid, firebaseToken.getEmail());
                            existingUser.setFirebaseUid(firebaseUid);
                            return userRepository.save(existingUser);
                        })
                        .orElse(null);
                }
                return null;
            });

        if (user == null) {
            log.warn("No SafeOps user found for firebaseUid={} email={}", firebaseUid, firebaseToken.getEmail());
            throw new FirebaseAuthenticationException(
                "User not registered in SafeOps. Please complete registration."
            );
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new FirebaseAuthenticationException("Account is suspended. Please contact your organization administrator.");
        }
        if (user.getStatus() == UserStatus.DEACTIVATED || !user.isActive()) {
            throw new FirebaseAuthenticationException("Account is deactivated.");
        }

        // Build active organization contexts
        List<SafeOpsPrincipal.OrganizationContext> orgContexts = organizationMembershipRepository
            .findByUserId(user.getId()).stream()
            .filter(m -> m.isActive() && m.getStatus() == MembershipStatus.ACTIVE)
            .map(m -> new SafeOpsPrincipal.OrganizationContext(
                m.getOrganization().getId(),
                m.getOrganization().getName(),
                m.getRole().getName()
            ))
            .collect(Collectors.toList());

        // Build active project contexts
        List<SafeOpsPrincipal.ProjectContext> projectContexts = projectMembershipRepository
            .findByUserId(user.getId()).stream()
            .filter(m -> m.isActive())
            .map(m -> new SafeOpsPrincipal.ProjectContext(
                m.getProject().getId(),
                m.getProject().getName(),
                m.getRole().getName()
            ))
            .collect(Collectors.toList());

        // Build active site contexts
        List<SafeOpsPrincipal.SiteContext> siteContexts = siteMembershipRepository
            .findByUserId(user.getId()).stream()
            .filter(m -> m.isActive())
            .map(m -> new SafeOpsPrincipal.SiteContext(
                m.getSite().getId(),
                m.getSite().getName(),
                m.getRole().getName()
            ))
            .collect(Collectors.toList());

        // Collect all roles from active memberships
        Set<RoleName> roles = new HashSet<>();
        orgContexts.forEach(oc -> roles.add(oc.role()));
        projectContexts.forEach(pc -> roles.add(pc.role()));
        siteContexts.forEach(sc -> roles.add(sc.role()));

        // Collect permissions from active roles
        Set<PermissionName> permissions = roles.stream()
            .flatMap(role -> rolePermissionRepository.findPermissionNamesByRoleName(role).stream())
            .collect(Collectors.toSet());

        return SafeOpsPrincipal.builder()
            .firebaseUid(firebaseUid)
            .userId(user.getId())
            .email(user.getEmail())
            .displayName(user.getDisplayName())
            .customerId(user.getCustomer() != null ? user.getCustomer().getId() : null)
            .status(user.getStatus())
            .organizations(orgContexts)
            .projects(projectContexts)
            .sites(siteContexts)
            .roles(roles)
            .permissions(permissions)
            .build();
    }

    @Transactional
    public SafeOpsPrincipal syncAndBuildPrincipal(FirebaseToken firebaseToken) {
        String firebaseUid = firebaseToken.getUid();
        User user = userRepository.findByFirebaseUid(firebaseUid)
            .orElseThrow(() -> new FirebaseAuthenticationException(
                "User not registered in SafeOps. Please complete registration with an organization."
            ));

        if (firebaseToken.getEmail() != null && !firebaseToken.getEmail().isBlank()) {
            user.setEmail(firebaseToken.getEmail());
        }
        if (firebaseToken.getName() != null && !firebaseToken.getName().isBlank()) {
            user.setDisplayName(firebaseToken.getName());
        }
        user.setEmailVerified(firebaseToken.isEmailVerified());
        userRepository.save(user);

        return buildPrincipal(firebaseToken);
    }

    @Transactional(readOnly = true)
    public ReferralCodeResponse validateReferralCode(String code) {
        if (code == null || code.trim().length() < 3) {
            return ReferralCodeResponse.invalid("Referral code must be at least 3 characters");
        }
        String normalized = code.trim().toUpperCase();
        return organizationRepository.findByReferralCode(normalized)
            .filter(Organization::isActive)
            .map(org -> ReferralCodeResponse.success(org.getId(), org.getSlug(), org.getName()))
            .orElseGet(() -> ReferralCodeResponse.invalid("Invalid or inactive referral code. Please check with your administrator."));
    }

    @Transactional
    public AdminRegistrationResponse registerAdmin(FirebaseToken firebaseToken, AdminRegistrationRequest request) {
        String firebaseUid = firebaseToken.getUid();
        String email = firebaseToken.getEmail();

        if (email == null || email.isBlank()) {
            throw new ValidationException("Firebase token must contain a valid email address");
        }

        if (userRepository.findByFirebaseUid(firebaseUid).isPresent()) {
            throw new ValidationException("This account is already registered in SafeOps");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("A user with this email already exists");
        }

        String rawOrgId = request.getOrganizationId().trim();
        String slug = rawOrgId.toLowerCase();
        String referralCode = rawOrgId.toUpperCase();

        if (organizationRepository.existsBySlug(slug) || organizationRepository.existsByReferralCode(referralCode)) {
            throw new ValidationException("Organization ID '" + rawOrgId + "' is already in use. Please select a different Organization ID.");
        }

        // 1. Create Customer tenant
        Customer customer = new Customer(request.getOrganizationName(), slug);
        customer = customerRepository.save(customer);

        // 2. Create Organization
        Organization organization = new Organization(request.getOrganizationName(), slug, referralCode, customer);
        if (request.getAddress() != null) {
            organization.setDescription("Industry: " + request.getIndustryType() + " | Address: " + request.getAddress());
        }
        organization = organizationRepository.save(organization);

        // 3. Create Admin User
        User user = new User(firebaseUid, email);
        user.setDisplayName(request.getFullName());
        user.setEmailVerified(firebaseToken.isEmailVerified());
        user.setCustomer(customer);
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);
        user = userRepository.save(user);

        // 4. Assign ADMIN Role
        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
            .orElseThrow(() -> new ResourceNotFoundException("Role ADMIN not found in database"));

        OrganizationMembership membership = new OrganizationMembership(user, organization, adminRole, MembershipStatus.ACTIVE);
        organizationMembershipRepository.save(membership);

        log.info("Registered organization admin: user={}, org={}, referralCode={}", user.getEmail(), organization.getName(), referralCode);

        return new AdminRegistrationResponse(
            customer.getId(),
            organization.getId(),
            organization.getSlug(),
            organization.getReferralCode(),
            organization.getName(),
            user.getDisplayName()
        );
    }

    @Transactional
    public void registerStaff(FirebaseToken firebaseToken, StaffRegistrationRequest request) {
        String firebaseUid = firebaseToken.getUid();
        String email = firebaseToken.getEmail();

        if (email == null || email.isBlank()) {
            throw new ValidationException("Firebase token must contain a valid email address");
        }

        String referralCode = request.getReferralCode().trim().toUpperCase();
        Organization organization = organizationRepository.findByReferralCode(referralCode)
            .filter(Organization::isActive)
            .orElseThrow(() -> new ValidationException("Invalid organization referral code: " + referralCode));

        User user = userRepository.findByFirebaseUid(firebaseUid).orElse(null);
        if (user == null) {
            user = new User(firebaseUid, email);
            user.setDisplayName(request.getFullName());
            user.setEmailVerified(firebaseToken.isEmailVerified());
            user.setCustomer(organization.getCustomer());
            user.setStatus(UserStatus.PENDING);
            user.setActive(false);
            user = userRepository.save(user);
        }

        if (organizationMembershipRepository.existsByUserIdAndOrganizationId(user.getId(), organization.getId())) {
            throw new ValidationException("You already have an existing membership or request with " + organization.getName());
        }

        // Map requested worker role
        RoleName assignedRole = resolveRoleName(request.getWorkerRole());

        Role role = roleRepository.findByName(assignedRole)
            .orElseThrow(() -> new ResourceNotFoundException("Role " + assignedRole + " not found"));

        OrganizationMembership membership = new OrganizationMembership(
            user,
            organization,
            role,
            MembershipStatus.PENDING
        );
        membership.setWorkerRole(request.getWorkerRole());
        membership.setWorkLocation(request.getWorkLocation());
        membership.setActive(false);
        organizationMembershipRepository.save(membership);

        log.info("Registered staff membership pending approval: user={}, org={}, role={}", user.getEmail(), organization.getName(), assignedRole);
    }

    @Transactional(readOnly = true)
    public List<PendingMembershipResponse> getPendingMemberships(UUID organizationId, SafeOpsPrincipal principal) {
        verifyAdminAccess(organizationId, principal);

        return organizationMembershipRepository.findByOrganizationIdAndStatus(organizationId, MembershipStatus.PENDING).stream()
            .map(m -> new PendingMembershipResponse(
                m.getId(),
                m.getUser().getId(),
                m.getUser().getDisplayName() != null ? m.getUser().getDisplayName() : m.getUser().getEmail(),
                m.getUser().getEmail(),
                m.getRole().getName().name(),
                m.getWorkerRole(),
                m.getWorkLocation(),
                m.getStatus().name(),
                m.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public void approveMembership(UUID organizationId, UUID membershipId, SafeOpsPrincipal principal) {
        verifyAdminAccess(organizationId, principal);

        OrganizationMembership membership = organizationMembershipRepository.findById(membershipId)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + membershipId));

        if (!membership.getOrganization().getId().equals(organizationId)) {
            throw new ForbiddenException("Membership does not belong to this organization");
        }

        membership.setStatus(MembershipStatus.ACTIVE);
        membership.setActive(true);
        organizationMembershipRepository.save(membership);

        User user = membership.getUser();
        user.setStatus(UserStatus.ACTIVE);
        user.setActive(true);
        userRepository.save(user);

        log.info("Approved staff membership: user={}, org={}", user.getEmail(), membership.getOrganization().getName());
    }

    @Transactional
    public void rejectMembership(UUID organizationId, UUID membershipId, SafeOpsPrincipal principal) {
        verifyAdminAccess(organizationId, principal);

        OrganizationMembership membership = organizationMembershipRepository.findById(membershipId)
            .orElseThrow(() -> new ResourceNotFoundException("Membership not found: " + membershipId));

        if (!membership.getOrganization().getId().equals(organizationId)) {
            throw new ForbiddenException("Membership does not belong to this organization");
        }

        membership.setStatus(MembershipStatus.REJECTED);
        membership.setActive(false);
        organizationMembershipRepository.save(membership);

        log.info("Rejected staff membership: user={}, org={}", membership.getUser().getEmail(), membership.getOrganization().getName());
    }

    private RoleName resolveRoleName(String workerRole) {
        if (workerRole == null) {
            return RoleName.WORKER;
        }
        String lower = workerRole.toLowerCase();
        if (lower.contains("engineer")) {
            return RoleName.ENGINEER;
        }
        if (lower.contains("safety")) {
            return RoleName.SAFETY_OFFICER;
        }
        return RoleName.WORKER;
    }

    private void verifyAdminAccess(UUID organizationId, SafeOpsPrincipal principal) {
        if (!principal.hasRole(RoleName.ADMIN)) {
            throw new ForbiddenException("Only organization administrators can access membership requests");
        }
        if (!principal.belongsToOrganization(organizationId)) {
            throw new ForbiddenException("You do not have administrative authority over this organization");
        }
    }
}
