package com.rieke.bmore.catan.base.board.item.edge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.resources.Resource;
import java.util.HashMap;
import java.util.Map;

public class HarborEdge extends Edge {
    private Map<Class<? extends Resource>, Integer> resourceTradeIns;

    public HarborEdge(int id, Map<Class<? extends Resource>, Integer> resourceTradeIns) {
        super(id);
        this.resourceTradeIns = resourceTradeIns;
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
