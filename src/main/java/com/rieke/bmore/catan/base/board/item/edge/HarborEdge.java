package com.rieke.bmore.catan.base.board.item.edge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.resources.Resource;
import java.util.HashMap;
import java.util.Map;

public class HarborEdge extends Edge {
    private Map<Class<? extends Resource>, Integer> resourceTradeIns;

    public HarborEdge(int id, Map<Class<? extends Resource>, Integer> resourceTradeIns) {
        super(id);
        this.resourceTradeIns = resourceTradeIns;
    }

    public HarborEdge(Edge edge, Map<Class<? extends Resource>, Integer> resourceTradeIns) {
        super(edge.getId());
        this.resourceTradeIns = resourceTradeIns;

        for(Tile tile : Lists.newArrayList(edge.getTiles())) {
            tile.removeEdge(edge);
            tile.addEdge(this);
        }

        for(Corner corner :Lists.newArrayList(edge.getCorners())) {
            corner.removeEdge(edge);
            corner.addEdge(this);
        }
    }

    @JsonIgnore
    public Map<Class<? extends Resource>, Integer> getResourceTradeIns() {
        return resourceTradeIns;
    }

    public Map<String,Integer> getResourceRatio() {
        Map<String, Integer> ratios = new HashMap<>();
        for(Map.Entry<Class<? extends Resource>, Integer> tradeIn:resourceTradeIns.entrySet()) {
            ratios.put(tradeIn.getKey().getSimpleName(),tradeIn.getValue());
        }
        return ratios;
    }
}
