package com.rieke.bmore.catan.base.board.item.tile;

import java.util.HashMap;
import java.util.Map;

public enum TileNumber {
    TWO(2),THREE(3),FOUR(4),FIVE(5),SIX(6),EIGHT(8),NINE(9),TEN(10),ELEVEN(11),TWELVE(12);

    private static final Map<Integer,TileNumber> valueMap = new HashMap<>();

    static {
        for(TileNumber entry:TileNumber.values()) {
            valueMap.put(entry.value,entry);
        }
    }

    private int value;
    TileNumber(int value) {
        this.value = value;
    }

    public static TileNumber getByValue(int value) {
        return valueMap.get(value);
    }

    public int getValue() {
        return value;
    }
}
