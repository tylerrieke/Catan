package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.turn.NormalTurn;

import java.lang.invoke.MethodHandles;

/**
 * Created by tcrie on 8/29/2017.
 */
public class Knight extends DevelopmentCard {

    public Knight(Game game) {
        super(true, game);
    }

    @Override
    public void action(NormalTurn turn) {
        turn.goToRobber();
        super.action(turn);
    }

    public static String getDisplayName() {
        return MethodHandles.lookup().lookupClass().getSimpleName();
    }
}
