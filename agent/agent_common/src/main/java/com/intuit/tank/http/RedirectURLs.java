package com.intuit.tank.http;

import java.util.ArrayList;
import java.util.List;

public class RedirectURLs {
    private final List<String> locations = new ArrayList<>();

    public void add(String location) {
        this.locations.add(location);
    }

    public List<String> getLocations() {
        return new ArrayList<>(this.locations);
    }

    public boolean isEmpty() {
        return this.locations.isEmpty();
    }

    public void clear() {
        this.locations.clear();
    }
}
