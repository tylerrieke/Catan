package com.rieke.bmore.catan.base.board.item.corner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.BoardItem;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.pieces.Settlement;

import java.util.Set;
import java.util.LinkedHashSet;

public class Corner extends BoardItem {
    public static final int MAX_NUM_EDGES = 3;
    private Set<Edge> edges = new LinkedHashSet<>();
    private Set<Tile> tiles = new LinkedHashSet<>();
    private Settlement settlement;

    public Corner(int id) {
        super(id);
        settlement = null;
    }

    public Corner addEdge(Edge edge) {
        if(edges.size() == MAX_NUM_EDGES && !edges.contains(edge)) {
            //TODO: Throw Exception
        } else {
            edges.add(edge);
            edge.addCorner(this);
        }
        return this;
    }

    public Corner addTile(Tile tile) {
        if(tiles.size() == MAX_NUM_EDGES && !tiles.contains(tile)) {
            //TODO: Throw Exception
        } else {
            tiles.add(tile);
        }
        return this;
    }

    @JsonIgnore
    public Set<Edge> getEdges() {
        return edges;
    }

    public Set<Integer> getEdgeIds() {
        Set<Integer> ids = new LinkedHashSet<>();
        if(edges!=null) {
            for(Edge edge:edges) {
                ids.add(edge.getId());
            }
        }
        return ids;
    }

    public Settlement getSettlement() {
        return settlement;
    }

    public void setSettlement(Settlement settlement) {
        this.settlement = settlement;
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
}
