package com.intuit.tank.rest.mvc.rest.util;

import com.intuit.tank.project.*;
import com.intuit.tank.util.ScriptFilterType;
import com.intuit.tank.vm.api.enumerated.ScriptFilterActionType;
import com.intuit.tank.vm.script.util.AddActionScope;
import com.intuit.tank.vm.script.util.RemoveActionScope;
import com.intuit.tank.vm.script.util.ReplaceActionScope;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ScriptFilterUtilTest {

    // =====================================================================
    // applyFilter - Remove request action
    // =====================================================================

    @Test
    void applyFilter_removeRequest_removesMatchingStep() {
        ScriptStep step = createRequestStep("example.com", "/api/test");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.request.getValue(), null, null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(steps.isEmpty());
    }

    @Test
    void applyFilter_removeRequest_keepsNonMatchingStep() {
        ScriptStep step = createRequestStep("other.com", "/api/test");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.request.getValue(), null, null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals(1, steps.size());
    }

    // =====================================================================
    // applyFilter - Replace actions
    // =====================================================================

    @Test
    void applyFilter_replacePath_updatesPath() {
        ScriptStep step = createRequestStep("example.com", "/old/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.path.getValue(), null, "/new/path"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("/new/path", step.getSimplePath());
    }

    @Test
    void applyFilter_replaceHost_updatesHostname() {
        ScriptStep step = createRequestStep("old.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "old.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.host.getValue(), null, "new.com"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("new.com", step.getHostname());
    }

    @Test
    void applyFilter_replaceOnFail_updatesOnFail() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.onfail.getValue(), null, "abort"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("abort", step.getOnFail());
    }

    // =====================================================================
    // applyFilter - Add actions
    // =====================================================================

    @Test
    void applyFilter_addRequestHeader_addsHeader() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.requestHeader.getValue(), "Authorization", "Bearer token123"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getRequestheaders().stream()
                .anyMatch(rd -> rd.getKey().equals("Authorization") && rd.getValue().equals("Bearer token123")));
    }

    @Test
    void applyFilter_addRequestCookie_addsCookie() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.requestCookie.getValue(), "session", "abc123"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getRequestCookies().stream()
                .anyMatch(rd -> rd.getKey().equals("session")));
    }

    @Test
    void applyFilter_addThinkTime_insertsThinkTimeStep() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.thinkTime.getValue(), null, "1000,5000"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals(2, steps.size());
        assertEquals("thinkTime", steps.get(1).getType());
    }

    @Test
    void applyFilter_addSleepTime_insertsSleepStep() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.sleepTime.getValue(), null, "2000"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals(2, steps.size());
        assertEquals("sleep", steps.get(1).getType());
    }

    // =====================================================================
    // Condition types
    // =====================================================================

    @Test
    void applyFilter_conditionMatches_usesRegex() {
        ScriptStep step = createRequestStep("api.example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Matches", "api\\.example\\.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.host.getValue(), null, "new.com"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("new.com", step.getHostname());
    }

    @Test
    void applyFilter_conditionDoesNotContain_excludesMatching() {
        ScriptStep step1 = createRequestStep("example.com", "/path");
        ScriptStep step2 = createRequestStep("other.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step1, step2));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Does not contain", "example"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.request.getValue(), null, null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals(1, steps.size());
        assertEquals("example.com", steps.get(0).getHostname());
    }

    @Test
    void applyFilter_conditionStartsWith_matchesPrefix() {
        ScriptStep step = createRequestStep("example.com", "/api/v2/test");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Path", "Starts with", "/api/v2"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.path.getValue(), null, "/api/v3/test"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("/api/v3/test", step.getSimplePath());
    }

    // =====================================================================
    // allConditionsMustPass
    // =====================================================================

    @Test
    void applyFilter_allConditionsMustPass_requiresAllTrue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilterCondition c1 = createCondition("Hostname", "Contains", "example.com");
        ScriptFilterCondition c2 = createCondition("Path", "Contains", "/other"); // won't match

        ScriptFilter filter = new ScriptFilter();
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(true);
        filter.getConditions().add(c1);
        filter.getConditions().add(c2);
        filter.getActions().add(createAction(ScriptFilterActionType.remove, RemoveActionScope.request.getValue(), null, null));

        ScriptFilterUtil.applyFilter(filter, steps);
        // Should NOT be removed because second condition doesn't match
        assertEquals(1, steps.size());
    }

    // =====================================================================
    // filterOnFieldSets
    // =====================================================================

    @Test
    void filterOnFieldSets_matchesOnPayload() {
        ScriptStep step = createRequestStep("example.com", "/path");
        step.setPayload("some body content with key=value");

        ScriptFilterCondition condition = createCondition("Post Data", "Contains", "key=value");

        boolean result = ScriptFilterUtil.filterOnFieldSets(
                step.getPostDatas(), condition, step, List.of(step));
        assertTrue(result);
    }

    @Test
    void filterOnFieldSets_returnsFalse_whenFieldsNull_andNoPayload() {
        ScriptStep step = createRequestStep("example.com", "/path");
        ScriptFilterCondition condition = createCondition("Post Data", "Contains", "anything");

        boolean result = ScriptFilterUtil.filterOnFieldSets(null, condition, step, List.of(step));
        assertFalse(result);
    }

    // =====================================================================
    // Remove actions on specific scopes
    // =====================================================================

    @Test
    void applyFilter_removeQueryString_removesMatchingKey() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData qs = new RequestData();
        qs.setKey("debug");
        qs.setValue("true");
        step.setQueryStrings(new HashSet<>(Set.of(qs)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.queryString.getValue(), "debug", null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getQueryStrings().isEmpty());
    }

    // =====================================================================
    // Replace actions on specific scopes
    // =====================================================================

    @Test
    void applyFilter_replaceRequestHeader_updatesValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData header = new RequestData();
        header.setKey("Authorization");
        header.setValue("old-token");
        step.setRequestheaders(new HashSet<>(Set.of(header)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.requestHeader.getValue(), "Authorization", "new-token"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("new-token", header.getValue());
    }

    @Test
    void applyFilter_replaceRequestCookie_updatesValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData cookie = new RequestData();
        cookie.setKey("session");
        cookie.setValue("old-session");
        step.setRequestCookies(new HashSet<>(Set.of(cookie)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.requestCookie.getValue(), "session", "new-session"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("new-session", cookie.getValue());
    }

    @Test
    void applyFilter_replaceQueryString_updatesValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData qs = new RequestData();
        qs.setKey("page");
        qs.setValue("1");
        step.setQueryStrings(new HashSet<>(Set.of(qs)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.queryString.getValue(), "page", "2"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("2", qs.getValue());
    }

    @Test
    void applyFilter_replacePostData_updatesValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData pd = new RequestData();
        pd.setKey("username");
        pd.setValue("oldUser");
        step.setPostDatas(new HashSet<>(Set.of(pd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.postData.getValue(), "username", "newUser"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("newUser", pd.getValue());
    }

    @Test
    void applyFilter_replaceValidation_updatesTypeAndValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("status");
        rd.setValue("200");
        rd.setType("bodyValidation");
        step.setResponseData(new HashSet<>(Set.of(rd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.validation.getValue(), "status", "201"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("201", rd.getValue());
        assertEquals("bodyValidation", rd.getType());
    }

    @Test
    void applyFilter_replaceAssignment_updatesTypeAndValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("token");
        rd.setValue("=oldAssignment");
        rd.setType("bodyAssignment");
        step.setResponseData(new HashSet<>(Set.of(rd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.assignment.getValue(), "token", "=newAssignment"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("=newAssignment", rd.getValue());
        assertEquals("bodyAssignment", rd.getType());
    }

    @Test
    void applyFilter_replaceResponseData_updatesTypeBasedOnValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("field");
        rd.setValue("oldVal");
        step.setResponseData(new HashSet<>(Set.of(rd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, "responseData", "field", "=assignment"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("=assignment", rd.getValue());
        assertEquals("bodyAssignment", rd.getType());
    }

    @Test
    void applyFilter_replaceResponseData_validationType() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("body");
        rd.setValue("old");
        step.setResponseData(new HashSet<>(Set.of(rd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, "responseData", "body", "==equality"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("==equality", rd.getValue());
        assertEquals("bodyValidation", rd.getType()); // starts with == so it's validation
    }

    // =====================================================================
    // Add actions on specific scopes
    // =====================================================================

    @Test
    void applyFilter_addQueryString_addsEntry() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.queryString.getValue(), "newParam", "value"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getQueryStrings().stream().anyMatch(rd -> "newParam".equals(rd.getKey())));
    }

    @Test
    void applyFilter_addPostData_addsEntry() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.postData.getValue(), "field", "value"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getPostDatas().stream().anyMatch(rd -> "field".equals(rd.getKey())));
    }

    @Test
    void applyFilter_addResponseData_addsEntry() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.responseData.getValue(), "check", "expectedVal"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getResponseData().stream().anyMatch(rd -> "check".equals(rd.getKey())));
    }

    @Test
    void applyFilter_addValidation_addsBodyValidation() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.validation.getValue(), "status", "200"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getResponseData().stream()
                .anyMatch(rd -> "status".equals(rd.getKey()) && "bodyValidation".equals(rd.getType())));
    }

    @Test
    void applyFilter_addAssignment_addsBodyAssignment() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.add, AddActionScope.assignment.getValue(), "token", "=extractToken"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getResponseData().stream()
                .anyMatch(rd -> "token".equals(rd.getKey()) && "bodyAssignment".equals(rd.getType())));
    }

    // =====================================================================
    // Remove actions on additional scopes
    // =====================================================================

    @Test
    void applyFilter_removeRequestCookie_removesMatchingKey() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData cookie = new RequestData();
        cookie.setKey("session");
        cookie.setValue("abc");
        step.setRequestCookies(new HashSet<>(Set.of(cookie)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.requestCookie.getValue(), "session", null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getRequestCookies().isEmpty());
    }

    @Test
    void applyFilter_removeRequestHeader_removesMatchingKey() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData header = new RequestData();
        header.setKey("X-Custom");
        header.setValue("val");
        step.setRequestheaders(new HashSet<>(Set.of(header)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.requestHeader.getValue(), "X-Custom", null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getRequestheaders().isEmpty());
    }

    @Test
    void applyFilter_removePostData_removesMatchingKey() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData pd = new RequestData();
        pd.setKey("field");
        pd.setValue("val");
        step.setPostDatas(new HashSet<>(Set.of(pd)));

        List<ScriptStep> steps = new ArrayList<>(List.of(step));
        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.remove, RemoveActionScope.postData.getValue(), "field", null));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertTrue(step.getPostDatas().isEmpty());
    }

    // =====================================================================
    // Condition - allConditionsMustPass = false (OR mode)
    // =====================================================================

    @Test
    void applyFilter_anyConditionPasses_matchesOnFirst() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilterCondition c1 = createCondition("Hostname", "Contains", "example.com");
        ScriptFilterCondition c2 = createCondition("Path", "Contains", "/other"); // won't match

        ScriptFilter filter = new ScriptFilter();
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(false); // OR mode
        filter.getConditions().add(c1);
        filter.getConditions().add(c2);
        filter.getActions().add(createAction(ScriptFilterActionType.remove, RemoveActionScope.request.getValue(), null, null));

        ScriptFilterUtil.applyFilter(filter, steps);
        // Should be removed because first condition matches (OR mode)
        assertTrue(steps.isEmpty());
    }

    // =====================================================================
    // Condition - Exist / Does not exist
    // =====================================================================

    @Test
    void applyFilter_conditionExist_matchesNonEmptyHostname() {
        ScriptStep step = createRequestStep("example.com", "/path");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Exist", ""),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.host.getValue(), null, "replaced.com"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("replaced.com", step.getHostname());
    }

    @Test
    void applyFilter_conditionDoesNotExist_matchesEmptyPath() {
        ScriptStep step = createRequestStep("example.com", "");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Path", "Does not exist", ""),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.path.getValue(), null, "/default"));

        ScriptFilterUtil.applyFilter(filter, steps);
        assertEquals("/default", step.getSimplePath());
    }

    // =====================================================================
    // filterOnFieldSets - additional coverage
    // =====================================================================

    @Test
    void filterOnFieldSets_matchesOnFieldKeyValue() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("username");
        rd.setValue("admin");
        step.setPostDatas(new HashSet<>(Set.of(rd)));

        ScriptFilterCondition condition = createCondition("Post Data", "Contains", "username=admin");

        boolean result = ScriptFilterUtil.filterOnFieldSets(step.getPostDatas(), condition, step, List.of(step));
        assertTrue(result);
    }

    @Test
    void filterOnFieldSets_matchesCondition_matchesRegex() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("id");
        rd.setValue("12345");
        step.setQueryStrings(new HashSet<>(Set.of(rd)));

        ScriptFilterCondition condition = createCondition("Query String", "Matches", "id=\\d+");

        boolean result = ScriptFilterUtil.filterOnFieldSets(step.getQueryStrings(), condition, step, List.of(step));
        assertTrue(result);
    }

    @Test
    void filterOnFieldSets_startsWith_matchesPrefix() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("token");
        rd.setValue("abc123");
        step.setPostDatas(new HashSet<>(Set.of(rd)));

        ScriptFilterCondition condition = createCondition("Post Data", "Starts with", "token=abc");

        boolean result = ScriptFilterUtil.filterOnFieldSets(step.getPostDatas(), condition, step, List.of(step));
        assertTrue(result);
    }

    @Test
    void filterOnFieldSets_doesNotContain_returnsTrueWhenNotPresent() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("other");
        rd.setValue("value");
        step.setPostDatas(new HashSet<>(Set.of(rd)));

        ScriptFilterCondition condition = createCondition("Post Data", "Does not contain", "secret=value");

        // "Does not contain" returns true when the value is NOT found
        boolean result = ScriptFilterUtil.filterOnFieldSets(step.getPostDatas(), condition, step, List.of(step));
        assertTrue(result);
    }

    @Test
    void filterOnFieldSets_exist_returnsTrueWhenFieldsPresent() {
        ScriptStep step = createRequestStep("example.com", "/path");
        RequestData rd = new RequestData();
        rd.setKey("data");
        rd.setValue("val");
        step.setPostDatas(new HashSet<>(Set.of(rd)));

        ScriptFilterCondition condition = createCondition("Post Data", "Exist", "");

        boolean result = ScriptFilterUtil.filterOnFieldSets(step.getPostDatas(), condition, step, List.of(step));
        assertTrue(result);
    }

    // =====================================================================
    // applyFilters with collection of ScriptFilter (static method overload)
    // =====================================================================

    @Test
    void applyFilters_withCollection_appliesFiltersToSteps() {
        ScriptStep step = createRequestStep("example.com", "/old");
        List<ScriptStep> steps = new ArrayList<>(List.of(step));

        ScriptFilter filter = createFilter(
                createCondition("Hostname", "Contains", "example.com"),
                createAction(ScriptFilterActionType.replace, ReplaceActionScope.path.getValue(), null, "/new"));

        List<ScriptStep> result = ScriptFilterUtil.applyFilters(List.of(filter), steps);
        assertEquals("/new", result.get(0).getSimplePath());
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    private static ScriptStep createRequestStep(String hostname, String path) {
        ScriptStep step = new ScriptStep();
        step.setType("request");
        step.setHostname(hostname);
        step.setSimplePath(path);
        step.setRequestheaders(new HashSet<>());
        step.setRequestCookies(new HashSet<>());
        step.setQueryStrings(new HashSet<>());
        step.setPostDatas(new HashSet<>());
        step.setResponseData(new HashSet<>());
        return step;
    }

    private static ScriptFilterCondition createCondition(String scope, String condition, String value) {
        ScriptFilterCondition c = new ScriptFilterCondition();
        c.setScope(scope);
        c.setCondition(condition);
        c.setValue(value);
        return c;
    }

    private static ScriptFilterAction createAction(ScriptFilterActionType action, String scope, String key, String value) {
        ScriptFilterAction a = new ScriptFilterAction();
        a.setAction(action);
        a.setScope(scope);
        a.setKey(key);
        a.setValue(value);
        return a;
    }

    private static ScriptFilter createFilter(ScriptFilterCondition condition, ScriptFilterAction action) {
        ScriptFilter filter = new ScriptFilter();
        filter.setFilterType(ScriptFilterType.INTERNAL);
        filter.setAllConditionsMustPass(true);
        filter.getConditions().add(condition);
        filter.getActions().add(action);
        return filter;
    }
}
