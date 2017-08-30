package com.rieke.bmore.catan.base.pieces.dc;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.turn.BuildTurn;
import com.rieke.bmore.catan.turn.NormalTurn;

/**
 * Created by tcrie on 8/29/2017.
 */
public class RoadBuilding extends DevelopmentCard {
    public RoadBuilding(Game game) {
        super(true, game);
    }

    @Override
    public void action(NormalTurn turn) {
        final NormalTurn fullTurn = turn;
        final NormalTurn.State previousState = fullTurn.getState();
        fullTurn.setChildTurn(new BuildTurn(getPlayer(), Road.class, false, getGame().getBoard()) {
            @Override
            protected void onCancel() {}

            @Override
            protected void onSuccess() {
                getBoardItem().setSelectable(false);
                fullTurn.setChildTurn(new BuildTurn(getPlayer(), Road.class, false, getGame().getBoard()) {
                    @Override
                    protected void onCancel() {}

                    @Override
                    protected void onSuccess() {
                        getBoardItem().setSelectable(false);
                        getGame().getSpecialMap().get(Game.LONGEST_ROAD).evaluateAll();
                        getGame().calculatePlayerPoints(getGame().getPlayers());
                        fullTurn.setState(previousState);
                    }
                });
                fullTurn.activate();
            }
        });

        fullTurn.setState(NormalTurn.State.BUILDING);
        turn.activate();
        super.action(fullTurn);
    }
}
