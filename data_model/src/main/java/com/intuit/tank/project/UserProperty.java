package com.intuit.tank.project;

public enum UserProperty {
    PROPERTY_NAME("name"),
    PROPERTY_GROUPS("groups"),
    PROPERTY_EMAIL("email"),
    PROPERTY_TOKEN("apiToken");

    public final String value;

    UserProperty(String value) {
        this.value = value;
    }
}
