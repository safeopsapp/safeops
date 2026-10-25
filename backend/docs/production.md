# SafeOps Production Infrastructure & AWS RDS Deployment Guide

This document establishes the verified production configuration, database sizing analysis, security architecture, and operational procedures for the SafeOps backend deployed on AWS with Amazon RDS PostgreSQL.

---

## 1. Verified AWS RDS Topology

| Specification | Target Value | Verification Note |
| :--- | :--- | :--- |
| **DB Instance Identifier** | `safeops-db` | Primary production instance |
| **Database Name** | `safeops` | Application database |
| **Database Engine** | PostgreSQL 18.3 | Engine version |
| **AWS Region** | `ap-south-1` (Mumbai) | Target latency zone for India operations |
| **Instance Class** | `db.t3.micro` | 2 vCPUs, 1 GiB RAM, Burstable |
| **Storage** | 20 GiB gp3 | 3,000 IOPS, 125 MiB/s throughput |
| **Storage Autoscaling** | Enabled | Max limit: 1,000 GiB |
| **Encryption at Rest** | Enabled | AWS KMS managed key |
| **Public Accessibility** | **No (Private)** | Strictly no direct Internet gateway ingress |
| **Multi-AZ Deployment** | **Disabled (Single-AZ)** | Production availability assessment detailed in Section 3 |
| **Deletion Protection** | **Disabled** | ⚠️ Recommendation to enable detailed in Section 4 |
| **Monitoring** | Enhanced (60s) | Monitoring role `arn:aws:iam::605606417505:role/rds-monitoring-role` |
| **VPC** | `SafeOps-vpc` | ID: `vpc-016d5ab8169fecec8` |
| **RDS Subnet Group** | `safeops-db-subnet-group` | Bound across private subnets |
| **RDS Security Group** | `safeops-rds-sg` | ID: `sg-09985efb3acf6419c` |
| **RDS Endpoint** | `safeops-db.cbam0y2e0rb8.ap-south-1.rds.amazonaws.com` | Port `5432` |
| **Master Username** | `postgres` | Reserved for administrative/DDL tasks only |

---

## 2. Dedicated Application Database User Provisioning

To enforce the principle of least privilege, the SafeOps Spring Boot application **must never** connect to RDS using the master `postgres` superuser account.

Execute the following SQL script as master `postgres` during initial database initialization to create the dedicated `safeops_app` user:

```sql
-- 1. Create dedicated application role with strong generated password from AWS Secrets Manager
CREATE USER safeops_app WITH ENCRYPTED PASSWORD 'REPLACE_WITH_GENERATED_SECRET';

-- 2. Grant connection rights to the application database
REVOKE ALL ON DATABASE safeops FROM PUBLIC;
GRANT CONNECT ON DATABASE safeops TO safeops_app;

-- 3. Switch to safeops database
\c safeops

-- 4. Grant schema usage
GRANT USAGE ON SCHEMA public TO safeops_app;

-- 5. Grant CRUD permissions on existing tables and sequences
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO safeops_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO safeops_app;

-- 6. Set default privileges for tables/sequences created in future migrations
ALTER DEFAULT PRIVILEGES IN SCHEMA public 
    GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO safeops_app;

ALTER DEFAULT PRIVILEGES IN SCHEMA public 
    GRANT USAGE, SELECT ON SEQUENCES TO safeops_app;

-- 7. Revoke table creation and dropping rights from the application user
REVOKE CREATE ON SCHEMA public FROM safeops_app;
```

---

## 3. Sizing & Resource Assessment for `db.t3.micro`

### Memory Constraints (1 GiB RAM)
A `db.t3.micro` instance provides 1 GiB (1,024 MB) of total system RAM. PostgreSQL allocates memory for:
- `shared_buffers`: ~25% of RAM (~256 MB)
- Operating system and AWS RDS monitoring agents: ~300 MB
- Available memory for active backend processes (`work_mem`, connection overhead): ~400 MB

**Connection Pool Tuning:**
Each active PostgreSQL connection allocates private process memory (5–10 MB + query sorting buffers).
- Default connection pool sizes (e.g. 20–30) across multiple backend instances will trigger out-of-memory (OOM) errors and instance restarts on a 1 GiB node.
- **Tuned Configuration:** `application-prod.yml` has been configured with:
  ```yaml
  hikari:
    maximum-pool-size: 10
    minimum-idle: 2
    connection-timeout: 30000
    idle-timeout: 600000
    max-lifetime: 1800000
  ```
- This guarantees the application connection footprint remains under ~80 MB total, preserving buffer space for index caching and query execution.

### CPU & IOPS Constraints (T3 Burstable)
- `db.t3.micro` operates on CPU credits. Sustained high CPU (>20%) will exhaust credit balances, resulting in query throttling.
- `gp3` storage provides baseline 3,000 IOPS and 125 MiB/s throughput, which is more than sufficient for the foundation phase.

### High Availability & SLA Risk (Single-AZ)
- Current Multi-AZ setting: **Disabled**.
- **Impact:** If the underlying AWS physical host or Availability Zone experiences hardware failure or maintenance, the database will be unavailable until AWS recovers the instance (typically 10–30 minutes downtime).
- **Recommendation:** Upgrade to Multi-AZ (`db.t3.small` or `db.t4g.small` with Multi-AZ) prior to enterprise commercial roll-out with contractual SLAs.

---

## 4. Deletion Protection Assessment

- Current Status: **Disabled**.
- **Risk:** High risk of catastrophic data loss. An administrative error in the AWS Console, automated Terraform/CloudFormation script, or CLI command could terminate the database without confirmation.
- **Recommended Action:** Enable deletion protection immediately via AWS Console or CLI:
  ```bash
  aws rds modify-db-instance \
      --db-instance-identifier safeops-db \
      --deletion-protection \
      --apply-immediately \
      --region ap-south-1
  ```
  *(Requires user authorization before executing)*.

---

## 5. Security & Secret Management Architecture

### AWS Secrets Manager Secret Schema
Store production database credentials under secret identifier `safeops/prod/rds`:
```json
{
  "engine": "postgres",
  "host": "safeops-db.cbam0y2e0rb8.ap-south-1.rds.amazonaws.com",
  "port": 5432,
  "dbname": "safeops",
  "username": "safeops_app",
  "password": "<STRONG_GENERATED_PASSWORD>"
}
```

### Runtime Environment Injection (`/etc/safeops/safeops.env`)
Generated on EC2 startup from AWS Secrets Manager using IAM instance profile (no hardcoded keys):
```bash
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_HOST=safeops-db.cbam0y2e0rb8.ap-south-1.rds.amazonaws.com
DB_PORT=5432
DB_NAME=safeops
DB_USERNAME=safeops_app
DB_PASSWORD=<INJECTED_AT_RUNTIME_FROM_SECRETS_MANAGER>
DB_SSL_MODE=require
DB_POOL_MAX=10
DB_POOL_MIN_IDLE=2
SAFEOPS_FIREBASE_CREDENTIALS_JSON=<INJECTED_FROM_SECRETS_MANAGER>
AWS_REGION=ap-south-1
```

---

## 6. PostgreSQL 18.3 & Flyway Compatibility Analysis

1. **JDBC Driver:** PostgreSQL JDBC Driver `42.7.4` (Spring Boot 3.3.5 managed) fully supports the PostgreSQL v3 wire protocol utilized by PostgreSQL 18.x.
2. **Flyway Migrations:** All migrations (`V1` through `V11`) use ANSI-standard SQL and standard PostgreSQL extensions (`uuid-ossp`, `gen_random_uuid()`, B-tree indexing). Validated and verified clean.
3. **Hibernate Dialect:** Spring Data JPA uses `PostgreSQLDialect` in Hibernate 6.5.3, which cleanly maps UUID, Timestamps, JSONB, and string types without legacy syntax.

---

## 7. Production Verification & Smoke Test Checklist

Once deployed on the EC2 application instance within VPC `vpc-016d5ab8169fecec8`:

```bash
# 1. Test Network Connectivity from EC2 to RDS
nc -zv safeops-db.cbam0y2e0rb8.ap-south-1.rds.amazonaws.com 5432

# 2. Test TLS Handshake
openssl s_client -connect safeops-db.cbam0y2e0rb8.ap-south-1.rds.amazonaws.com:5432 -starttls postgres

# 3. Start Application Service
sudo systemctl restart safeops
sudo journalctl -u safeops -f

# 4. Verify Actuator Health (Confirms Hikari connection and Flyway migration state)
curl -s http://127.0.0.1:8080/actuator/health | jq .
```
Expected output:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    },
    "ping": {
      "status": "UP"
    }
  }
}
```
