package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.board.LegendEntry;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.pieces.*;
import com.rieke.bmore.catan.base.pieces.dc.*;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.base.resources.ResourceSelection;
import com.rieke.bmore.catan.base.resources.ResourceService;
import com.rieke.bmore.catan.player.*;
import com.rieke.bmore.catan.turn.*;
import com.rieke.bmore.common.connection.ConnectionFactory;

import java.util.*;

public class Game {

    //Settings
    protected static boolean FRIEDNLY_ROBBER_DEFAULT = true;
    private boolean friendlyRobber;
    private int maxVictoryPoints;
    private boolean buildTurn;

    public static final String LONGEST_ROAD = "Longest Road";
    public static final String LARGEST_ARMY = "Largest Army";

    private ResourceService resourceService;
    private PieceService pieceService;

    private String id;
    private List<CatanPlayer> players;
    private CatanPlayerService playerService;
    private Board board;
    private State state;
    private List<SetupTurn> setupTurns;
    private List<EndBuildTurn> buildTurns;
    private int turnCount = 0;
    private Turn currentTurn;
    private Die dice1;
    private Die dice2;
    private int robberId = -1;
    private List<DevelopmentCard> developmentCards = new ArrayList<>();
    private List<LegendEntry> legendEntries = new ArrayList<>();

    private Map<String, Special> specialMap = new HashMap<>();

    private String[] colors = new String[]{"orange","blue","red","white","brown","green"};

    public Game(String id, Settings settings, ConnectionFactory connectionFactory, Board board, ResourceService resourceService, PieceService pieceService) {
        this.id = id;
        playerService = new CatanPlayerService(new CatanPlayerFactory(connectionFactory, settings.getMaxPlayers(), this),connectionFactory);
        state = State.OPEN;
        this.board = board;
        this.board.setGame(this);
        setupTurns = new ArrayList<>();
        buildTurns = new ArrayList<>();
        dice1 = new Die();
        dice2 = new Die();
        this.resourceService = resourceService;
        this.pieceService = pieceService;

        maxVictoryPoints = settings.getVictoryPoints();
        friendlyRobber = settings.isFriendly();
        buildTurn = settings.isBuildTurn();
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
        for(Map.Entry<String,Map<String,Integer>> entries:pieceService.getPieceCosts().entrySet()) {
            legendEntries.add(new LegendEntry(entries.getKey(),entries.getValue()));
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
        for(int i = 0; i < 2; i++) {
            developmentCards.add(new YearOfPlenty(this));
        }
        for(int i = 0; i < 2; i++) {
            developmentCards.add(new Monopoly(this));
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
        OPEN, SETUP, ACTIVE, OVER
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
                Turn childTurn = currentTurn.getChildTurn();
                if(childTurn instanceof TradeTurn) {
                    return ((TradeTurn) childTurn).waitingPlayerResponse(player);
                } else {
                    return ((NormalTurn) currentTurn).getRobberDiscardMap().keySet().contains(player);
                }
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

    public void setCurrentTurn(Turn currentTurn) {
        this.currentTurn = currentTurn;
    }

    public Turn getNextTurn() {
        if(!setupTurns.isEmpty()) {
            currentTurn = setupTurns.remove(0);
        } else if(!buildTurns.isEmpty()) {
            currentTurn = buildTurns.remove(0);
        } else {
            calculatePlayerPoints(players);
            state = State.ACTIVE;
            currentTurn = new NormalTurn(players.get(turnCount++%players.size()), this);
        }
        currentTurn.activate();
        return currentTurn;
    }

    public void createSettlement(CatanPlayer player, int cornerId) {
        synchronized (this) {
            Turn turn = getCurrentTurn();
            if (/*player.equals(turn.getPlayer()) &&*/ turn instanceof SetupTurn) {
                ((SetupTurn) turn).setCorner(getBoard().getIdToCornerMap().get(cornerId));
            } else if (turn instanceof NormalTurn || turn instanceof EndBuildTurn) {
                Turn childTurn = turn.getChildTurn();
                if (childTurn instanceof BuildTurn) {
                    ((BuildTurn) childTurn).setBoardItem(getBoard().getIdToCornerMap().get(cornerId));
                }
            }
            processState(turn);
        }
    }

    public void createRoad(CatanPlayer player, int edgeId) {
        Turn turn = getCurrentTurn();
        if(/*player.equals(turn.getPlayer()) &&*/ turn instanceof SetupTurn) {
            ((SetupTurn) turn).setEdge(getBoard().getIdToEdgeMap().get(edgeId));
        } else if(turn instanceof NormalTurn || turn instanceof EndBuildTurn) {
            Turn childTurn = turn.getChildTurn();
            if(childTurn instanceof BuildTurn) {
                ((BuildTurn) childTurn).setBoardItem(getBoard().getIdToEdgeMap().get(edgeId));
            }
        }
        processState(turn);
    }

    public void ok(CatanPlayer player, boolean ok) {
        synchronized (this) {
            Turn turn = getCurrentTurn();
            turn.setConfirmation(ok);
            processState(turn);
        }
    }

    private void processState(Turn turn) {
        if(!turn.nextState() && turn instanceof NormalTurn && buildTurn) {
            int turnCount = this.turnCount;
            CatanPlayer buildPlayer;
            buildTurns = new ArrayList<>();
            while((buildPlayer = players.get(turnCount++%players.size())) != turn.getPlayer()) {
                buildTurns.add(new EndBuildTurn(buildPlayer));
            }
            getNextTurn();
        } else if(!turn.nextState()) {
            if(turn instanceof NormalTurn && turn.getPlayer().getTotalVictoryPoints() >= maxVictoryPoints) {
                setState(State.OVER);
            } else {
                getNextTurn();
            }
        }
    }

    public int roll(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        int roll = dice1.roll() + dice2.roll();
        synchronized (this) {
            if (turn instanceof NormalTurn) {
                if(((NormalTurn) turn).getState().equals(NormalTurn.State.ROLL)) {
                    ((NormalTurn) turn).setRoll(roll);
                    payOutRoll(roll);
                }
                roll = ((NormalTurn) turn).getRoll();
            }
            processState(turn);
        }
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
        synchronized (this) {
            Turn turn = getCurrentTurn();
            if (turn.getPlayer().equals(player)) {
                turn.setDone(true);
            }
            processState(turn);
        }
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
        if(Road.getDisplayName().equals(type)) {
            pieceType = Road.class;
        } else if(Settlement.getDisplayName().equals(type)) {
            pieceType = Settlement.class;
        } else if(City.getDisplayName().equals(type)){
            pieceType = City.class;
        } else {
            if(!developmentCards.isEmpty()) {
                DevelopmentCard dc = developmentCards.remove(0);
                player.purchasePiece(dc);
                player.addPiece(DevelopmentCard.class, dc);
                dc.setPlayer(player);
                dc.setTurn(turn);
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
            protected boolean onSuccess() {
                getPlayer().purchasePiece(getBoardItem().getPiece());
                getBoardItem().setSelectable(false);
                if(turn instanceof NormalTurn || turn instanceof EndBuildTurn) {
                    if (turn instanceof NormalTurn) {
                        ((NormalTurn) turn).setBuilt(true);
                        ((NormalTurn) turn).setState(NormalTurn.State.BUILD);
                    }
                    specialMap.get(LONGEST_ROAD).evaluateAll();
                    calculatePlayerPoints(players);
                }
                return true;
            }
        });

        if(turn instanceof NormalTurn) {
            ((NormalTurn) turn).setState(NormalTurn.State.BUILDING);
        }

        turn.activate();
    }

    public void requestTrade(CatanPlayer player, CardExchange exchange) {
        Turn turn = getCurrentTurn();
        List<CatanPlayer> tradeRecipients = new ArrayList<>(getPlayers());
        tradeRecipients.remove(player);
        turn.setChildTurn(new TradeTurn(player,tradeRecipients,exchange,this));

        turn.activate();
    }

    public void answerTradeRequest(CatanPlayer player, boolean accept) {
        Turn turn = getCurrentTurn().getChildTurn();
        if(turn != null && turn instanceof TradeTurn) {
            if(((TradeTurn) turn).getPlayerResponses().containsKey(player)) {
                ((TradeTurn) turn).getPlayerResponses().put(player,accept);
            }
        }
    }

    private CatanPlayer getPlayerById(int id) {
        CatanPlayer player = null;
        for(CatanPlayer p:players) {
            if(p.getId() == id) {
                player = p;
                break;
            }
        }
        return player;
    }

    public void acceptPlayerTrade(CatanPlayer player, Integer acceptedId) {
        synchronized (this) {
            CatanPlayer accepted = (acceptedId != null ? getPlayerById(acceptedId) : null);
            Turn turn = getCurrentTurn().getChildTurn();
            if (turn != null && turn instanceof TradeTurn && turn.getPlayer().equals(player)) {
                if (accepted != null) {
                    ((TradeTurn) turn).setAcceptedPlayer(accepted);
                } else {
                    turn.cancel();
                }
                processState(getCurrentTurn());
            }
        }
    }

    public void robPlayer(CatanPlayer player, int robbedId) {
        synchronized (this) {
            CatanPlayer robbed = getPlayerById(robbedId);
            Turn turn = getCurrentTurn();
            if (robbed != null && turn.getPlayer().equals(player)) {
                if (turn instanceof NormalTurn) {
                    ((NormalTurn) turn).setRobbedPlayer(robbed);
                }
                processState(turn);
            }
        }
    }

    public void applyExchange(CatanPlayer player, CardExchange cardExchange) {
        synchronized (this) {
            Turn turn = getCurrentTurn();
            if (isValidExchange(player, cardExchange)) {
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
        synchronized (this) {
            Turn turn = getCurrentTurn();
            turn.cancel();
            processState(turn);
        }
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
        synchronized (this) {
            DevelopmentCard dc = player.getOnePlayableDcByType(dcType);
            Turn turn = getCurrentTurn();
            if (dc != null && turn instanceof NormalTurn) {
                dc.play((NormalTurn) turn);
                if (dc.getClass().equals(Knight.class)) {
                    specialMap.get(LARGEST_ARMY).evaluateAll();
                    calculatePlayerPoints(players);
                }
            }
        }
    }

    public ResourceService getResourceService() {
        return resourceService;
    }

    public PieceService getPieceService() {
        return pieceService;
    }

    public void playCardSelection(CatanPlayer player, Map<String, Integer> cardSelection) {
        synchronized (this) {
            Turn turn = getCurrentTurn();
            if (turn instanceof NormalTurn) {
                if (turn.getChildTurn() instanceof CardSelectionTurn) {
                    ((NormalTurn) turn).setCardSelection(cardSelection);
                }
            }
            processState(turn);
        }
    }

    public ResourceSelection getResourceSelection(CatanPlayer catanPlayer) {
        Turn turn = getCurrentTurn();
        Map<String, Integer> resourceMap = new HashMap<>();
        int count = 0;
        if(turn instanceof NormalTurn) {
            if(turn.getChildTurn() instanceof CardSelectionTurn) {
                count = ((NormalTurn) turn).getCardSelectionCount();
                for(Class<? extends Resource> resource:resourceService.getResources()) {
                    resourceMap.put(resource.getSimpleName(),0);
                }
            }
        }
        return new ResourceSelection(count, resourceMap);
    }

    public String getMessage() {
        Turn turn = getCurrentTurn();
        if(turn instanceof NormalTurn) {
            return ((NormalTurn) turn).getMessage().toString();
        }
        return "";
    }

    public CardExchange getTradeRequest(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn != null) {
            turn = turn.getChildTurn();
            if (turn != null && turn instanceof TradeTurn) {
                if(turn.getPlayer().equals(player) || ((TradeTurn) turn).waitingPlayerResponse(player)) {
                    return ((TradeTurn) turn).getTradeOffer();
                }
            }
        }
        return null;
    }

    public List<SimplePlayer> getTradeResponses(CatanPlayer player) {
        Turn turn = getCurrentTurn();
        if(turn != null) {
            turn = turn.getChildTurn();
            if (turn != null && turn instanceof TradeTurn && turn.getPlayer().equals(player)) {
                List<SimplePlayer> tradeResponses = new ArrayList<>();
                for (Map.Entry<CatanPlayer, Boolean> entry : ((TradeTurn) turn).getPlayerResponses().entrySet()) {
                    tradeResponses.add(new SimplePlayer(entry.getKey(), Boolean.TRUE.equals(entry.getValue()),entry.getValue()));
                }
                return tradeResponses;
            }
        }
        return null;
    }

    public boolean isFriendlyRobber() {
        return friendlyRobber;
    }

    public void setFriendlyRobber(boolean friendlyRobber) {
        if(state == State.OPEN) {
            this.friendlyRobber = friendlyRobber;
        }
    }

    public void makePlayerFirst(String name) {
        for(CatanPlayer player:playerService.getAllPlayers()) {
            if(player.getName().equals(name)) {
                player.setOrder(System.currentTimeMillis());
            }
        }
    }

    public List<LegendEntry> getPieceCosts() {
        return legendEntries;
    }
}
