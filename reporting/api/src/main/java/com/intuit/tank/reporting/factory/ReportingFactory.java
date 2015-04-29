package com.intuit.tank.reporting.factory;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.intuit.tank.reporting.api.DummyResultsReporter;
import com.intuit.tank.reporting.api.ResultsReporter;
import com.intuit.tank.vm.settings.ReportingConfig;
import com.intuit.tank.vm.settings.TankConfig;

public final class ReportingFactory {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportingFactory.class);

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
            String providerClass = config.getProviderClass();
            ret = (ResultsReporter) Class.forName(providerClass).newInstance();
            HierarchicalConfiguration providerConfig = config.getProviderConfig();
            ret.config(providerConfig);
        } catch (Exception e) {
            LOG.error("Error instantiating reporter");
            ret = new DummyResultsReporter();
        }
        return ret;
    }

}
