package com.rieke.bmore.catan.base.resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 9/1/2017.
 */
public class ResourceSelection {

    private int count = 0;
    private Map<String, Integer> resourceSelection = new HashMap<>();

    public ResourceSelection() {
    }

    public ResourceSelection(int count, Map<String, Integer> resourceSelection) {
        this.count = count;
        this.resourceSelection = resourceSelection;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Map<String, Integer> getResourceSelection() {
        return resourceSelection;
    }

    public void setResourceSelection(Map<String, Integer> resourceSelection) {
        this.resourceSelection = resourceSelection;
    }
}
