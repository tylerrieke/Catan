package com.rieke.bmore.catan.base.pieces;

import com.google.common.collect.ImmutableMap;
import com.rieke.bmore.catan.base.Player;
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

    public City(Player player) {
        super(player);
    }

    @Override
    public int getResourceMultiple() {
        return 2;
    }

    @Override
    public Map<Class<? extends Resource>, Integer> getCost() {
        return ImmutableMap.copyOf(cost);
    }
}
