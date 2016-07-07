package com.intuit.tank.project;

/*
 * #%L
 * Intuit Tank data model
 * %%
 * Copyright (C) 2011 - 2015 Intuit Inc.
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * ScriptStep represents a single step in a script.
 * 
 * @author dangleton
 * 
 */
public class ScriptStep extends Request implements Comparable<ScriptStep>, Serializable {

    private static final long serialVersionUID = 1L;

    private String scriptGroupName;

    /**
     * @return the scriptGroup
     */
    public String getScriptGroupName() {
        return scriptGroupName;
    }

    /**
     * @param scriptGroupName
     *            the scriptGroup to set
     */
    public void setScriptGroupName(String scriptGroupName) {
        this.scriptGroupName = scriptGroupName;
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int compareTo(ScriptStep o) {
        return new CompareToBuilder().append(getStepIndex(), o.getStepIndex()).toComparison();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).append("uuid", getUuid()).append("scriptGroupName", scriptGroupName)
                .append("name", getName())
                .toString();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ScriptStep)) {
            return false;
        }
        ScriptStep o = (ScriptStep) obj;
        return new EqualsBuilder().append(o.getUuid(), getUuid()).isEquals();
    }

    /**
     * @{inheritDoc
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(29, 45).append(getUuid()).toHashCode();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(ScriptStep step) {
        return new Builder(step);
    }

    public static class Builder extends ScriptStepBuilderBase<Builder> {

        private Builder() {
            super(new ScriptStep());
        }

        private Builder(ScriptStep step) {
            super(step);
        }

        public ScriptStep build() {
            return getInstance();
        }
    }

    static class ScriptStepBuilderBase<GeneratorT extends ScriptStepBuilderBase<GeneratorT>> {
        private ScriptStep instance;

        protected ScriptStepBuilderBase(ScriptStep aInstance) {
            instance = aInstance;
        }

        protected ScriptStep getInstance() {
            return instance;
        }

        //
        // @SuppressWarnings("unchecked")
        // public GeneratorT script(Script aValue) {
        // instance.setScript(aValue);
        //
        // return (GeneratorT) this;
        // }

        @SuppressWarnings("unchecked")
        public GeneratorT scriptGroupName(String aValue) {
            instance.setScriptGroupName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT method(String aValue) {
            instance.setMethod(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT type(String aValue) {
            instance.setType(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT label(String aValue) {
            instance.setLabel(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT data(Set<RequestData> aValue) {
            instance.setData(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addDataElement(RequestData aValue) {
            if (instance.getData() == null) {
                instance.setData(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getData()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT url(String aValue) {
            instance.setUrl(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT result(String aValue) {
            instance.setResult(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT mimetype(String aValue) {
            instance.setMimetype(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT name(String aValue) {
            instance.setName(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT onFail(String aValue) {
            instance.setOnFail(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT requestheaders(Set<RequestData> aValue) {
            instance.setRequestheaders(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addRequestheader(RequestData aValue) {
            if (instance.getRequestheaders() == null) {
                instance.setRequestheaders(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getRequestheaders()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT responseheaders(Set<RequestData> aValue) {
            instance.setResponseheaders(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addResponseheader(RequestData aValue) {
            if (instance.getResponseheaders() == null) {
                instance.setResponseheaders(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getResponseheaders()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT requestCookies(Set<RequestData> aValue) {
            instance.setRequestCookies(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addRequestCooky(RequestData aValue) {
            if (instance.getRequestCookies() == null) {
                instance.setRequestCookies(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getRequestCookies()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT responseCookies(Set<RequestData> aValue) {
            instance.setResponseCookies(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addResponseCooky(RequestData aValue) {
            if (instance.getResponseCookies() == null) {
                instance.setResponseCookies(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getResponseCookies()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT postDatas(Set<RequestData> aValue) {
            instance.setPostDatas(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addPostData(RequestData aValue) {
            if (instance.getPostDatas() == null) {
                instance.setPostDatas(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getPostDatas()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT queryStrings(Set<RequestData> aValue) {
            instance.setQueryStrings(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addQueryString(RequestData aValue) {
            if (instance.getQueryStrings() == null) {
                instance.setQueryStrings(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getQueryStrings()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT stepIndex(int aValue) {
            instance.setStepIndex(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT simplePath(String aValue) {
            instance.setSimplePath(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT hostname(String aValue) {
            instance.setHostname(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT comments(String aValue) {
            instance.setComments(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT protocol(String aValue) {
            instance.setProtocol(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT responseData(Set<RequestData> aValue) {
            instance.setResponseData(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT addResponseDataElement(RequestData aValue) {
            if (instance.getResponseData() == null) {
                instance.setResponseData(new HashSet<RequestData>());
            }

            ((HashSet<RequestData>) instance.getResponseData()).add(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT respFormat(String aValue) {
            instance.setRespFormat(aValue);

            return (GeneratorT) this;
        }

        @SuppressWarnings("unchecked")
        public GeneratorT reqFormat(String aValue) {
            instance.setReqFormat(aValue);

            return (GeneratorT) this;
        }
    }

}
