package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.pieces.City;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.base.pieces.dc.DevelopmentCard;
import com.rieke.bmore.catan.base.pieces.dc.Knight;
import com.rieke.bmore.catan.base.pieces.dc.RoadBuilding;
import com.rieke.bmore.catan.base.pieces.dc.VictoryPoint;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.base.resources.ResourceService;
import com.rieke.bmore.catan.player.*;
import com.rieke.bmore.catan.turn.BuildTurn;
import com.rieke.bmore.catan.turn.NormalTurn;
import com.rieke.bmore.catan.turn.SetupTurn;
import com.rieke.bmore.catan.turn.Turn;
import com.rieke.bmore.common.connection.ConnectionFactory;

import java.util.*;

public class Game {
    public static final String LONGEST_ROAD = "Longest Road";
    public static final String LARGEST_ARMY = "Largest Army";

    private ResourceService resourceService;

    private String id;
    private List<CatanPlayer> players;
    private CatanPlayerService playerService;
    private Board board;
    private State state;
    private List<SetupTurn> setupTurns;
    private int turnCount = 0;
    private Turn currentTurn;
    private Die dice1;
    private Die dice2;
    private int robberId = -1;
    private List<DevelopmentCard> developmentCards = new ArrayList<>();

    private Map<String, Special> specialMap = new HashMap<>();

    private String[] colors = new String[]{"orange","blue","red","white","brown","green"};

    public Game(String id, ConnectionFactory connectionFactory, Board board, ResourceService resourceService) {
        this.id = id;
        playerService = new CatanPlayerService(new CatanPlayerFactory(connectionFactory, board.getMaxPlayers(), this),connectionFactory);
        state = State.OPEN;
        this.board = board;
        setupTurns = new ArrayList<>();
        dice1 = new Die();
        dice2 = new Die();
        this.resourceService = resourceService;
    }

    public void init() {
        specialMap.put(LONGEST_ROAD, new LongestRoad(this, LONGEST_ROAD, 2));
        specialMap.put(LARGEST_ARMY, new LargestArmy(this, LARGEST_ARMY, 2));

        players = new ArrayList<>(playerService.getAllPlayers());
        int colorIndex = 0;
        for(CatanPlayer player: players) {
            player.init(board.getResourceSet(), colors[colorIndex++]);
            initPlayerTradeIns(player);
            setupTurns.add(new SetupTurn(player,false,board));
        }
        for(int i = players.size()-1; i >= 0; i--) {
            setupTurns.add(new SetupTurn(players.get(i),true,board));
        }
        initDCs();
        shuffleDCs();
        state = State.SETUP;
        getNextTurn();
    }

    protected void initDCs() {
        for(int i = 0; i < 14; i++) {
            developmentCards.add(new Knight(this));
        }
        for(int i = 0; i < 5; i++) {
            developmentCards.add(new VictoryPoint(this));
        }
        for(int i = 0; i < 2; i++) {
            developmentCards.add(new RoadBuilding(this));
        }
    }

    protected void shuffleDCs() {
        Collections.shuffle(developmentCards, new Random(System.nanoTime()));
    }

    public String getId() {
        return id;
    }

    public List<CatanPlayer> getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public CatanPlayer getPlayer(String ip) {
        switch (state) {
            case OPEN:
                return playerService.getPlayer(ip);
            case SETUP:
            case ACTIVE:
            default:
                return playerService.getPlayerIfExists(ip);
        }
    }

    public void initPlayerTradeIns(CatanPlayer player) {
        Map<Class<? extends Resource>, Integer> tradeIns = new HashMap<>();
        for(Class<? extends Resource> resource : resourceService.getResources()) {
            tradeIns.put(resource,4);
        }
        player.setTradeIns(tradeIns);
    }

    public void updatePlayer(String ip, String name, String display) {
        if(playerService.getPlayerIfExists(ip) != null) {
            playerService.update(ip, name, display);
        }
    }

    public enum State {
        OPEN, SETUP, ACTIVE
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public CatanPlayer getActivePlayer() {
        return (currentTurn!=null?currentTurn.getPlayer():null);
    }

    public List<SimplePlayer> getSimplePlayers() {
        List<SimplePlayer> simplePlayers = new ArrayList<>();
        for (CatanPlayer player : playerService.getAllPlayers()) {
            simplePlayers.add(new SimplePlayer(player, isPlayerActive(player)));
        }
        return simplePlayers;
    }

    private boolean isPlayerActive(CatanPlayer player) {
        if(currentTurn != null) {
            if(currentTurn.getPlayer().equals(player)) {
                return true;
            } else if(currentTurn instanceof NormalTurn) {
                return ((NormalTurn) currentTurn).getRobberDiscardMap().keySet().contains(player);
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Turn getCurrentTurn() {
        return currentTurn;
    }

    public Turn getNextTurn() {
        if(!setupTurns.isEmpty()) {
            currentTurn = setupTurns.remove(0);
        } else {
            calculatePlayerPoints(players);
            state = State.ACTIVE;
            currentTurn = new NormalTurn(players.get(turnCount++%players.size()), this);
        }
        currentTurn.activate();
        return currentTurn;
    }

    public void createSettlement(CatanPlayer player, int cornerId) {
        Turn turn = getCurrentTurn();
        if(/*player.equals(turn.getPlayer()) &&*/ turn instanceof SetupTurn) {
            ((SetupTurn) turn).setCorner(getBoard().getIdToCornerMap().get(cornerId));
        } else if(turn instanceof NormalTurn) {
            Turn childTurn = turn.getChildTurn();
            if(childTurn instanceof BuildTurn) {
                ((BuildTurn) childTurn).setBoardItem(getBoard().getIdToCornerMap().get(cornerId));
            }
        }
        processState(turn);
    }

    public void createRoad(CatanPlayer player, int edgeId) {
        Turn turn = getCurrentTurn();
        if(/*player.equals(turn.getPlayer()) &&*/ turn instanceof SetupTurn) {
            ((SetupTurn) turn).setEdge(getBoard().getIdToEdgeMap().get(edgeId));
        } else if(turn instanceof NormalTurn) {
            Turn childTurn = turn.getChildTurn();
            if(childTurn instanceof BuildTurn) {
                ((BuildTurn) childTurn).setBoardItem(getBoard().getIdToEdgeMap().get(edgeId));
            }
        }
        processState(turn);
    }

    public void ok(CatanPlayer player, boolean ok) {
        Turn turn = getCurrentTurn();
        turn.setConfirmation(ok);
        processState(turn);
    }

    private void processState(Turn turn) {
        if(!turn.nextState()) {
            getNextTurn();
        }
    }

    public int roll(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        int roll = dice1.roll() + dice2.roll();
        if(turn instanceof NormalTurn) {
            ((NormalTurn) turn).setRoll(roll);
            payOutRoll(roll);
        }
        processState(turn);
        return roll;
    }

    public void playRobber(CatanPlayer player, int tileId) {
        Turn turn = getCurrentTurn();
        if(turn instanceof NormalTurn) {
            ((NormalTurn) turn).setRobberTile(getBoard().getIdToTileMap().get(tileId));
        }
        processState(turn);
    }

    public void setRobberId(int robberId) {
        this.robberId = robberId;
    }

    private void payOutRoll(int roll) {
        for(ResourceTile tile:board.getTiles()) {
            if(tile.getTileNumber() != null && tile.getTileNumber().getValue() == roll && tile.getId() != robberId) {
                for(Corner corner:tile.getCorners()) {
                    Settlement settlement = corner.getSettlement();
                    if(settlement != null) {
                        settlement.getPlayer().addResource(tile.getResource(),settlement.getResourceMultiple());
                    }
                }
            }
        }
    }

    public int getDice1() {
        return dice1.getValue();
    }

    public int getDice2() {
        return dice2.getValue();
    }

    public List<String> getTurnActions() {
        if(currentTurn!=null) {
            return currentTurn.getActions();
        }
        return new ArrayList<>();
    }

    public void endTurn(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn instanceof NormalTurn) {
            ((NormalTurn) turn).setDone(true);
        }
        processState(turn);
    }

    public int getRobberId() {
        return robberId;
    }

    public void calculatePlayerPoints(Collection<CatanPlayer> players) {
        for(CatanPlayer player:players) {
            player.calculateVictoryPoints(specialMap.values());
        }
    }

    public Integer getDiscardCount(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn instanceof NormalTurn) {
            return ((NormalTurn) turn).getRobberDiscardMap().get(player);
        } else {
            return 0;
        }
    }

    public List<SimplePlayer> getRobbable(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn instanceof NormalTurn) {
            return ((NormalTurn) turn).getRobbablePlayers();
        } else {
            return new ArrayList<>();
        }
    }

    public void startBuildSelection(final CatanPlayer player, String type) {
        final Turn turn = getCurrentTurn();
        Class<? extends Piece> pieceType;
        if(Road.class.getSimpleName().equals(type)) {
            pieceType = Road.class;
        } else if(Settlement.class.getSimpleName().equals(type)) {
            pieceType = Settlement.class;
        } else if(City.class.getSimpleName().equals(type)){
            pieceType = City.class;
        } else {
            if(!developmentCards.isEmpty()) {
                DevelopmentCard dc = developmentCards.remove(0);
                player.purchasePiece(dc);
                player.addPiece(DevelopmentCard.class, dc);
                dc.setPlayer(player);
                calculatePlayerPoints(players);
                if(turn instanceof NormalTurn) {
                    ((NormalTurn) turn).setBuilt(true);
                    ((NormalTurn) turn).setState(NormalTurn.State.BUILD);
                }
            }
            return;
        }

        turn.setChildTurn(new BuildTurn(player, pieceType, true, getBoard()) {
            @Override
            protected void onCancel() {
                board.clearSelection();
                if(turn instanceof NormalTurn) {
                    ((NormalTurn) turn).setState(NormalTurn.State.TRADE);
                }
            }

            @Override
            protected void onSuccess() {
                getPlayer().purchasePiece(getBoardItem().getPiece());
                getBoardItem().setSelectable(false);
                if(turn instanceof NormalTurn) {
                    ((NormalTurn) turn).setBuilt(true);
                    ((NormalTurn) turn).setState(NormalTurn.State.BUILD);
                    specialMap.get(LONGEST_ROAD).evaluateAll();
                    calculatePlayerPoints(players);
                }
            }
        });

        if(turn instanceof NormalTurn) {
            ((NormalTurn) turn).setState(NormalTurn.State.BUILDING);
        }

        turn.activate();
    }

    public void robPlayer(CatanPlayer player, int robbedId) {
        CatanPlayer robbed = null;
        for(CatanPlayer p:players) {
            if(p.getId() == robbedId) {
                robbed = p;
                break;
            }
        }
        if(robbed != null) {
            Turn turn = getCurrentTurn();
            if(turn instanceof NormalTurn) {
                ((NormalTurn) turn).setRobbedPlayer(robbed);
            }
            processState(turn);
        }
    }

    public void applyExchange(CatanPlayer player, CardExchange cardExchange) {
        Turn turn = getCurrentTurn();
        if(isValidExchange(player, cardExchange)) {
            for (Map.Entry<String, Integer> discard : cardExchange.getDiscard().entrySet()) {
                player.addResource(resourceService.getResourceByName(discard.getKey()), -discard.getValue());
            }
            for (Map.Entry<String, Integer> receive : cardExchange.getReceive().entrySet()) {
                player.addResource(resourceService.getResourceByName(receive.getKey()), receive.getValue());
            }
            if (turn instanceof NormalTurn) {
                ((NormalTurn) turn).getRobberDiscardMap().remove(player);
            }
            processState(turn);
        }
    }

    private boolean isValidExchange(CatanPlayer player, CardExchange cardExchange) {
        Map<String,Integer> playerTradins = player.getTradeable();
        int tradeable = 0;
        for(Map.Entry<String,Integer> discard:cardExchange.getDiscard().entrySet()){
            tradeable+=discard.getValue()/playerTradins.get(discard.getKey());
        }
        int tradedFor = 0;
        for(Map.Entry<String,Integer> receive:cardExchange.getReceive().entrySet()){
            tradedFor+=receive.getValue();
        }
        return tradeable >= tradedFor;
    }

    public boolean canCancel(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn != null) {
            return turn.isCanCancel();
        }
        return false;
    }

    public void cancelTurn(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        turn.cancel();
        processState(turn);
    }

    public Map<String, Special> getSpecialMap() {
        return specialMap;
    }

    public int getPieceCount(Class<? extends Piece> pieceType) {
        int count = 0;
        if(pieceType.equals(DevelopmentCard.class)) {
            count = developmentCards.size();
        }
        return count;
    }

    public boolean canBuild(Class<? extends Piece> pieceType, CatanPlayer player) {
        if(pieceType.equals(DevelopmentCard.class)) {
            if(!developmentCards.isEmpty()) {
                return player.canBuildPiece(developmentCards.get(0));
            }
        }
        return false;
    }

    public void playDC(CatanPlayer player, String dcType) {
        DevelopmentCard dc = player.getOnePlayableDcByType(dcType);
        Turn turn = getCurrentTurn();
        if(dc != null && turn instanceof NormalTurn) {
            dc.play((NormalTurn)turn);
            if(dc.getClass().equals(Knight.class)) {
                specialMap.get(LARGEST_ARMY).evaluateAll();
                calculatePlayerPoints(players);
            }
        }
    }
}
