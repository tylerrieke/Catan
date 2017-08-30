package com.rieke.bmore.catan.base.board.item.tile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.resources.Resource;
public class ResourceTile extends Tile {
    private Class<? extends Resource> resource;
    private TileNumber tileNumber;

    public ResourceTile(int id) {
        super(id);
        resource = null;
        tileNumber = null;
    }

    @JsonIgnore
    public Class<? extends Resource> getResource() {
        return resource;
    }

    public void setResource(Class<? extends Resource> resource) {
        this.resource = resource;
    }

    @JsonIgnore
    public TileNumber getTileNumber() {
        return tileNumber;
    }

    public String getResourceName() {
        return (resource != null ? resource.getSimpleName() : "");
    }

    public int getValue() {
        return (tileNumber != null ? tileNumber.getValue() : 0);
    }

    public void setTileNumber(TileNumber tileNumber) {
        this.tileNumber = tileNumber;
    }

    public void onRoll() {
        if(resource!=null && tileNumber!=null) {
            for(Corner corner:getCorners()) {
                if(corner.getSettlement() != null) {
                    corner.getSettlement().getPlayer().addResource(resource,corner.getSettlement().getResourceMultiple());
                }
            }
        }
    }

    @Override
    public Class<? extends Piece> getPieceType() {
        return null;
    }
}
