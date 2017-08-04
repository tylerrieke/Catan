package com.rieke.bmore.catan.base.pieces;

import com.rieke.bmore.catan.base.Player;
import com.rieke.bmore.catan.base.resources.Resource;

import java.util.Map;

public abstract class Piece {
    private Player player;

    public Piece(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public abstract Map<Class<? extends Resource>,Integer> getCost();
}
