package com.rieke.bmore.catan.turn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tcrie on 8/13/2017.
 */
public abstract class Turn {
    private CatanPlayer player;
    private boolean confirmPrompt = false;
    private Boolean confirmation = null;
    private Turn childTurn = null;
    private boolean canCancel = false;

    public Turn(CatanPlayer player, boolean canCancel) {
        this.player = player;
        this.canCancel = canCancel;
    }

    public void endTurn() {

    }

    @JsonIgnore
    public CatanPlayer getPlayer() {
        return player;
    }

    public void setPlayer(CatanPlayer player) {
        this.player = player;
    }

    public int getPlayerId() {
        return player.getId();
    }

    public boolean nextState() {
        if(childTurn != null) {
            if(childTurn.nextState()) {
                return true;
            } else {
                childTurn = null;
            }
        }
        if(!applyState()) {
            return true;
        } else {
            endTurn();
            return false;
        }
    }

    public boolean isConfirmPrompt() {
        if(childTurn != null) {
            return childTurn.isConfirmPrompt();
        }
        return confirmPrompt;
    }

    public Boolean getConfirmation() {
        if(childTurn != null) {
            return childTurn.getConfirmation();
        }
        return confirmation;
    }

    public void setConfirmPrompt(boolean confirmPrompt) {
        if(childTurn != null) {
            childTurn.setConfirmPrompt(confirmPrompt);
        } else {
            this.confirmPrompt = confirmPrompt;
        }
    }

    public void setConfirmation(Boolean confirmation) {
        if(childTurn != null ) {
            childTurn.setConfirmation(confirmation);
        } else {
            this.confirmation = confirmation;
        }
    }

    protected abstract boolean applyState();

    public void activate() {
        if(childTurn != null) {
            childTurn.activate();
        } else {
            setConfirmPrompt(false);
            setConfirmation(null);
        }
    }

    public List<String> getActions() {
        if(childTurn != null) {
            return childTurn.getActions();
        }
        return new ArrayList<>();
    }

    public void setChildTurn(Turn childTurn) {
        this.childTurn = childTurn;
    }

    public void cancel() {
        if(childTurn != null) {
            childTurn.cancel();
        }
    }

    public boolean isCanCancel() {
        if(childTurn != null) {
            return childTurn.isCanCancel();
        }
        return canCancel;
    }

    public Turn getChildTurn() {
        return childTurn;
    }
}
