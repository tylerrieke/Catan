package com.rieke.bmore.catan.base.board.item.edge;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.BoardItem;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.pieces.Road;

import java.util.Set;
import java.util.LinkedHashSet;

public class Edge extends SelectableBoardItem<Road> {
    private static final int MAX_NUM_TILES = 2;
    private static final int MAX_NUM_CORNERS = 2;

    private Set<Tile> tiles = new LinkedHashSet<>();
    private Set<Corner> corners = new LinkedHashSet<>();
    private Road road;

    public Edge(int id) {
        super(id);
        road = null;
    }

    public Edge addTile(Tile tile) {
        if(tiles.size() == MAX_NUM_TILES && !tiles.contains(tile)) {
            //TODO: Throw exception
        } else {
            tiles.add(tile);
        }
        return this;
    }

    public Edge addCorner(Corner corner) {
        if(corners.size() == MAX_NUM_CORNERS && !corners.contains(corner)) {
            //TODO: Throw exception
        } else {
            corners.add(corner);
        }
        return this;
    }

    public Road getRoad() {
        return getPiece();
    }

    @JsonIgnore
    public Set<Tile> getTiles() {
        return tiles;
    }

    public Set<Integer> getTileIds() {
        Set<Integer> ids = new LinkedHashSet<>();
        if(tiles!=null) {
            for(Tile tile:tiles) {
                ids.add(tile.getId());
            }
        }
        return ids;
    }

    @JsonIgnore
    public Set<Corner> getCorners() {
        return corners;
    }

    public Set<Integer> getCornerIds() {
        Set<Integer> ids = new LinkedHashSet<>();
        if(corners!=null) {
            for(Corner corner:corners) {
                ids.add(corner.getId());
            }
        }
        return ids;
    }

    @Override
    public Class<? extends Piece> getPieceType() {
        return Road.class;
    }
}
