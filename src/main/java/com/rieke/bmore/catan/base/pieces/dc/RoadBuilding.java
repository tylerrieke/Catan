package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.turn.BuildTurn;
import com.rieke.bmore.catan.turn.NormalTurn;
import com.rieke.bmore.catan.turn.Turn;

/**
 * Created by tcrie on 8/29/2017.
 */
public class RoadBuilding extends DevelopmentCard {
    public RoadBuilding(Game game) {
        super(true, game);
    }

    @Override
    public void action(final NormalTurn turn) {
        final NormalTurn fullTurn = turn;
        final NormalTurn.State previousState = fullTurn.getState();
        fullTurn.setChildTurn(new BuildTurn(getPlayer(), Road.class, false, getGame().getBoard()) {
            @Override
            protected void onCancel() {}

            @Override
            protected boolean onSuccess() {
                getBoardItem().setSelectable(false);
                if(getPlayer().getAvailablePiecesByType(Road.class).getCount() > 0) {
                    fullTurn.setChildTurn(new BuildTurn(getPlayer(), Road.class, false, getGame().getBoard()) {
                        @Override
                        protected void onCancel() {
                        }

                        @Override
                        protected boolean onSuccess() {
                            getBoardItem().setSelectable(false);
                            getGame().getSpecialMap().get(Game.LONGEST_ROAD).evaluateAll();
                            getGame().calculatePlayerPoints(getGame().getPlayers());
                            fullTurn.setState(previousState);
                            return true;
                        }
                    });
                    fullTurn.setState(NormalTurn.State.BUILDING);
                    fullTurn.activate();
                    return false;
                } else {
                    getBoardItem().setSelectable(false);
                    getGame().getSpecialMap().get(Game.LONGEST_ROAD).evaluateAll();
                    getGame().calculatePlayerPoints(getGame().getPlayers());
                    fullTurn.setState(previousState);
                    return true;
                }
            }
        });

        fullTurn.setState(NormalTurn.State.BUILDING);
        turn.activate();
        super.action(fullTurn);
    }

    @Override
    public boolean getPlayable(Turn turn) {
        boolean playable = super.getPlayable(turn);
        return playable && getPlayer().getAvailablePiecesByType(Road.class).getCount() > 0 ;
    }

    public static String getDisplayName() {
        return "Road Building";
    }
}
