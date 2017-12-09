package com.rieke.bmore.catan.base.game;

/**
 * Created by tcrie on 11/26/2017.
 */
public class Settings {
    private int maxPlayers = 4;
    private boolean friendly = true;
    private int numberOfTiles = 19;
    private int victoryPoints = 10;
    private String boardType = null;
    private boolean buildTurn = false;

    public Settings() {
    }

    public Settings(int maxPlayers, boolean friendly, int numberOfTiles, int victoryPoints, String boardType, boolean buildTurn) {
        this.maxPlayers = maxPlayers;
        this.friendly = friendly;
        this.numberOfTiles = numberOfTiles;
        this.victoryPoints = victoryPoints;
        this.boardType = boardType;
        this.buildTurn = buildTurn;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }

    public int getNumberOfTiles() {
        return numberOfTiles;
    }

    public void setNumberOfTiles(int numberOfTiles) {
        this.numberOfTiles = numberOfTiles;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    public String getBoardType() {
        return boardType;
    }

    public void setBoardType(String boardType) {
        this.boardType = boardType;
    }

    public boolean isBuildTurn() {
        return buildTurn;
    }

    public void setBuildTurn(boolean buildTurn) {
        this.buildTurn = buildTurn;
    }
}
