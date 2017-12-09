package com.rieke.bmore.catan.base.pieces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.resources.*;

import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

public class Settlement extends Piece<Corner> {
    private static final Map<Class<? extends Resource>, Integer> cost;

    static {
        cost = new HashMap<>();
        cost.put(Wood.class,1);
        cost.put(Brick.class,1);
        cost.put(Sheep.class,1);
        cost.put(Wheat.class,1);
    }

    private Corner corner;

    Settlement(){}

    public Settlement(CatanPlayer player) {
        super(player);
        corner = null;
    }

    @Override
    public int getVictoryPoints() {
        return 1;
    }

    public int getResourceMultiple() {
        return 1;
    }

    @Override
    @JsonIgnore
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }

    @Override
    public void setBoardItem(Corner boardItem) {
        super.setBoardItem(boardItem);
        afterSetBoardItem(boardItem);
    }

    protected void afterSetBoardItem(Corner boardItem) {
        if(boardItem!=null) {
            getPlayer().recalculateTradeIns(boardItem);
        }
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public static String getDisplayName() {
        return MethodHandles.lookup().lookupClass().getSimpleName();
    }

    @Override
    public int getLegendSortValue() {
        return 2;
    }
}
