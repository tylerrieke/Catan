package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.player.CatanPlayer;

/**
 * Created by tcrie on 9/5/2017.
 */
public abstract class CancellableTurn extends Turn {

    private boolean cancelled = false;

    public CancellableTurn(CatanPlayer player, boolean canCancel) {
        super(player, canCancel);
    }

    public boolean applyState() {
        if(cancelled) {
            onCancel();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void cancel() {
        if(isCanCancel()) {
            cancelled = true;
        }
    }

    protected abstract void onCancel();
    protected abstract boolean onSuccess();
}
