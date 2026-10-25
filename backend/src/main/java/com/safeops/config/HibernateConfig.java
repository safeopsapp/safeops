package com.safeops.config;

import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Advanced Hibernate 6 Configuration for SafeOps.
 * Optimizes JDBC batching, statement inspection, and PostgreSQL 18 execution profiles.
 */
@Configuration
public class HibernateConfig {

    private final SafeOpsTenantStatementInspector statementInspector;

    public HibernateConfig(SafeOpsTenantStatementInspector statementInspector) {
        this.statementInspector = statementInspector;
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return (Map<String, Object> hibernateProperties) -> {
            // Enterprise Batching Configuration
            hibernateProperties.put(AvailableSettings.STATEMENT_BATCH_SIZE, 25);
            hibernateProperties.put(AvailableSettings.ORDER_INSERTS, true);
            hibernateProperties.put(AvailableSettings.ORDER_UPDATES, true);
            hibernateProperties.put(AvailableSettings.BATCH_VERSIONED_DATA, true);

            // Statement Inspector for Query Tracking
            hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, statementInspector);

            // Fetch Size & JDBC Optimization
            hibernateProperties.put(AvailableSettings.DEFAULT_BATCH_FETCH_SIZE, 30);
            hibernateProperties.put(AvailableSettings.IN_CLAUSE_PARAMETER_PADDING, true);
        };
    }
}

