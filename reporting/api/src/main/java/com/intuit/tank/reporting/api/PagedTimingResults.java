package com.intuit.tank.reporting.api;

import java.io.Serializable;
import java.util.List;

import com.intuit.tank.results.TankResult;

public class PagedTimingResults implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object nextToken;
    private List<TankResult> results;

    public PagedTimingResults(Object nextToken, List<TankResult> results) {
        super();
        this.nextToken = nextToken;
        this.results = results;
    }

    /**
     * @return the nextToken
     */
    public Object getNextToken() {
        return nextToken;
    }

    /**
     * @return the results
     */
    public List<TankResult> getResults() {
        return results;
    }

    /**
     * returns if there are more results to fetch.
     * 
     * @return
     */
    public boolean hasMoreResults() {
        return nextToken != null;
    }
}
