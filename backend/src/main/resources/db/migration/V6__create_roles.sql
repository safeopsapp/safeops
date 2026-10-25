CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_roles_name ON roles(name);

INSERT INTO roles (id, name, description) VALUES
    (gen_random_uuid(), 'ADMIN', 'Full administrative access'),
    (gen_random_uuid(), 'SAFETY_OFFICER', 'Safety management and permit approval'),
    (gen_random_uuid(), 'ENGINEER', 'Engineering operations and permit submission'),
    (gen_random_uuid(), 'WORKER', 'Field worker access');
