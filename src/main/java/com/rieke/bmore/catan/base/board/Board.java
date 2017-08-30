package com.rieke.bmore.catan.base.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.pieces.City;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Board {
    private Map<Integer, Edge> idToEdgeMap;
    private Map<Integer, ResourceTile> idToTileMap;
    private Map<Integer, Corner> idToCornerMap;
    private List<ResourceTile> tiles;
    private int maxPlayers;
    private Set<Class<? extends Resource>> resourceSet;

    public Board(Map<Integer, Edge> idToEdgeMap, Map<Integer, ResourceTile> idToTileMap, Map<Integer, Corner> idToCornerMap, List<ResourceTile> tiles, int maxPlayers, Set<Class<? extends Resource>> resourceSet) {
        this.idToEdgeMap = idToEdgeMap;
        this.idToTileMap = idToTileMap;
        this.idToCornerMap = idToCornerMap;
        this.tiles = tiles;
        this.maxPlayers = maxPlayers;
        this.resourceSet = resourceSet;
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

    @JsonIgnore
    public int getMaxPlayers() {
        return maxPlayers;
    }

    @JsonIgnore
    public Set<Class<? extends Resource>> getResourceSet() {
        return resourceSet;
    }

    public void clearSelection() {
        for(Corner corner:idToCornerMap.values()) {
            corner.setSelectable(false);
        }
        for(Edge edge:idToEdgeMap.values()) {
            edge.setSelectable(false);
        }
        for(Tile tile:getTiles()) {
            tile.setSelectable(false);
        }
    }

    public void setSelectableBoardItems(CatanPlayer player, Class<? extends Piece> pieceType) {
        if(Settlement.class.equals(pieceType)) {
            setSelectableSettlementCorners(player);
        } else if(Road.class.equals(pieceType)) {
            setSelectableEdges(player);
        } else if(City.class.equals(pieceType)) {
            setSelectableCityCorners(player);
        }
    }

    public void setSelectableEdges(CatanPlayer player) {
        clearSelection();
        if(player!=null) {
            for(Road road:player.getPiecesByType(Road.class)) {
                if(road.getBoardItem() != null) {
                    for(Corner corner:road.getBoardItem().getCorners()) {
                        if(corner.getSettlement() == null || corner.getSettlement().getPlayer().equals(player)) {
                            for(Edge edge:corner.getEdges()) {
                                if(edge.getRoad() == null) {
                                    edge.setSelectable(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setSelectableSettlementCorners(CatanPlayer player) {
        clearSelection();
        if(player!=null) {
            for(Road road:player.getPiecesByType(Road.class)) {
                if(road.getBoardItem() != null) {
                    for(Corner corner:road.getBoardItem().getCorners()) {
                        setSelectableCorner(corner);
                    }
                }
            }
        } else {
            for(Corner corner:idToCornerMap.values()) {
                setSelectableCorner(corner);
            }
        }
    }

    public void setSelectableCityCorners(CatanPlayer player) {
        clearSelection();
        if(player!=null) {
            for(Settlement settlement:player.getPiecesByType(Settlement.class)) {
                if(settlement.getBoardItem() != null) {
                    settlement.getBoardItem().setSelectable(true);
                }
            }
        }
    }

    public void setSelectableRobberTiles(int currentRobberTileId) {
        clearSelection();
        for(ResourceTile tile : getTiles()) {
            if(tile.getId() != currentRobberTileId) {
                tile.setSelectable(true);
                for (Corner corner : tile.getCorners()) {
                    Settlement settlement = corner.getSettlement();
                    if (settlement != null && settlement.getPlayer().getVisibleVictoryPoints() < 3) {
                        tile.setSelectable(false);
                        break;
                    }
                }
            }
        }
    }

    private void setSelectableCorner(Corner corner) {
        if (corner.getSettlement() == null) {
            for (Edge edge : corner.getEdges()) {
                for (Corner neighbor : edge.getCorners()) {
                    if (neighbor.getSettlement() != null) {
                        return;
                    }
                }
            }
            corner.setSelectable(true);
        }
    }
}
