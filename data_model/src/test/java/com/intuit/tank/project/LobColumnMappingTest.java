package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2026 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Production runs hbm2ddl=update against MySQL. A LOB column that resolves to a smaller type than the
 * existing column makes Hibernate try to shrink it on every startup, so pin the resolved MySQL types.
 */
public class LobColumnMappingTest {

    private static final Class<?>[] ENTITIES = {
            DataFile.class, ScriptFilter.class, ScriptFilterAction.class, ScriptFilterCondition.class,
            ScriptFilterGroup.class, JobVMInstance.class, ColumnPreferences.class, Preferences.class,
            Project.class, RequestData.class, Script.class, ScriptGroup.class, ScriptGroupStep.class,
            ScriptStep.class, VMInstance.class, JobConfiguration.class, JobInstance.class, JobQueue.class,
            JobNotification.class, JobRecipient.class, JobRegion.class, Workload.class,
            TankRevisionInfo.class, SerializedScriptStep.class, ExternalScript.class, SummaryData.class,
            PeriodicData.class, TestPlan.class, User.class, Group.class };

    private static StandardServiceRegistry registry;
    private static Metadata metadata;

    @BeforeAll
    public static void buildMetadata() {
        registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                .applySetting("hibernate.boot.allow_jdbc_metadata_access", "false")
                .applySetting("org.hibernate.envers.audit_table_suffix", "_version")
                .applySetting("org.hibernate.envers.revision_field_name", "rev")
                .applySetting("org.hibernate.envers.revision_type_field_name", "rev_type")
                .build();
        MetadataSources sources = new MetadataSources(registry);
        for (Class<?> entity : ENTITIES) {
            sources.addAnnotatedClass(entity);
        }
        metadata = sources.buildMetadata();
    }

    @AfterAll
    public static void destroyRegistry() {
        StandardServiceRegistryBuilder.destroy(registry);
    }

    @Test
    public void jobDetailsIsLongtext() {
        assertEquals("longtext", sqlType("job_instance", "job_details"));
    }

    @Test
    public void externalScriptIsLongtext() {
        assertEquals("longtext", sqlType("external_script", "script"));
    }

    @Test
    public void externalScriptAuditIsLongtext() {
        assertEquals("longtext", sqlType("external_script_version", "script"));
    }

    private static String sqlType(String tableName, String columnName) {
        Table table = metadata.collectTableMappings().stream()
                .filter(t -> t.getName().equals(tableName))
                .findFirst().orElse(null);
        assertNotNull(table, "no table " + tableName);
        Column column = table.getColumn(Identifier.toIdentifier(columnName));
        assertNotNull(column, "no column " + tableName + "." + columnName);
        return column.getSqlType(metadata).toLowerCase();
    }
}
