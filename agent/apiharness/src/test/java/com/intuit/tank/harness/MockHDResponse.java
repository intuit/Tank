package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDAssignment;
import com.intuit.tank.harness.data.HDResponse;
import com.intuit.tank.harness.data.HDValidation;

import jakarta.annotation.Nonnull;

public class MockHDResponse extends HDResponse {
    private HDValidation validation = new HDValidation();

    private HDAssignment assignment = new HDAssignment();

    public void setValidation(@Nonnull HDValidation validation) {
        this.validation = validation;
    }

    public void setAssignment(@Nonnull HDAssignment assignment) { this.assignment = assignment; }

    @Override
    public HDValidation getValidation() {
        return validation;
    }

    @Override
    public HDAssignment getAssignment() {
        return assignment;
    }
}
