package com.rieke.bmore.catan.base.board;

import com.rieke.bmore.catan.base.board.item.BoardItem;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.board.item.tile.Tile;

import java.util.List;
import java.util.Map;

public class Board {
    private Map<Integer, Edge> idToEdgeMap;
    private Map<Integer, ResourceTile> idToTileMap;
    private Map<Integer, Corner> idToCornerMap;
    private List<ResourceTile> tiles;

    public Board(Map<Integer, Edge> idToEdgeMap, Map<Integer, ResourceTile> idToTileMap, Map<Integer, Corner> idToCornerMap, List<ResourceTile> tiles) {
        this.idToEdgeMap = idToEdgeMap;
        this.idToTileMap = idToTileMap;
        this.idToCornerMap = idToCornerMap;
        this.tiles = tiles;
    }

    public Map<Integer, Edge> getIdToEdgeMap() {
        return idToEdgeMap;
    }

    public Map<Integer, ResourceTile> getIdToTileMap() {
        return idToTileMap;
    }

    public Map<Integer, Corner> getIdToCornerMap() {
        return idToCornerMap;
    }

    public List<ResourceTile> getTiles() {
        return tiles;
    }
}
