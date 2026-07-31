package com.intuit.tank.integration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TankUiBrowserIT {

    @Test
    void normalizeSummaryText_replacesNonBreakingWhitespace() {
        assertEquals("Workload Type: Nonlinear",
                TankUiBrowser.normalizeSummaryText("Workload\u00A0Type:\u00A0Nonlinear"));
    }
}
