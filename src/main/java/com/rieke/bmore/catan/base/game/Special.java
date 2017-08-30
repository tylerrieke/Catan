package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.HashMap;
import java.util.Map;

public abstract class Special {
    private Game game;
    private CatanPlayer player;
    private final String name;
    private final int points;
    private int minimumQualifyScore = 0;

    public Special(Game game, String name, int points, int minimumQualifyScore) {
        this.game = game;
        this.name = name;
        this.points = points;
        this.minimumQualifyScore = minimumQualifyScore;
    }

    public CatanPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CatanPlayer player) {
        this.player = player;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    protected Game getGame() {
        return game;
    }

    public void evaluateAll() {
        int maxLength = 0;
        Map<CatanPlayer,Integer> qualified = new HashMap<>();
        for(CatanPlayer player:getGame().getPlayers()) {
            int length = evaluateOne(player);
            if(length >= minimumQualifyScore) {
                if(length > maxLength) {
                    maxLength = length;
                }
                qualified.put(player,length);
            }
        }
        if(qualified.size() > 0) {
            if(getPlayer() != null && qualified.get(getPlayer()) != null && qualified.get(getPlayer()) == maxLength) {
                return;
            } else {
                CatanPlayer maxPlayer = null;
                for(Map.Entry<CatanPlayer,Integer> entry:qualified.entrySet()) {
                    if(entry.getValue() == maxLength) {
                        if(maxPlayer == null) {
                            maxPlayer = entry.getKey();
                        } else {
                            setPlayer(null);
                            return;
                        }
                    }
                }
                if(maxPlayer != null) {
                    setPlayer(maxPlayer);
                }
            }
        }
    }

    protected abstract int evaluateOne(CatanPlayer player);
}
