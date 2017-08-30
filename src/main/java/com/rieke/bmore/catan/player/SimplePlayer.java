package com.rieke.bmore.catan.player;

/**
 * Created by tcrie on 8/9/2017.
 */
public class SimplePlayer {
    private int id;
    private String name;
    private String display;
    private int numCards;
    private int dcCount;
    private String color;
    private boolean active;
    private int victoryPoints;

    public SimplePlayer(CatanPlayer player, boolean active) {
        id = player.getId();
        name = player.getName();
        display = player.getDisplay();
        color = player.getColor();
        numCards = player.getCardCount();
        dcCount = player.getDcCount();
        victoryPoints = player.getVisibleVictoryPoints();
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }

    public boolean isActive() {
        return active;
    }

    public int getId() {
        return id;
    }

    public int getNumCards() {
        return numCards;
    }

    public int getDcCount() {
        return dcCount;
    }

    public String getColor() {
        return color;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePlayer that = (SimplePlayer) o;

        if (id != that.id) return false;
        if (numCards != that.numCards) return false;
        if (active != that.active) return false;
        if (!name.equals(that.name)) return false;
        if (!display.equals(that.display)) return false;
        return color != null ? color.equals(that.color) : that.color == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + display.hashCode();
        result = 31 * result + numCards;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (active ? 1 : 0);
        return result;
    }
}