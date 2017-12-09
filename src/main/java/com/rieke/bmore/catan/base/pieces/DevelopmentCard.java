package com.rieke.bmore.catan.base.pieces;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.resources.*;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.turn.NormalTurn;
import com.rieke.bmore.catan.turn.Turn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 8/29/2017.
 */
public class DevelopmentCard extends Piece {
    private CatanPlayer player = null;
    private boolean played;
    private boolean playable;
    private Game game;
    private Turn turn = null;
    private StringBuilder message = new StringBuilder();

    private static Map cost;

    static {
        cost = new HashMap<>();
        cost.put(Ore.class,1);
        cost.put(Sheep.class,1);
        cost.put(Wheat.class,1);
    }

    DevelopmentCard(){}

    public DevelopmentCard(boolean playable, Game game) {
        super(null);
        this.playable = playable;
        this.game = game;
    }

    public boolean isPlayable() {
        return playable && !isPlayed();
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    public boolean getPlayable(Turn turn) {
        boolean hasPlayed = false;
        if(turn instanceof NormalTurn) {
            hasPlayed = ((NormalTurn) turn).getDevelopmentCard() != null;
        }
        return !turn.equals(this.turn) && !hasPlayed && playable;
    }

    public void play(NormalTurn turn) {
        if(isPlayable() && getPlayer() != null) {
            action(turn);
        }
    }

    public void action(NormalTurn turn) {
        setPlayed(true);
        //To help with GC
        setTurn(null);
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
        if(player != null) {
            message = new StringBuilder(player.getDisplay()+" played DC "+getClass().getSimpleName()+" ");
        }
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

    public StringBuilder getMessage() {
        return message;
    }

    public static String getDisplayName() {
        return "DC";
    }

    @Override
    public int getLegendSortValue() {
        return 4;
    }
}
