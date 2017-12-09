package com.rieke.bmore.catan.base.board;

import com.rieke.bmore.catan.base.board.item.tile.TileNumber;
import com.rieke.bmore.catan.base.resources.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tcrie on 12/9/2017.
 */
public class ExpansionBoardFactory extends BasicBoardFactory {

    public static final String NAME = "Expansion";

    private final TileNumber[] tileNumbers = new TileNumber[] {
            TileNumber.TWO, TileNumber.FIVE, TileNumber.FOUR,
            TileNumber.SIX, TileNumber.THREE, TileNumber.NINE,
            TileNumber.EIGHT, TileNumber.ELEVEN, TileNumber.ELEVEN,
            TileNumber.TEN, TileNumber.SIX, TileNumber.THREE,
            TileNumber.EIGHT, TileNumber.FOUR, TileNumber.EIGHT,
            TileNumber.TEN, TileNumber.ELEVEN, TileNumber.TWELVE,
            TileNumber.TEN, TileNumber.FIVE, TileNumber.FOUR,
            TileNumber.NINE, TileNumber.FIVE, TileNumber.NINE,
            TileNumber.TWELVE, TileNumber.THREE, TileNumber.TWO,
            TileNumber.SIX
    };

    private static final List<Class<? extends Resource>> addedHarbors;

    private final List<Class<? extends Resource>> harbors;

    private static final List<Class<? extends Resource>> addedResources = Arrays.asList(
            Wood.class, Wood.class,
            Sheep.class, Sheep.class,
            Wheat.class, Wheat.class,
            Ore.class, Ore.class,
            Brick.class, Brick.class,
            null
    );

    private final List<Class<? extends Resource>> resources;

    static {
        addedHarbors = new ArrayList<>();
        addedHarbors.add(Sheep.class);
        addedHarbors.add(null);
    }

    public ExpansionBoardFactory(ResourceService resourceService) {
        super(resourceService);
        resources = new ArrayList<>(super.getResources());
        resources.addAll(addedResources);
        harbors = new ArrayList<>(super.getHarbors());
        harbors.addAll(addedHarbors);

    }

    @Override
    protected TileNumber[] getTileNumbers() {
        return tileNumbers;
    }

    @Override
    protected List<Class<? extends Resource>> getHarbors() {
        return harbors;
    }

    @Override
    protected List<Class<? extends Resource>> getResources() {
        return resources;
    }

    @Override
    public int getMinPlayers() {
        return 5;
    }

    @Override
    public int getMaxPlayers() {
        return 6;
    }

    @Override
    public String getName() {
        return NAME;
    }
}
