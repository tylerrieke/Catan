package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.Map;

/**
 * Created by tcrie on 9/1/2017.
 */
public abstract class CardSelectionTurn extends Turn {

    private NormalTurn fullTurn;
    private int numCards;
    private NormalTurn.State previousState;

    public CardSelectionTurn(CatanPlayer player, NormalTurn turn, Integer numCards) {
        super(player, false);
        this.fullTurn = turn;
        this.numCards = numCards;
    }

    @Override
    public void activate() {
        super.activate();
        previousState = fullTurn.getState();
        fullTurn.setState(NormalTurn.State.SELECT_CARDS);
        fullTurn.setCardSelectionCount(numCards);

    }

    @Override
    protected boolean applyState() {
        boolean success = false;
        for(Map.Entry<String, Integer> entry:fullTurn.getCardSelection().entrySet()) {
            if(entry.getValue() > 0) {
                actionPerResource(entry.getKey(), entry.getValue());
                success = true;
            }
        }
        if(success) {
            fullTurn.setState(previousState);
        }
        return success;
    }

    protected abstract void actionPerResource(String resourceName, Integer count);
}
