package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;

/**
 * Created by tcrie on 8/29/2017.
 */
public class VictoryPoint extends DevelopmentCard {
    public VictoryPoint(Game game) {
        super(false, game);
    }

    @Override
    public int getVictoryPoints() {
        return 1;
    }

    public static String getDisplayName() {
        return "Victory Point";
    }
}
