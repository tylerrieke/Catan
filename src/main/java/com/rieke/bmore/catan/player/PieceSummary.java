package com.rieke.bmore.catan.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.pieces.Piece;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 8/21/2017.
 */
public class PieceSummary {
    private Class<? extends Piece> type;
    private int count;
    private boolean buildable;
    private Map<String,Integer> cost = new HashMap<>();


    public PieceSummary(Class<? extends Piece> type, int count, boolean buildable) {
        this.type = type;
        this.count = count;
        this.buildable = buildable;
    }

    @JsonIgnore
    public Class<? extends Piece> getRawType() {
        return type;
    }

    public String getType() {
        String typeName = "none";
        try {
            typeName = (String) type.getMethod(Piece.DISPLAY_NAME_METHOD_NAME).invoke(null);
        } catch (Exception e) {}
        return typeName;
    }

    public Map<String, Integer> getCost() {
        return cost;
    }

    public void setCost(Map<String, Integer> cost) {
        this.cost = cost;
    }

    public void setType(Class<? extends Piece> type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }
}
