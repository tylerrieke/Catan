package com.rieke.bmore.catan.base.pieces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.base.resources.*;

import java.util.HashMap;
import java.util.Map;

public class City extends Settlement {
    private static final Map<Class<? extends Resource>, Integer> cost;

    static {
        cost = new HashMap<>();
        cost.put(Ore.class,3);
        cost.put(Wheat.class,2);
    }

    public City(CatanPlayer player) {
        super(player);
    }

    @Override
    public int getVictoryPoints() {
        return 2;
    }

    @Override
    public int getResourceMultiple() {
        return 2;
    }

    @Override
    @JsonIgnore
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }

    @Override
    protected void afterSetBoardItem(Corner boardItem) {

    }
}
