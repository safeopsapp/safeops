CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL REFERENCES roles(id),
    permission_id UUID NOT NULL REFERENCES permissions(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_role_permission UNIQUE (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);

-- Assign all permissions to ADMIN
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ADMIN';

-- SAFETY_OFFICER permissions
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN ('PERMIT_VIEW', 'PERMIT_REVIEW', 'PERMIT_APPROVE', 'PERMIT_REJECT', 'PERMIT_CLOSE', 'HAZARD_VIEW', 'HAZARD_UPDATE', 'INCIDENT_VIEW', 'INCIDENT_UPDATE', 'INSPECTION_CREATE', 'INSPECTION_VIEW')
WHERE r.name = 'SAFETY_OFFICER';

-- ENGINEER permissions
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN ('PERMIT_CREATE', 'PERMIT_VIEW', 'HAZARD_CREATE', 'HAZARD_VIEW', 'INCIDENT_CREATE', 'INCIDENT_VIEW', 'INSPECTION_VIEW')
WHERE r.name = 'ENGINEER';

-- WORKER permissions
INSERT INTO role_permissions (id, role_id, permission_id)
SELECT gen_random_uuid(), r.id, p.id
FROM roles r
JOIN permissions p ON p.name IN ('PERMIT_VIEW', 'HAZARD_CREATE', 'HAZARD_VIEW', 'INCIDENT_CREATE', 'INCIDENT_VIEW')
WHERE r.name = 'WORKER';
