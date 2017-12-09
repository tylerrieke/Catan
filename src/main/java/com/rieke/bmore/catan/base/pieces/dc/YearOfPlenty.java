package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.turn.CardSelectionTurn;
import com.rieke.bmore.catan.turn.NormalTurn;

/**
 * Created by tcrie on 8/31/2017.
 */
public class YearOfPlenty extends DevelopmentCard {
    public YearOfPlenty(Game game) {
        super(true, game);
    }

    @Override
    public void action(NormalTurn turn) {
        final NormalTurn fullTurn = turn;
        fullTurn.setChildTurn(new CardSelectionTurn(getPlayer(), fullTurn, 2) {
            @Override
            protected void actionPerResource(String name, Integer count) {
                Class<? extends Resource> resource =  getGame().getResourceService().getResourceByName(name);
                getPlayer().addResource(resource, count);
                getMessage().append(", collected "+count+" "+name);
            }
        });
        fullTurn.activate();
        super.action(turn);
    }

    public static String getDisplayName() {
        return "Year of Plenty";
    }
}
