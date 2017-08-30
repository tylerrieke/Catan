package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.lang.reflect.Method;

/**
 * Created by tcrie on 8/21/2017.
 */
public abstract class BuildTurn extends Turn {
    private SelectableBoardItem boardItem = null;
    private Board board;
    private boolean cancelled = false;
    private Class<? extends Piece> pieceType;
    private boolean isPlaceable = false;

    public BuildTurn(CatanPlayer player, Class<? extends Piece> pieceType, boolean canCancel, Board board) {
        super(player ,canCancel);
        this.board = board;
        this.pieceType = pieceType;
        isPlaceable = Piece.canBePlaced(pieceType);
    }

    @Override
    public void activate() {
        super.activate();
        boardItem = null;
        selection();
    }

    public boolean applyState() {
        if(cancelled) {
            onCancel();
            return true;
        }
        if(!isPlaceable) {
            onSuccess();
            return true;
        }
        if(boardItem != null) {
            if (!isConfirmPrompt()) {
                board.clearSelection();
                boardItem.setSelectable(true);
                setConfirmPrompt(true);
            } else if (getConfirmation() == true && getPlayer().playPiece(boardItem, pieceType)) {
                onSuccess();
                return true;
            } else if (getConfirmation() != null) {
                activate();
            }
        }
        return false;
    }

    public void cancel() {
        if(isCanCancel()) {
            cancelled = true;
        }
    }

    protected abstract void onCancel();
    protected abstract void onSuccess();

    private void selection() {
        board.setSelectableBoardItems(getPlayer(),pieceType);
    }

    public void setBoardItem(SelectableBoardItem boardItem) {
        this.boardItem = boardItem;
    }

    public SelectableBoardItem getBoardItem() {
        return boardItem;
    }
}
