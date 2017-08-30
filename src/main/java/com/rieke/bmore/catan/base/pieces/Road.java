package com.rieke.bmore.catan.base.pieces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.resources.Brick;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.base.resources.Wood;

import java.util.HashMap;
import java.util.Map;

public class Road extends Piece<Edge> {
    private static final Map<Class<? extends Resource>, Integer> cost;

    static {
        cost = new HashMap<>();
        cost.put(Wood.class,1);
        cost.put(Brick.class,1);
    }

    public Road(CatanPlayer player) {
        super(player);
    }

    @Override
    @JsonIgnore
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }
}
