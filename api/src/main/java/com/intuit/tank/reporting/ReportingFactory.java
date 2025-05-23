package com.intuit.tank.reporting;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.intuit.tank.vm.settings.ReportingConfig;
import com.intuit.tank.vm.settings.TankConfig;

public final class ReportingFactory {

    private static final Logger LOG = LogManager.getLogger(ReportingFactory.class);

    private ReportingFactory() {

    }

    /**
     * 
     * @return
     */
    public static final ResultsReporter getResultsReporter() {
        ResultsReporter ret = null;
        try {
            ReportingConfig config = new TankConfig().getReportingConfig();
            String providerClass = config.getReporterClass();
            ret = (ResultsReporter) Class.forName(providerClass).getDeclaredConstructor().newInstance();
            HierarchicalConfiguration providerConfig = config.getProviderConfig();
            ret.config(providerConfig);
        } catch (Exception e) {
            LOG.error("Error instantiating reporter");
            ret = new DummyResultsReporter();
        }
        return ret;
    }
    /**
     * 
     * @return
     */
    public static final ResultsReader getResultsReader() {
        ResultsReader ret = null;
        try {
            ReportingConfig config = new TankConfig().getReportingConfig();
            String providerClass = config.getReaderClass();
            ret = (ResultsReader) Class.forName(providerClass).getDeclaredConstructor().newInstance();
            HierarchicalConfiguration providerConfig = config.getProviderConfig();
            ret.config(providerConfig);
        } catch (Exception e) {
            LOG.error("Error instantiating reporter");
//            ret = new DummyResultsReader();
        }
        return ret;
    }

}
