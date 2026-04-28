package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.ExternalScript;
import com.intuit.tank.project.RequestData;
import com.intuit.tank.project.Script;
import com.intuit.tank.project.ScriptStep;
import com.intuit.tank.script.RequestDataPhase;
import com.intuit.tank.script.models.*;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScriptServiceUtilTest {

    // =====================================================================
    // scriptToTransferObject / transferObjectToScript round-trip
    // =====================================================================

    @Test
    void scriptToTransferObject_convertsAllFields() {
        Script script = new Script();
        script.setId(1);
        script.setName("TestScript");
        script.setComments("comment");
        script.setCreator("user");
        script.setProductName("product");
        script.setRuntime(120);
        script.setCreated(new Date());
        script.setModified(new Date());
        script.setScriptSteps(new ArrayList<>());

        ScriptTO to = ScriptServiceUtil.scriptToTransferObject(script);

        assertEquals(1, to.getId());
        assertEquals("TestScript", to.getName());
        assertEquals("comment", to.getComments());
        assertEquals("user", to.getCreator());
        assertEquals("product", to.getProductName());
        assertEquals(120, to.getRuntime());
        assertNotNull(to.getCreated());
        assertNotNull(to.getModified());
    }

    @Test
    void transferObjectToScript_convertsAllFields() {
        ScriptTO to = ScriptTO.builder()
                .withId(2)
                .withName("FromTO")
                .withComments("c")
                .withCreator("admin")
                .withProductName("prod")
                .withRuntime(60)
                .withCreated(new Date())
                .withModified(new Date())
                .withSteps(Collections.emptyList())
                .build();

        Script script = ScriptServiceUtil.transferObjectToScript(to);

        assertEquals(2, script.getId());
        assertEquals("FromTO", script.getName());
        assertEquals("c", script.getComments());
        assertEquals("admin", script.getCreator());
    }

    @Test
    void transferObjectToScript_handlesNullId() {
        ScriptTO to = ScriptTO.builder()
                .withName("NoId")
                .withSteps(Collections.emptyList())
                .build();

        Script script = ScriptServiceUtil.transferObjectToScript(to);
        assertEquals(0, script.getId());
    }

    // =====================================================================
    // scriptStepToTransferObject / transferObjectToScriptStep round-trip
    // =====================================================================

    @Test
    void scriptStepToTransferObject_convertsStep() {
        ScriptStep step = new ScriptStep();
        step.setType("request");
        step.setHostname("example.com");
        step.setSimplePath("/api");
        step.setMethod("GET");
        step.setProtocol("https");
        step.setStepIndex(5);
        step.setLabel("Step Label");
        step.setLoggingKey("login");

        ScriptStepTO to = ScriptServiceUtil.scriptStepToTransferObject(step);

        assertEquals("request", to.getType());
        assertEquals("example.com", to.getHostname());
        assertEquals("/api", to.getSimplePath());
        assertEquals("GET", to.getMethod());
        assertEquals("https", to.getProtocol());
        assertEquals(5, to.getStepIndex());
        assertEquals("login", to.getLoggingKey());
    }

    @Test
    void transferObjectToScriptStep_convertsBack() {
        ScriptStepTO to = ScriptStepTO.builder()
                .withType("request")
                .withHostname("example.com")
                .withSimplePath("/path")
                .withMethod("POST")
                .withProtocol("http")
                .withStepIndex(3)
                .withName("step-name")
                .withComments("comment")
                .build();

        ScriptStep step = ScriptServiceUtil.transferObjectToScriptStep(to);

        assertEquals("request", step.getType());
        assertEquals("example.com", step.getHostname());
        assertEquals("/path", step.getSimplePath());
        assertEquals("POST", step.getMethod());
        assertEquals(3, step.getStepIndex());
    }

    @Test
    void scriptStep_responseEncodeDecode_roundTrip() {
        ScriptStep step = new ScriptStep();
        step.setType("request");
        step.setResponse("Hello World & <test>");

        ScriptStepTO to = ScriptServiceUtil.scriptStepToTransferObject(step);
        assertNotNull(to.getResponse());

        ScriptStep back = ScriptServiceUtil.transferObjectToScriptStep(to);
        assertEquals("Hello World & <test>", back.getResponse());
    }

    // =====================================================================
    // requestDataToTransferObject / transferObjectToRequestData
    // =====================================================================

    @Test
    void requestDataToTransferObject_convertsData() {
        RequestData rd = new RequestData();
        rd.setKey("Content-Type");
        rd.setValue("application/json");
        rd.setType("header");
        rd.setPhase(RequestDataPhase.POST_REQUEST);

        StepDataTO to = ScriptServiceUtil.requestDataToTransferObject(rd);

        assertEquals("Content-Type", to.getKey());
        assertEquals("application/json", to.getValue());
        assertEquals("header", to.getType());
        assertEquals("POST_REQUEST", to.getPhase());
    }

    @Test
    void transferObjectToRequestData_convertsBack() {
        StepDataTO to = StepDataTO.builder()
                .withKey("Accept")
                .withValue("text/html")
                .withType("header")
                .withPhase("POST_REQUEST")
                .build();

        RequestData rd = ScriptServiceUtil.transferObjectToRequestData(to);

        assertEquals("Accept", rd.getKey());
        assertEquals("text/html", rd.getValue());
        assertEquals(RequestDataPhase.POST_REQUEST, rd.getPhase());
    }

    @Test
    void transferObjectToRequestData_handlesNullPhase() {
        StepDataTO to = StepDataTO.builder()
                .withKey("key")
                .withValue("val")
                .withType("type")
                .build();

        RequestData rd = ScriptServiceUtil.transferObjectToRequestData(to);
        assertEquals(RequestDataPhase.POST_REQUEST, rd.getPhase());
    }

    @Test
    void transferObjectToRequestData_handlesInvalidPhase() {
        StepDataTO to = StepDataTO.builder()
                .withKey("key")
                .withValue("val")
                .withPhase("INVALID_PHASE")
                .build();

        RequestData rd = ScriptServiceUtil.transferObjectToRequestData(to);
        assertEquals(RequestDataPhase.POST_REQUEST, rd.getPhase());
    }

    // =====================================================================
    // scriptToScriptDescription / scriptDescriptionToScript
    // =====================================================================

    @Test
    void scriptToScriptDescription_convertsBasicFields() {
        Script script = new Script();
        script.setId(10);
        script.setName("MyScript");
        script.setCreator("creator");
        script.setCreated(new Date());
        script.setModified(new Date());

        ScriptDescription desc = ScriptServiceUtil.scriptToScriptDescription(script);

        assertEquals(10, desc.getId());
        assertEquals("MyScript", desc.getName());
        assertEquals("creator", desc.getCreator());
    }

    @Test
    void scriptDescriptionToScript_convertsBack() {
        ScriptDescription desc = ScriptDescription.builder()
                .withId(5)
                .withName("FromDesc")
                .withCreator("admin")
                .withRuntime(30)
                .build();

        Script script = ScriptServiceUtil.scriptDescriptionToScript(desc);

        assertEquals(5, script.getId());
        assertEquals("FromDesc", script.getName());
        assertEquals(30, script.getRuntime());
    }

    // =====================================================================
    // externalScriptToTO / TOToExternalScript
    // =====================================================================

    @Test
    void externalScriptToTO_convertsAllFields() {
        ExternalScript es = new ExternalScript();
        es.setId(3);
        es.setName("ExtScript");
        es.setCreator("user");
        es.setProductName("product");
        es.setScript("function test() {}");
        es.setCreated(new Date());
        es.setModified(new Date());

        ExternalScriptTO to = ScriptServiceUtil.externalScriptToTO(es);

        assertEquals(3, to.getId());
        assertEquals("ExtScript", to.getName());
        assertEquals("function test() {}", to.getScript());
    }

    @Test
    void TOToExternalScript_convertsAllFields() {
        ExternalScriptTO to = ExternalScriptTO.builder()
                .withId(7)
                .withName("ToEntity")
                .withCreator("admin")
                .withProductName("prod")
                .withScript("code here")
                .withCreated(new Date())
                .withModified(new Date())
                .build();

        ExternalScript es = ScriptServiceUtil.TOToExternalScript(to);

        assertEquals(7, es.getId());
        assertEquals("ToEntity", es.getName());
        assertEquals("code here", es.getScript());
    }

    @Test
    void TOToExternalScript_handlesNullCreator() {
        ExternalScriptTO to = ExternalScriptTO.builder()
                .withId(0)
                .withName("NoCreator")
                .build();

        ExternalScript es = ScriptServiceUtil.TOToExternalScript(to);
        assertEquals("", es.getCreator());
    }

    @Test
    void TOToExternalScript_skipsTimestampsForNewScript() {
        ExternalScriptTO to = ExternalScriptTO.builder()
                .withId(0)
                .withName("New")
                .withCreated(new Date())
                .withModified(new Date())
                .build();

        ExternalScript es = ScriptServiceUtil.TOToExternalScript(to);
        // id=0 means new, so created/modified should NOT be copied
        assertNull(es.getCreated());
        assertNull(es.getModified());
    }

    // =====================================================================
    // copy
    // =====================================================================

    @Test
    void copy_createsIndependentCopy() {
        ScriptStepTO original = ScriptStepTO.builder()
                .withType("request")
                .withHostname("example.com")
                .withStepIndex(1)
                .build();

        ScriptStepTO copy = ScriptServiceUtil.copy(original);

        assertEquals(original.getHostname(), copy.getHostname());
        assertEquals(original.getStepIndex(), copy.getStepIndex());
    }
}
