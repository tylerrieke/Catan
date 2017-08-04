package com.rieke.bmore.catan.base.board.item;

public abstract class BoardItem {
    private int id;

    public BoardItem(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
