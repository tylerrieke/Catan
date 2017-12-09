package com.rieke.bmore.catan.base.board;

import java.util.Map;

/**
 * Created by tcrie on 11/8/2017.
 */
public class LegendEntry {
    private String pieceName;
    private Map<String,Integer> cost;

    public LegendEntry(String pieceName, Map<String, Integer> cost) {
        this.pieceName = pieceName;
        this.cost = cost;
    }

    public String getPieceName() {
        return pieceName;
    }

    public void setPieceName(String pieceName) {
        this.pieceName = pieceName;
    }

    public Map<String, Integer> getCost() {
        return cost;
    }

    public void setCost(Map<String, Integer> cost) {
        this.cost = cost;
    }
}
