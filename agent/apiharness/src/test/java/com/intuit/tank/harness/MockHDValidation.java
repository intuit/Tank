package com.intuit.tank.harness;

import com.intuit.tank.harness.data.HDValidation;
import com.intuit.tank.harness.data.ValidationData;

import java.util.ArrayList;
import java.util.List;

public class MockHDValidation extends HDValidation {
    private List<ValidationData> headerValidation = new ArrayList<ValidationData>();

    private List<ValidationData> cookieValidation = new ArrayList<ValidationData>();

    private List<ValidationData> bodyValidation = new ArrayList<ValidationData>();

    public void addHeaderValidation(ValidationData headerValidationData) {
        this.headerValidation.add(headerValidationData);
    }

    public void addCookieValidation(ValidationData cookieValidationData) {
        this.cookieValidation.add(cookieValidationData);
    }

    public void addBodyValidation(ValidationData bodyValidationData) {
        this.bodyValidation.add(bodyValidationData);
    }

    @Override
    public List<ValidationData> getHeaderValidation() {
        return headerValidation;
    }

    @Override
    public List<ValidationData> getCookieValidation() {
        return cookieValidation;
    }

    @Override
    public List<ValidationData> getBodyValidation() {
        return bodyValidation;
    }
}
