package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.pieces.dc.DevelopmentCard;
import com.rieke.bmore.catan.base.pieces.dc.Knight;
import com.rieke.bmore.catan.player.CatanPlayer;

/**
 * Created by tcrie on 8/27/2017.
 */
public class LargestArmy extends Special {
    private static final int MINIMUM_SIZE = 3;

    public LargestArmy(Game game, String name, int points) {
        super(game, name, points, MINIMUM_SIZE);
    }

    @Override
    protected int evaluateOne(CatanPlayer player) {
        int count = 0;

        for(DevelopmentCard dc:player.getPiecesByType(DevelopmentCard.class)) {
            if(dc.getClass().equals(Knight.class) && dc.isPlayed()) {
                count++;
            }
        }

        return count;
    }
}
