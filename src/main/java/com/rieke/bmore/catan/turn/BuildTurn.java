package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.lang.reflect.Method;

/**
 * Created by tcrie on 8/21/2017.
 */
public abstract class BuildTurn extends CancellableTurn {
    private SelectableBoardItem boardItem = null;
    private Board board;
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

    @Override
    public boolean applyState() {
        if(super.applyState()) {
            return true;
        }
        if(!isPlaceable) {
            return onSuccess();
        }
        if(boardItem != null) {
            if (!isConfirmPrompt()) {
                board.clearSelection();
                boardItem.setSelectable(true);
                setConfirmPrompt(true);
            } else if (Boolean.TRUE.equals(getConfirmation()) && getPlayer().playPiece(boardItem, pieceType)) {
                return onSuccess();
            } else if (getConfirmation() != null) {
                activate();
            }
        }
        return false;
    }

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
