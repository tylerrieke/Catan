package com.rieke.bmore.catan.base.pieces;

import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.base.Player;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.resources.*;

import java.util.HashMap;
import java.util.Map;

public class Settlement extends Piece {
    private static final Map<Class<? extends Resource>, Integer> cost;

    static {
        cost = new HashMap<>();
        cost.put(Wood.class,1);
        cost.put(Brick.class,1);
        cost.put(Sheep.class,1);
        cost.put(Wheat.class,1);
    }

    private Corner corner;

    public Settlement(Player player) {
        super(player);
        corner = null;
    }

    public Corner getCorner() {
        return corner;
    }

    public void setCorner(Corner corner) {
        this.corner = corner;
    }

    public int getResourceMultiple() {
        return 1;
    }

    @Override
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }
}
