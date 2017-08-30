package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.resources.*;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.turn.NormalTurn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 8/29/2017.
 */
public abstract class DevelopmentCard extends Piece {
    private CatanPlayer player = null;
    private boolean played;
    private boolean playable;
    private Game game;

    private static Map cost;

    static {
        cost = new HashMap<>();
        cost.put(Ore.class,1);
        cost.put(Sheep.class,1);
        cost.put(Wheat.class,1);
    }

    public DevelopmentCard(boolean playable, Game game) {
        super(null);
        this.playable = playable;
        this.game = game;
    }

    public boolean isPlayable() {
        return playable && !isPlayed();
    }

    public boolean getPlayable() {
        return playable;
    }

    public void play(NormalTurn turn) {
        if(isPlayable() && getPlayer() != null) {
            action(turn);
        }
    }

    public void action(NormalTurn turn) {
        setPlayed(true);
        turn.setDevelopmentCard(this);
    }

    public CatanPlayer getPlayer() {
        return player;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayer(CatanPlayer player) {
        this.player = player;
    }

    protected void setPlayed(boolean played) {
        this.played = played;
    }

    public Game getGame() {
        return game;
    }

    public Map<Class<? extends Resource>,Integer> getCost() {
        return cost;
    }

    public static boolean isPlaceable() {
        return false;
    }
}
