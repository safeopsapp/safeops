CREATE TABLE sites (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    location VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    customer_id UUID NOT NULL REFERENCES customers(id),
    organization_id UUID NOT NULL REFERENCES organizations(id),
    project_id UUID NOT NULL REFERENCES projects(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_sites_slug ON sites(slug);
CREATE INDEX idx_sites_customer_id ON sites(customer_id);
CREATE INDEX idx_sites_organization_id ON sites(organization_id);
CREATE INDEX idx_sites_project_id ON sites(project_id);
