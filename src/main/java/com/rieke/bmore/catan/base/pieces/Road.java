package com.rieke.bmore.catan.base.pieces;

import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.base.Player;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.resources.Brick;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.base.resources.Wood;

import java.util.HashMap;
import java.util.Map;

public class Road extends Piece {
    private static final Map<Class<? extends Resource>, Integer> cost;

    static {
        cost = new HashMap<>();
        cost.put(Wood.class,1);
        cost.put(Brick.class,1);
    }

    private Edge edge;

    public Road(Player player) {
        super(player);
        edge = null;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    public Edge getEdge() {
        return edge;
    }

    @Override
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }
}
