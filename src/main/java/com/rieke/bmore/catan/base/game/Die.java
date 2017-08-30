package com.rieke.bmore.catan.base.game;

import java.util.Random;

public class Die {
    private int value;
    private static Random randomOffset = new Random(6);

    public Die() {
        roll();
    }

    public int getValue() {
        return value;
    }

    public int roll() {
        value = generateRandom();
        return value;
    }

    private int generateRandom() {
        long x = System.nanoTime();
        x ^= (x << 21);
        x ^= (x >>> 35);
        x ^= (x << 4);
        return Math.toIntExact((Math.abs(x)+randomOffset.nextInt()) % 6)+1;
    }
}
