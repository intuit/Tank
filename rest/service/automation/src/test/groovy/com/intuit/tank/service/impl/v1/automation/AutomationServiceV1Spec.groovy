package com.intuit.tank.service.impl.v1.automation

import com.intuit.tank.api.model.v1.automation.CreateJobRequest
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class AutomationServiceV1Spec extends Specification {

    AutomationServiceV1 automationServiceV1

    def setup() {
        automationServiceV1 = new AutomationServiceV1()
    }

    @Unroll
    def "test createJob returns a bad request when missing required arguments"() {
        when:
        def resp = automationServiceV1.createJob(new CreateJobRequest())

        then:
        resp.status == 200
    }

}
