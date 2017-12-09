package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.player.CatanPlayer;
import com.rieke.bmore.catan.player.SimplePlayer;

import java.util.*;

/**
 * Created by tcrie on 8/18/2017.
 */
public class NormalTurn extends Turn{

    private State state;
    private Game game;
    private int roll = 0;
    private boolean built = false;
    private Map<CatanPlayer, Integer> robberDiscardMap;
    private Tile robberTile = null;
    private List<SimplePlayer> robbablePlayers;
    private CatanPlayer robbedPlayer = null;
    private SelectableBoardItem builder = null;
    private DevelopmentCard developmentCard = null;
    private int cardSelectionCount = 0;
    private Map<String, Integer> cardSelection;
    private StringBuilder message = new StringBuilder();

    public NormalTurn(CatanPlayer player, Game game) {
        super(player, false);
        this.state = State.ROLL;
        this.game = game;
        robberDiscardMap = new HashMap<>();
        robbablePlayers = new ArrayList<>();
    }

    @Override
    protected boolean applyState() {
        if(super.applyState()) {
            return true;
        }
        switch(state) {
            case ROLL:
                if(roll == 7) {
                    populateDiscardMap();
                    if(!robberDiscardMap.isEmpty()) {
                        state = State.ROBBER_DISCARD;
                    } else {
                        goToRobber();
                    }
                } else if(roll > 0) {
                    state = State.TRADE;
                }
                break;
            case ROBBER_DISCARD:
                if(robberDiscardMap.isEmpty()) {
                    goToRobber();
                }
                break;
            case ROBBER:
                if(robberTile != null) {
                    if (!isConfirmPrompt()) {
                        game.getBoard().clearSelection();
                        robberTile.setSelectable(true);
                        setConfirmPrompt(true);
                        break;
                    } else if (getConfirmation() == true) {
                        game.setRobberId(robberTile.getId());
                        populateRobbablePlayers();
                        if(robbablePlayers.isEmpty()) {
                            if(roll > 0) {
                                state = State.TRADE;
                            } else {
                                state = State.ROLL;
                            }
                        } else {
                            state = State.ROB_PLAYER;
                        }
                        activate();
                    } else if (getConfirmation() != null) {
                        goToRobber();
                        break;
                    }
                }
            case ROB_PLAYER:
                if(robbedPlayer != null) {
                    Class<? extends Resource> resource = robbedPlayer.removeRandomResource();
                    if(resource != null) {
                        getPlayer().addResource(resource,1);
                    }
                    if(roll > 0) {
                        state = State.TRADE;
                    } else {
                        state = State.ROLL;
                    }
                }
                break;
            case TRADE:
                if(built) {
                    state = State.BUILD;
                }
            case BUILD:

        }
        return false;
    }

    public void goToRobber() {
        robbedPlayer = null;
        robberTile = null;
        state = State.ROBBER;
        activate();
        game.getBoard().setSelectableRobberTiles(game.getRobberId());
    }

    public int getRoll() {
        return roll;
    }

    public void setRoll(int roll) {
        this.roll = roll;
    }

    public boolean isBuilt() {
        return built;
    }

    public void setBuilt(boolean built) {
        this.built = built;
    }

    public DevelopmentCard getDevelopmentCard() {
        return developmentCard;
    }

    public void setDevelopmentCard(DevelopmentCard developmentCard) {
        if(developmentCard != null) {
            this.message = developmentCard.getMessage();
        }
        this.developmentCard = developmentCard;
    }

    public void setRobbedPlayer(CatanPlayer robbedPlayer) {
        if(robbedPlayer != null) {
            this.message.append(getPlayer().getDisplay()+", robbed "+robbedPlayer.getDisplay());
        }
        this.robbedPlayer = robbedPlayer;
    }

    public Map<CatanPlayer, Integer> getRobberDiscardMap() {
        return robberDiscardMap;
    }

    public Tile getRobberTile() {
        return robberTile;
    }

    public void setRobberTile(Tile robberTile) {
        this.robberTile = robberTile;
    }

    public void populateDiscardMap() {
        for(CatanPlayer player:game.getPlayers()) {
            int cardCount = player.getCardCount();
            if(cardCount>7) {
                robberDiscardMap.put(player, cardCount/2);
            }
        }
    }

    public void populateRobbablePlayers() {
        robbablePlayers = new ArrayList<>();
        for(Corner corner:robberTile.getCorners()) {
            Settlement settlement = corner.getSettlement();
            if(settlement != null) {
                CatanPlayer player = settlement.getPlayer();
                SimplePlayer simplePlayer = new SimplePlayer(player,false);
                if(!player.equals(getPlayer()) && !robbablePlayers.contains(simplePlayer)) {
                    robbablePlayers.add(simplePlayer);
                }
            }
        }
    }

    public List<SimplePlayer> getRobbablePlayers() {
        return robbablePlayers;
    }

    @Override
    public List<String> getActions() {
        List<String> states;
        switch (state) {
            case ROLL:
                if(getDevelopmentCard() == null) {
                    states = Arrays.asList(State.DC.toString(), State.ROLL.toString());
                } else {
                    states = Arrays.asList(State.ROLL.toString());
                }
                break;
            case ROBBER:
                states = Arrays.asList(State.ROBBER.toString());
                break;
            case TRADE:
                if(getDevelopmentCard() == null) {
                    states = Arrays.asList(State.DC.toString(), State.TRADE.toString(), State.BUILD.toString(), State.END.toString());
                } else {
                    states = Arrays.asList(State.TRADE.toString(), State.BUILD.toString(), State.END.toString());
                }
                break;
            case BUILDING:
                states = Arrays.asList(State.BUILDING.toString());
                break;
            case BUILD:
                if(getDevelopmentCard() == null) {
                    states = Arrays.asList(State.DC.toString(), State.BUILD.toString(), State.END.toString());
                } else {
                    states = Arrays.asList(State.BUILD.toString(), State.END.toString());
                }
                break;
            case ROBBER_DISCARD:
                states = Arrays.asList(State.ROBBER_DISCARD.toString());
                break;
            case ROB_PLAYER:
                states = Arrays.asList(State.ROB_PLAYER.toString());
                break;
            case SELECT_CARDS:
                states = Arrays.asList(State.SELECT_CARDS.toString());
                break;
            default:
                states = Arrays.asList();
        }
        return states;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getCardSelectionCount() {
        return cardSelectionCount;
    }

    public void setCardSelectionCount(int cardSelectionCount) {
        this.cardSelectionCount = cardSelectionCount;
    }

    public Map<String, Integer> getCardSelection() {
        return cardSelection;
    }

    public void setCardSelection(Map<String, Integer> cardSelection) {
        this.cardSelection = cardSelection;
    }

    public CatanPlayer getRobbedPlayer() {
        return robbedPlayer;
    }

    public String getMessage() {
        return message.toString();
    }
}
