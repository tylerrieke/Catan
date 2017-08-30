package com.rieke.bmore.catan.base.board.item;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.pieces.Piece;

/**
 * Created by tcrie on 8/13/2017.
 */
public abstract class SelectableBoardItem<PieceType extends Piece> extends BoardItem {

    private boolean selectable = false;
    private PieceType piece;

    public SelectableBoardItem(int id) {
        super(id);
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    @JsonIgnore
    public PieceType getPiece() {
        return piece;
    }

    public void setPiece(PieceType piece) {
        this.piece = piece;
    }

    @JsonIgnore
    public abstract Class<? extends Piece> getPieceType();
}
