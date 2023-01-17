package com.intuit.tank.vm.settings;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Purpose:
 *
 * @author : atayal
 **/
public class ReportingConfigTest {
    HierarchicalConfiguration config = new HierarchicalConfiguration();
    ReportingConfig reportingConfig = null;

    @BeforeEach
    public void init() {
        reportingConfig = new ReportingConfig(config);
    }

    @Test
    public void testReportingConfig() {
        assertEquals("com.intuit.tank.reporting.db.DatabaseResultsReporter", reportingConfig.getReporterClass());
        assertEquals("com.intuit.tank.reporting.db.DatabaseResultsReader", reportingConfig.getReaderClass());
        assertNull(reportingConfig.getProviderConfig());
    }
}
