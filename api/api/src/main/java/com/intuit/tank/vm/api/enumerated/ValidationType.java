package com.intuit.tank.vm.api.enumerated;

/*
 * #%L
 * Intuit Tank Api
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import com.intuit.tank.vm.common.ValidationTypeConstants;

public enum ValidationType {

    equals(ValidationTypeConstants.EQUALS, ValidationTypeConstants.REPRESENTATION_EQUALS),
    notequals(ValidationTypeConstants.NOTEQUALS, ValidationTypeConstants.REPRESENTATION_NOT_EQUALS),
    empty(ValidationTypeConstants.EMPTY, ValidationTypeConstants.REPRESENTATION_EMPTY),
    notempty(ValidationTypeConstants.NOTEMPTY, ValidationTypeConstants.REPRESENTATION_NOTEMPTY),
    contains(ValidationTypeConstants.CONTAINS, ValidationTypeConstants.REPRESENTATION_CONTAINS),
    doesnotcontain(ValidationTypeConstants.DOESNOTCONTAIN, ValidationTypeConstants.REPRESENTATION_DOESNOTCONTAIN),
    lessthan(ValidationTypeConstants.LESS_THAN, ValidationTypeConstants.REPRESENTATION_LESS_THAN),
    greaterthan(ValidationTypeConstants.GREATER_THAN, ValidationTypeConstants.REPRESENTATION_GREATER_THAN);

    private String value;
    private String representation;

    private ValidationType(String value, String representation) {
        this.value = value;
        this.representation = representation;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    public String getRepresentation() {
        return representation;
    }

    public static ValidationType getValidationType(String value) {
        if (value.startsWith(ValidationTypeConstants.EQUALS)) {
            return ValidationType.equals;
        } else if (value.startsWith(ValidationTypeConstants.NOTEQUALS)) {
            return ValidationType.notequals;
        } else if (value.startsWith(ValidationTypeConstants.EMPTY)) {
            return ValidationType.empty;
        } else if (value.startsWith(ValidationTypeConstants.NOTEMPTY)) {
            return ValidationType.notempty;
        } else if (value.startsWith(ValidationTypeConstants.CONTAINS)) {
            return ValidationType.contains;
        } else if (value.startsWith(ValidationTypeConstants.DOESNOTCONTAIN)) {
            return ValidationType.doesnotcontain;
        } else if (value.startsWith(ValidationTypeConstants.GREATER_THAN)) {
            return ValidationType.greaterthan;
        } else if (value.startsWith(ValidationTypeConstants.LESS_THAN)) {
            return ValidationType.lessthan;
        } else {
            return ValidationType.equals;
        }
    }

    public static ValidationType getValidationTypeFromRepresentation(String representation) {
        if (representation.equalsIgnoreCase(ValidationTypeConstants.EQUALS)) {
            return ValidationType.equals;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.NOTEQUALS)) {
            return ValidationType.notequals;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_NOT_EQUALS)) {
            return ValidationType.notequals;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_EMPTY)) {
            return ValidationType.empty;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_NOTEMPTY)) {
            return ValidationType.notempty;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_CONTAINS)) {
            return ValidationType.contains;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_DOESNOTCONTAIN)) {
            return ValidationType.doesnotcontain;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_LESS_THAN)) {
            return ValidationType.lessthan;
        } else if (representation.equalsIgnoreCase(ValidationTypeConstants.REPRESENTATION_GREATER_THAN)) {
            return ValidationType.greaterthan;
        } else {
            return ValidationType.equals;
        }
    }

}
