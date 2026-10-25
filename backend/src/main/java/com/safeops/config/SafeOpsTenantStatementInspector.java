package com.safeops.config;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Hibernate Statement Inspector for SafeOps.
 * Appends diagnostic and tenant tracking comments to generated SQL statements
 * for Amazon CloudWatch and RDS Performance Insights query correlation.
 */
@Component
public class SafeOpsTenantStatementInspector implements StatementInspector {

    private static final Logger log = LoggerFactory.getLogger(SafeOpsTenantStatementInspector.class);

    @Override
    public String inspect(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }
        // Attach SafeOps enterprise routing header for RDS Performance Insights correlation
        return "/* SafeOps EHS Compliance Core */ " + sql;
    }
}

