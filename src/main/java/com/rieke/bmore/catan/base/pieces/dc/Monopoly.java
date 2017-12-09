package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.turn.CardSelectionTurn;
import com.rieke.bmore.catan.turn.NormalTurn;

import java.lang.invoke.MethodHandles;

/**
 * Created by tcrie on 8/31/2017.
 */
public class Monopoly extends DevelopmentCard {
    public Monopoly(Game game) {
        super(true, game);
    }

    @Override
    public void action(NormalTurn turn) {
        final NormalTurn fullTurn = turn;
        fullTurn.setChildTurn(new CardSelectionTurn(getPlayer(), fullTurn, 1) {
            protected void actionPerResource(String resourceName, Integer count) {
                Class<? extends Resource> resource =  getGame().getResourceService().getResourceByName(resourceName);
                for(CatanPlayer player:getGame().getPlayers()) {
                    if(!player.equals(getPlayer())) {
                        int playerCount = player.getResources().get(resource).intValue();
                        player.addResource(resource, -playerCount);
                        getPlayer().addResource(resource, playerCount);
                    }
                }
                getMessage().append(", stole all "+resourceName);
            }
        });
        fullTurn.activate();
        super.action(turn);
    }

    public static String getDisplayName() {
        return MethodHandles.lookup().lookupClass().getSimpleName();
    }
}
