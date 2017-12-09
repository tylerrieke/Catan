package com.rieke.bmore.catan.base.pieces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.base.resources.Resource;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public abstract class Piece<BoardItem extends SelectableBoardItem> {
    public static final String DISPLAY_NAME_METHOD_NAME = "getDisplayName";

    private CatanPlayer player;
    private BoardItem boardItem;

    protected Piece(){}

    public Piece(CatanPlayer player) {
        this.player = player;
    }

    @JsonIgnore
    public CatanPlayer getPlayer() {
        return player;
    }

    public String getPlayerColor() {
        return (player!=null?player.getColor():null);
    }

    @JsonIgnore
    public BoardItem getBoardItem() {
        return boardItem;
    }

    public void setBoardItem(BoardItem boardItem) {
        this.boardItem = boardItem;
    }

    public int getVictoryPoints() {
        return 0;
    }

    public abstract Map<Class<? extends Resource>,Integer> getCost();

    public static boolean isPlaceable() {
        return true;
    }

    public static boolean canBePlaced(Class<? extends Piece> pieceType) {
        boolean isPlaceable = false;
        try {
            Method method = pieceType.getMethod("isPlaceable");
            isPlaceable = (boolean) method.invoke(null);
        } catch (Exception e){}
        return isPlaceable;
    }

    public static String getDisplayName() {
        return MethodHandles.lookup().lookupClass().getSimpleName();
    }

    public String getSpecificDisplayName() {
        String typeName = "none";
        try {
            typeName = (String) this.getClass().getMethod(Piece.DISPLAY_NAME_METHOD_NAME).invoke(null);
        } catch (Exception e) {}
        return typeName;
    }

    public int getLegendSortValue() {
        return 0;
    }
}
