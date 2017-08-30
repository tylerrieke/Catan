package com.rieke.bmore.catan.player;

import com.rieke.bmore.catan.base.pieces.dc.DevelopmentCard;

/**
 * Created by tcrie on 8/29/2017.
 */
public class DevelopmentCardSummary {
    private Class<? extends DevelopmentCard> type;
    private int count;
    private boolean playable;

    public DevelopmentCardSummary(Class<? extends DevelopmentCard> type, int count, boolean playable) {
        this.type = type;
        this.count = count;
        this.playable = playable;
    }

    public String getType() {
        return type.getSimpleName();
    }

    public int getCount() {
        return count;
    }

    public boolean isPlayable() {
        return playable;
    }

    public void incrementCount() {
        count++;
    }
}
