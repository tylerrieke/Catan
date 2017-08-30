package com.rieke.bmore.catan.player;

import com.rieke.bmore.catan.base.pieces.Piece;

/**
 * Created by tcrie on 8/21/2017.
 */
public class PieceSummary {
    private Class<? extends Piece> type;
    private int count;
    private boolean buildable;

    public PieceSummary(Class<? extends Piece> type, int count, boolean buildable) {
        this.type = type;
        this.count = count;
        this.buildable = buildable;
    }

    public String getType() {
        return type.getSimpleName();
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
