package com.rieke.bmore.catan.player;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.edge.HarborEdge;
import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.game.Special;
import com.rieke.bmore.catan.base.pieces.City;
import com.rieke.bmore.catan.base.pieces.Piece;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.base.pieces.Settlement;
import com.rieke.bmore.catan.base.pieces.DevelopmentCard;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.common.connection.Connection;
import com.rieke.bmore.common.player.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CatanPlayer extends Player {
    public static final int DEFAULT_ROAD_COUNT = 15;
    public static final int DEFAULT_SETTLEMENT_COUNT = 5;
    public static final int DEFAULT_CITY_COUNT = 4;

    private Map<Class<? extends Resource>, AtomicInteger> resources = new ConcurrentHashMap<>();
    private Map<Class<? extends Piece>,Set<?>> pieces = new HashMap<>();
    private String color = null;
    private int id;
    private static int id_counter = 0;
    private int visibleVictoryPoints;
    private int totalVictoryPoints;
    private int longestRoad = 0;
    private int largestArmy = 0;
    private Game game;
    private long order = 0;

    private Map<Class<? extends Resource>, Integer> tradeIns = new HashMap<>();

    private Random random = new Random();

    public CatanPlayer(Connection connection, String name, String display, Game game) {
        super(connection, name, display);
        this.game = game;
        id = id_counter++;
        order = System.currentTimeMillis();
    }

    public void init(Set<Class<? extends Resource>> resourceSet, String color) {
        for (Class<? extends Resource> resourceType : resourceSet) {
            resources.put(resourceType, new AtomicInteger());
        }
        this.color = color;
        initializePieces();
    }

    private void initializePieces() {
        Set<Road> roads = new HashSet<>();
        for(int i = 0; i < DEFAULT_ROAD_COUNT; i++) {
            roads.add(new Road(this));
        }
        pieces.put(Road.class,roads);

        Set<Settlement> settlements = new HashSet<>();
        for(int i = 0; i < DEFAULT_SETTLEMENT_COUNT; i++) {
            settlements.add(new Settlement(this));
        }
        pieces.put(Settlement.class,settlements);

        Set<City> cities = new HashSet<>();
        for(int i = 0; i < DEFAULT_CITY_COUNT; i++) {
            cities.add(new City(this));
        }
        pieces.put(City.class,cities);

        Set<DevelopmentCard> developmentCards = new HashSet<>();
        pieces.put(DevelopmentCard.class, developmentCards);
    }

    @JsonIgnore
    public Map<Class<? extends Resource>, AtomicInteger> getResources() {
        return resources;
    }

    public Map<String, Integer> getResourceCounts() {
        Map<String, Integer> resourceMap = new HashMap<>();
        for(Map.Entry<Class<? extends Resource>, AtomicInteger> entry:resources.entrySet()) {
            if(entry.getValue().intValue()>0) {
                resourceMap.put(entry.getKey().getSimpleName(), entry.getValue().intValue());
            }
        }
        return resourceMap;
    }

    public void addResource(Class<? extends Resource> resource, int delta) {
        AtomicInteger count = resources.get(resource);
        if(count == null) {
            //TODO: throw exception
        } else {
            count.getAndAdd(delta);
        }
    }

    public void calculateVictoryPoints(Collection<Special> specials) {
        int visibleCount = 0;
        int hiddenCount = 0;
        for(Class<? extends Piece> type:pieces.keySet()) {
            for(Piece piece:getPiecesByType(type)) {
                if(piece.getBoardItem() != null) {
                    visibleCount += piece.getVictoryPoints();
                } else if(!Piece.canBePlaced(type)) {
                    hiddenCount += piece.getVictoryPoints();
                }
            }
        }
        for(Special special:specials) {
            if(this.equals(special.getPlayer())) {
                visibleCount+=special.getPoints();
            }
        }
        visibleVictoryPoints = visibleCount;
        totalVictoryPoints = visibleCount + hiddenCount;
    }

    public int getVisibleVictoryPoints() {
        return visibleVictoryPoints;
    }

    public int getTotalVictoryPoints() {
        return totalVictoryPoints;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @JsonIgnore
    public <PieceType extends Piece> Set<PieceType> getPiecesByType(Class<PieceType> type) {
        return (Set<PieceType>)pieces.get(type);
    }

    public <BoardItem extends SelectableBoardItem> boolean playPiece(BoardItem boardItem, Class<? extends Piece> pieceType) {
        for(Piece piece:getPiecesByType(pieceType)) {
            if(piece.getBoardItem() == null) {
                if(boardItem.getPiece() != null) {
                    boardItem.getPiece().setBoardItem(null);
                }
                boardItem.setPiece(piece);
                piece.setBoardItem(boardItem);
                return true;
            }
        }
        return false;
    }

    public int getId() {
        return id;
    }

    public Class<? extends Resource> removeRandomResource() {
        int numCards = getCardCount();
        if(numCards == 0) {
            return null;
        }
        int cardNum = random.nextInt(numCards)+1;
        for(Map.Entry<Class<? extends Resource>, AtomicInteger> resource: resources.entrySet()) {
            if(resource.getValue().intValue()>=cardNum) {
                addResource(resource.getKey(),-1);
                return resource.getKey();
            } else {
                cardNum-=resource.getValue().intValue();
            }
        }
        return null;
    }

    public int getCardCount() {
        int numCards = 0;
        for(AtomicInteger count:resources.values()) {
            numCards+=count.intValue();
        }
        return numCards;
    }

    public int getDcCount() {
        int numDCs = 0;
        Set<DevelopmentCard> dcs = getPiecesByType(DevelopmentCard.class);
        if(dcs != null) {
            for (DevelopmentCard dc : dcs) {
                if (!dc.isPlayed()) {
                    numDCs++;
                }
            }
        }
        return numDCs;
    }

    public List<PieceSummary> getAvailablePieces() {
        List<PieceSummary> availablePiece = new ArrayList<>();
        for(Class<? extends Piece> type:pieces.keySet()) {
            availablePiece.add(getAvailablePiecesByType(type));
        }
        return availablePiece;
    }

    public PieceSummary getAvailablePiecesByType(Class<? extends Piece> type) {
        int count = 0;
        boolean buildable;
        if(Piece.canBePlaced(type)) {
            Piece testPiece = null;
            for(Piece piece:getPiecesByType(type)) {
                if(piece.getBoardItem() == null) {
                    count++;
                    testPiece = piece;
                }
            }
            buildable = canBuildPiece(testPiece);
        } else {
            count = game.getPieceCount(type);
            buildable = game.canBuild(type, this);
        }
        PieceSummary pieceSummary = new PieceSummary(type, count, buildable);
        pieceSummary.setCost(game.getPieceService().getPieceCosts().get(pieceSummary.getType()));
        return pieceSummary;
    }

    public boolean canBuildPiece(Piece piece){
        if(piece == null) {
            return false;
        }
        for(Class<? extends Resource> resource:(Set<Class<? extends Resource>>)piece.getCost().keySet()) {
            if(resources.get(resource).intValue() < (Integer)piece.getCost().get(resource)) {
                return false;
            }
        }
        return true;
    }

    public void purchasePiece(Piece piece) {
        for(Class<? extends Resource> resource:(Set<Class<? extends Resource>>)piece.getCost().keySet()) {
            resources.get(resource).getAndAdd(-(Integer)piece.getCost().get(resource));
        }
    }

    @JsonIgnore
    public Map<Class<? extends Resource>, Integer> getTradeIns() {
        return tradeIns;
    }

    public Map<String, Integer> getTradeable() {
        Map<String,Integer> tradeInsMap = new HashMap<>();
        for(Map.Entry<Class<? extends Resource>, Integer> entry : tradeIns.entrySet()) {
            tradeInsMap.put(entry.getKey().getSimpleName(),entry.getValue());
        }
        return tradeInsMap;
    }

    public void setTradeIns(Map<Class<? extends Resource>, Integer> tradeIns) {
        this.tradeIns = tradeIns;
    }

    public void recalculateTradeIns(Corner corner) {
        for(Edge edge:corner.getEdges()) {
            if(edge instanceof HarborEdge) {
                for(Map.Entry<Class<? extends Resource>, Integer> entry:((HarborEdge) edge).getResourceTradeIns().entrySet()) {
                    Integer currentRatio = tradeIns.get(entry.getKey());
                    if(currentRatio == null || entry.getValue() < currentRatio) {
                        tradeIns.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
    }

    public <T extends Piece> void addPiece(Class<T> type, T piece) {
        ((Set<T>)pieces.get(type)).add(piece);
    }

    public List<DevelopmentCardSummary> getAvailableDCs() {
        Map<String,DevelopmentCardSummary> dcSummaryMap = new HashMap<>();
        Set<DevelopmentCard> dcs = getPiecesByType(DevelopmentCard.class);
        if(dcs != null) {
            for (DevelopmentCard dc :dcs) {
                if (dc.isPlayable() || !dc.getPlayable(game.getCurrentTurn())) {
                    DevelopmentCardSummary summary = dcSummaryMap.get(dc.getSpecificDisplayName());
                    if (summary == null) {
                        summary = new DevelopmentCardSummary(dc.getClass(), 0, dc.getPlayable(game.getCurrentTurn()));
                        dcSummaryMap.put(dc.getSpecificDisplayName(), summary);
                    }
                    if(!summary.isPlayable()) {
                        summary.setPlayable(dc.getPlayable(game.getCurrentTurn()));
                    }
                    summary.incrementCount();
                }
            }
        }
        return new ArrayList<>(dcSummaryMap.values());
    }

    public DevelopmentCard getOnePlayableDcByType(String dcType) {
        for(DevelopmentCard dc:getPiecesByType(DevelopmentCard.class)) {
            if(dc.getSpecificDisplayName().equals(dcType) && dc.isPlayable()) {
                return dc;
            }
        }
        return null;
    }

    public int getLongestRoad() {
        return longestRoad;
    }

    public void setLongestRoad(int longestRoad) {
        this.longestRoad = longestRoad;
    }

    public int getLargestArmy() {
        return largestArmy;
    }

    public void setLargestArmy(int largestArmy) {
        this.largestArmy = largestArmy;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}
