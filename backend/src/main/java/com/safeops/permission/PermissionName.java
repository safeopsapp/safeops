package com.safeops.permission;

public enum PermissionName {
    // Permit permissions
    PERMIT_CREATE, PERMIT_VIEW, PERMIT_REVIEW, PERMIT_APPROVE, PERMIT_REJECT, PERMIT_CLOSE,
    // Hazard permissions
    HAZARD_CREATE, HAZARD_VIEW, HAZARD_UPDATE,
    // Incident permissions
    INCIDENT_CREATE, INCIDENT_VIEW, INCIDENT_UPDATE,
    // Inspection permissions
    INSPECTION_CREATE, INSPECTION_VIEW,
    // Management permissions
    USER_MANAGE, PROJECT_MANAGE, SITE_MANAGE, ORGANIZATION_MANAGE, CUSTOMER_MANAGE
}
