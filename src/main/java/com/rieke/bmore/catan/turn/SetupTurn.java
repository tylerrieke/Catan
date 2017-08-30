package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.player.CatanPlayer;

/**
 * Created by tcrie on 8/13/2017.
 */
public class SetupTurn extends Turn {

    public enum State {
        PICK_CORNER, PICK_EDGE
    }

    private Corner corner;
    private Edge edge;
    private boolean collectResources;
    private State state;
    private Board board;

    public SetupTurn(CatanPlayer player, boolean collectResources, Board board) {
        super(player, false);
        corner = null;
        edge = null;
        this.board = board;
        this.collectResources = collectResources;
    }

    @Override
    public void activate() {
        super.activate();
        state = State.PICK_CORNER;
        board.setSelectableSettlementCorners(null);
    }

    @Override
    protected boolean applyState() {
        switch (state) {
            case PICK_CORNER:
                if(corner != null) {
                    if(!isConfirmPrompt()) {
                        board.clearSelection();
                        corner.setSelectable(true);
                        setConfirmPrompt(true);
                        break;
                    } else if(getConfirmation() == true && getPlayer().playPiece(corner, Settlement.class)){
                        setUpEdgeChoice();
                    } else if(getConfirmation() != null) {
                        corner = null;
                        activate();
                        break;
                    }
                }
                break;
            case PICK_EDGE:
                if(edge != null) {
                    if(!isConfirmPrompt()) {
                        board.clearSelection();
                        edge.setSelectable(true);
                        setConfirmPrompt(true);
                        break;
                    } else if(getConfirmation() == true && getPlayer().playPiece(edge, Road.class)){
                        return true;
                    } else if(getConfirmation() != null) {
                        setUpEdgeChoice();
                    }
                }
        }
        return false;
    }

    private void setUpEdgeChoice() {
        state = State.PICK_EDGE;
        setConfirmation(null);
        setConfirmPrompt(false);
        board.clearSelection();
        for(Edge edge:corner.getEdges()) {
            if(edge.getRoad() == null) {
                edge.setSelectable(true);
            }
        }
    }

    public Corner getCorner() {
        return corner;
    }

    public void setCorner(Corner corner) {
        this.corner = corner;
    }

    public Edge getEdge() {
        return edge;
    }

    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    @Override
    public void endTurn() {
        board.clearSelection();
        if(collectResources) {
            for(Tile tile:corner.getTiles()) {
                if(tile instanceof ResourceTile && ((ResourceTile) tile).getResource()!=null) {
                    getPlayer().addResource(((ResourceTile) tile).getResource(),1);
                }
            }
        }
    }
}
