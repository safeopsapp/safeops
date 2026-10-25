CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_permissions_name ON permissions(name);

INSERT INTO permissions (id, name, description) VALUES
    (gen_random_uuid(), 'PERMIT_CREATE', 'Create work permits'),
    (gen_random_uuid(), 'PERMIT_VIEW', 'View work permits'),
    (gen_random_uuid(), 'PERMIT_REVIEW', 'Review work permits'),
    (gen_random_uuid(), 'PERMIT_APPROVE', 'Approve work permits'),
    (gen_random_uuid(), 'PERMIT_REJECT', 'Reject work permits'),
    (gen_random_uuid(), 'PERMIT_CLOSE', 'Close work permits'),
    (gen_random_uuid(), 'HAZARD_CREATE', 'Create hazard reports'),
    (gen_random_uuid(), 'HAZARD_VIEW', 'View hazard reports'),
    (gen_random_uuid(), 'HAZARD_UPDATE', 'Update hazard reports'),
    (gen_random_uuid(), 'INCIDENT_CREATE', 'Create incident reports'),
    (gen_random_uuid(), 'INCIDENT_VIEW', 'View incident reports'),
    (gen_random_uuid(), 'INCIDENT_UPDATE', 'Update incident reports'),
    (gen_random_uuid(), 'INSPECTION_CREATE', 'Create inspections'),
    (gen_random_uuid(), 'INSPECTION_VIEW', 'View inspections'),
    (gen_random_uuid(), 'USER_MANAGE', 'Manage users'),
    (gen_random_uuid(), 'PROJECT_MANAGE', 'Manage projects'),
    (gen_random_uuid(), 'SITE_MANAGE', 'Manage sites'),
    (gen_random_uuid(), 'ORGANIZATION_MANAGE', 'Manage organizations'),
    (gen_random_uuid(), 'CUSTOMER_MANAGE', 'Manage customers');
