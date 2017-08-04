package com.rieke.bmore.catan.base;

import com.rieke.bmore.catan.base.pieces.City;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.base.resources.Resource;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Player {
    public static final int DEFAULT_ROAD_COUNT = 16;
    public static final int DEFAULT_SETTLEMENT_COUNT = 5;
    public static final int DEFAULT_CITY_COUNT = 4;

    private Map<Class<? extends Resource>, AtomicInteger> resources = new ConcurrentHashMap<>();
    private Set<Road> roads = new HashSet<>();
    private Set<Settlement> settlements = new HashSet<>();
    private Set<City> cities = new HashSet<>();

    public Player(Set<Class<? extends Resource>> resourceSet) {
        for(Class<? extends Resource> resourceType:resourceSet) {
            resources.put(resourceType, new AtomicInteger());
        }

        initializeRoads(DEFAULT_ROAD_COUNT);
        initializeSettlements(DEFAULT_SETTLEMENT_COUNT);
        initializeCities(DEFAULT_CITY_COUNT);
    }

    private void initializeRoads(int count) {
        for(int i = 0; i < count; i++) {
            roads.add(new Road(this));
        }
    }

    private void initializeSettlements(int count) {
        for(int i = 0; i < count; i++) {
            settlements.add(new Settlement(this));
        }
    }

    private void initializeCities(int count) {
        for(int i = 0; i < count; i++) {
            cities.add(new City(this));
        }
    }

    public Map<Class<? extends Resource>, AtomicInteger> getResources() {
        return resources;
    }

    public void addResource(Class<? extends Resource> resource, int delta) {
        AtomicInteger count = resources.get(resource);
        if(count == null) {
            //TODO: throw exception
        } else {
            count.getAndAdd(delta);
        }
    }
}
