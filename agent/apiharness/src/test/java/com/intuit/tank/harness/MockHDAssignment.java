package com.intuit.tank.harness;

import com.intuit.tank.harness.data.AssignmentData;
import com.intuit.tank.harness.data.HDAssignment;

import java.util.ArrayList;
import java.util.List;

public class MockHDAssignment extends HDAssignment {

    private List<AssignmentData> headerVariable = new ArrayList<AssignmentData>();

    private List<AssignmentData> cookieVariable = new ArrayList<AssignmentData>();

    private List<AssignmentData> bodyVariable = new ArrayList<AssignmentData>();

    @Override
    public List<AssignmentData> getHeaderVariable() {
        return headerVariable;
    }

    @Override
    public List<AssignmentData> getCookieVariable() {
        return cookieVariable;
    }

    @Override
    public List<AssignmentData> getBodyVariable() {
        return bodyVariable;
    }

    public void addHeaderVariable(AssignmentData assignmentData) {
        this.headerVariable.add(assignmentData);
    }

    public void addCookieVariable(AssignmentData assignmentData) {
        this.cookieVariable.add(assignmentData);
    }

    public void addBodyVariable(AssignmentData assignmentData){
        this.bodyVariable.add(assignmentData);
    }
}

