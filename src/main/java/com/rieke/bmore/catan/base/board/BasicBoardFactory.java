package com.rieke.bmore.catan.base.board;

import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.board.item.edge.HarborEdge;
import com.rieke.bmore.catan.base.board.item.tile.ResourceTile;
import com.rieke.bmore.catan.base.board.item.tile.Tile;
import com.rieke.bmore.catan.base.board.item.tile.TileNumber;
import com.rieke.bmore.catan.base.game.Settings;
import com.rieke.bmore.catan.base.resources.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.lang.Math.sqrt;

@Service
public class BasicBoardFactory {

    public static final String NAME = "Basic";

    private enum CornerOrder {
        N(new Integer[]{3,2,1,4,5,6}),S(new Integer[]{6,5,1,2,3,4}),E(new Integer[]{5,4,3,6,1,2}),W(new Integer[]{2,1,6,3,4,5}),
        C_NE(new Integer[]{4,3,5,6,1,2}),C_SE(new Integer[]{5,4,6,1,2,3}),C_SW(new Integer[]{1,6,2,3,4,5}),C_NW(new Integer[]{2,1,3,4,5,6}),
        NE_A(new Integer[]{3,2,4,5,6,1}),NE_B(new Integer[]{4,3,2,5,6,1}),SE(new Integer[]{6,5,4,1,2,3}),SW(new Integer[]{1,6,5,2,3,4}),NW(new Integer[]{3,2,1,4,5,6});
        private Integer[] order;

        CornerOrder(Integer[] order) {
            this.order = order;
        }

        public Integer[] getOrder() {
            return order;
        }
    }

    private final TileNumber[] tileNumbers = new TileNumber[] {
            TileNumber.FIVE, TileNumber.TWO, TileNumber.SIX,
            TileNumber.THREE, TileNumber.EIGHT, TileNumber.TEN,
            TileNumber.NINE, TileNumber.TWELVE, TileNumber.ELEVEN,
            TileNumber.FOUR, TileNumber.EIGHT, TileNumber.TEN,
            TileNumber.NINE, TileNumber.FOUR, TileNumber.FIVE,
            TileNumber.SIX, TileNumber.THREE, TileNumber.ELEVEN
    };

    private final List<Class<? extends Resource>> harbors =  Arrays.asList(
            Wood.class, Sheep.class, Wheat.class, Ore.class, Brick.class,
            null, null, null, null
    );

    private List<Class<? extends Resource>> shuffledHarbors;

    private final List<Class<? extends Resource>> resources = Arrays.asList(
            Wood.class, Wood.class, Wood.class, Wood.class,
            Sheep.class, Sheep.class, Sheep.class, Sheep.class,
            Wheat.class, Wheat.class, Wheat.class, Wheat.class,
            Ore.class, Ore.class, Ore.class,
            Brick.class, Brick.class, Brick.class,
            null
    );

    private ResourceService resourceService;

    public BasicBoardFactory(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public synchronized Board create(Settings settings) {
        shuffledHarbors = new ArrayList<>();
        shuffledHarbors.addAll(getHarbors());
        Collections.shuffle(shuffledHarbors, new Random(System.nanoTime()));

        Map<Integer, Edge> idToEdgeMap = new HashMap<>();
        Map<Integer, ResourceTile> idToTileMap = new HashMap<>();
        Map<Integer, Corner> idToCornerMap = new HashMap<>();
        List<ResourceTile> tiles = new ArrayList<>();

        List<Class<? extends Resource>> resourcesCopy = new ArrayList<>();
        resourcesCopy.addAll(getResources());
        Collections.shuffle(resourcesCopy, new Random(System.nanoTime()));

        int numberIndex = getTileNumbers().length-1;
        for(int i = 1; i <= settings.getNumberOfTiles(); i++) {
            if(numberIndex < 0) {numberIndex = getTileNumbers().length-1;}
            if(i > resourcesCopy.size()) {
                List<Class<? extends Resource>> tmpCopy = new ArrayList<>();
                tmpCopy.addAll(getResources());
                Collections.shuffle(tmpCopy, new Random(System.nanoTime()));
                resourcesCopy.addAll(tmpCopy);
            }
            ResourceTile resourceTile = new ResourceTile(i);
            resourceTile.setResource(resourcesCopy.get(i-1));
            if(resourceTile.getResource() != null) {
                resourceTile.setTileNumber(getTileNumbers()[numberIndex--]);
            }
            idToTileMap.put(i, resourceTile);
            tiles.add(resourceTile);
        }

        int maxLayer = prepareLayers(tiles);
        List<int[]> allCorners = new ArrayList<>();
        for(int i = 1; i<=maxLayer;i++) {
            allCorners.addAll(getLayerOfTilesForHosts(i));
        }
        Corner previousCorner = null;
        Edge previousDownEdge = null;
        int downEdgeDiff = 7;
        for(int i = 1; i<=allCorners.size();i++) {
            int[] host = allCorners.get(i-1);
            Corner corner = new Corner(i);
            List<ResourceTile> sharedTiles = new ArrayList<>();
            for(int tileId:host) {
                ResourceTile tile = idToTileMap.get(tileId);
                if(tile!=null) {
                    tile.addCorner(corner);
                    if(previousCorner!=null && previousCorner.getTiles().contains(tile)) {
                        sharedTiles.add(tile);
                    }
                }
            }
            if(!corner.getTiles().isEmpty()) {
                idToCornerMap.put(corner.getId(),corner);
                if((host[0]!=1 && host[1]!=host[2]-1) || corner.getId()==6) {
                    ResourceTile tile1,tile2;
                    if(previousDownEdge!=null && (corner.getId()+previousDownEdge.getId()!=2)) {
                        downEdgeDiff+=2;
                    }
                    tile1 = idToTileMap.get(host[0]);
                    if(previousDownEdge==null || host[1]-host[0]==1) {
                        tile2 = idToTileMap.get(host[1]);
                    } else {
                        tile2 = idToTileMap.get(host[2]);
                    }

                    int downCornerId = (corner.getId()!=6?corner.getId()-downEdgeDiff:1);
                    Edge edge = createEdge(0-corner.getId());
                    Corner downCorner = idToCornerMap.get(downCornerId);

                    if(tile1!=null) {
                        tile1.addEdge(edge);
                    }
                    if(tile2!=null) {
                        tile2.addEdge(edge);
                    }
                    corner.addEdge(edge);
                    downCorner.addEdge(edge);
                    idToEdgeMap.put(edge.getId(),edge);
                    previousDownEdge = edge;
                }
            }
            if(!sharedTiles.isEmpty()) {
                Edge edge = createEdge(corner.getId());
                for(ResourceTile tile:sharedTiles) {
                    tile.addEdge(edge);
                }
                idToEdgeMap.put(edge.getId(),edge);
                corner.addEdge(edge);
                previousCorner.addEdge(edge);
            }
            previousCorner = corner;
        }

        createHarbors(tiles.get(tiles.size()-1), idToEdgeMap);

        for(Tile tile:tiles) {
            if(tile!=null) {
                tile.finalize();
            }
        }
        return new Board(idToEdgeMap,idToTileMap,idToCornerMap,tiles,settings.getMaxPlayers(),getResourceSet());
    }

    protected TileNumber[] getTileNumbers() {
        return tileNumbers;
    }

    protected List<Class<? extends Resource>> getHarbors() {
        return harbors;
    }

    protected List<Class<? extends Resource>> getResources() {
        return resources;
    }

    public int getMinPlayers() {
        return 2;
    }

    public int getMaxPlayers() {
        return 4;
    }

    public String getName() {
        return NAME;
    }

    protected Set<Class<? extends Resource>> getResourceSet() {
        return new HashSet<>(Arrays.asList(Sheep.class,Wood.class,Brick.class,Wheat.class,Ore.class));
    }

    private void createHarbors(Tile tile, Map<Integer,Edge> idToEdgeMap) {
        Edge startingEdge = tile.getTopEdge();
        if(startingEdge.getTiles().size() != 1) {
            for(Edge testEdge:tile.getEdges()) {
                if(testEdge.getTiles().size()==1) {
                    startingEdge = testEdge;
                    break;
                }
            }
        }

        Edge currentEdge = startingEdge;
        int previousHarborHop = 3;
        int currentHarborHop = 3;
        Edge previousEdge = null;
        while(true) {
            currentEdge = new HarborEdge(currentEdge, getHarborTradeMap());
            idToEdgeMap.put(currentEdge.getId(), currentEdge);
            if(currentEdge.getId() == startingEdge.getId()) {
                startingEdge = currentEdge;
            }

            for(int i = 0; i < currentHarborHop; i ++) {
                boolean foundEdge = false;
                for (Corner corner : currentEdge.getCorners()) {
                    for (Edge nextEdge : corner.getEdges()) {
                        if (nextEdge != previousEdge && nextEdge != currentEdge && nextEdge != startingEdge && nextEdge.getTiles().size() == 1) {
                            previousEdge = currentEdge;
                            currentEdge = nextEdge;
                            foundEdge = true;
                            break;
                        }
                    }
                    if(foundEdge) {
                        break;
                    }
                }
                if (!foundEdge) {
                    return;
                }
            }

            if(!Collections.disjoint(currentEdge.getCorners(),(startingEdge.getCorners()))) {
                return;
            }

            int newHarborHop = (previousHarborHop==currentHarborHop?4:3);
            previousHarborHop=currentHarborHop;
            currentHarborHop=newHarborHop;
        }
    }

    private Edge createEdge(int id) {
        return new Edge(id);
    }

    private Map<Class<? extends Resource>, Integer> getHarborTradeMap() {
        if(shuffledHarbors.isEmpty()) {
            shuffledHarbors.addAll(getHarbors());
            Collections.shuffle(shuffledHarbors, new Random(System.nanoTime()));
        }
        Class<? extends Resource> harbor = shuffledHarbors.remove(0);
        Map<Class<? extends Resource>, Integer> tradeMap = new HashMap<>();
        if (harbor != null) {
            tradeMap.put(harbor, 2);
        } else {
            for (Class<? extends Resource> resource : resourceService.getResources()) {
                tradeMap.put(resource, 3);
            }
        }
        return tradeMap;
    }

    protected int prepareLayers(List<? extends Tile> tiles) {
        int maxLayer = 0;
        for(int i=1;i<=tiles.size();i++) {
            Tile tile = tiles.get(i-1);
            if(tile!=null) {
                maxLayer = calculateLayer(i).intValue();
                tile.setLayer(maxLayer);
                int loc = calculateLoc(i, tile.getLayer());
                tile.setX(calculateX(tile.getLayer(), loc));
                tile.setY(calculateY(tile.getLayer(), loc));
                tile.setCornerOrder(calculateCornerOrder(i,tile.getLayer(),tile.getX(),tile.getY()));
            }
        }
        return maxLayer;
    }

    private Double calculateLayer(int id) {
        if (id<=1) {
            return 1.0;
        }
        Double layer = Math.ceil((1/6.0)*(3+sqrt(3)*sqrt(-1+4*(id))));
        return (layer<0?2:layer);
    }

    private int calculateLoc(int id, int layer) {
        if (id<=1) {
            return 1;
        }
        return id - ((6*(layer-2)*(layer-1))/2+1);
    }

    private int calculateX(int layer, int loc) {
        int left = loc ;
        int total = 0;
        int reps = layer-1;

        for(int i = 0; i<reps;i++) {
            total+=1;
            if(--left == 0) return total;
        }
        for(int i = 0; i<reps;i++) {
            if(--left == 0) return total;
        }
        for(int i = 0; i<reps*2;i++) {
            total-=1;
            if(--left == 0) return total;
        }
        for(int i = 0; i<reps;i++) {
            if(--left == 0) return total;
        }
        for(int i = 0; i<layer-2;i++) {
            total+=1;
            if(--left == 0) return total;
        }
        return 0;
    }

    private int calculateY(int layer, int loc) {
        int left = loc;
        int total = (layer - 1) * -2;
        int reps = layer-1;

        for(int i = 0; i<reps;i++) {
            total+=1;
            if(--left == 0) return total;
        }

        for(int i = 0; i<reps;i++) {
            total+=2;
            if(--left == 0) return total;
        }

        for(int i = 0; i<reps;i++) {
            total+=1;
            if(--left == 0) return total;
        }

        for(int i = 0; i<reps;i++) {
            total-=1;
            if(--left == 0) return total;
        }

        for(int i = 0; i<reps;i++) {
            total-=2;
            if(--left == 0) return total;
        }

        for(int i = 0; i<layer-2;i++) {
            total-=1;
            if(--left == 0) return total;
        }
        return (layer - 1) * -2;
    }

    private Integer[] calculateCornerOrder(int id, int layer, int X, int Y) {
        if(id==1) {
            return new Integer[]{1,2,3,4,5,6};
        }
        if(id==2) {
            return new Integer[]{3,4,5,6,1,2};
        }
        int maxX = (layer-1);
        int maxY = (layer-1)*2;
        if(X == 0){
            if(Y == -maxY) return CornerOrder.N.getOrder();
            else return CornerOrder.S.getOrder();
        }
        if(X == 1 && Y < 0) {
            return CornerOrder.NE_A.getOrder();
        }
        if(X > 1 && Y < -maxY/2) {
            return CornerOrder.NE_B.getOrder();
        }
        if(X == maxX) {
            if(Y == -maxY/2) return CornerOrder.C_NE.getOrder();
            else if(Y == maxY/2) return CornerOrder.C_SE.getOrder();
            else return CornerOrder.E.getOrder();
        }
        if(X > 0 && Y > maxY/2) {
            return CornerOrder.SE.getOrder();
        }
        if(X < 0) {
            if(Y > maxY/2) return CornerOrder.SW.getOrder();
            else if(Y < -maxY/2) return CornerOrder.NW.getOrder();
        }
        if(X == -maxX) {
            if(Y == maxY/2) return CornerOrder.C_SW.getOrder();
            else if(Y == -maxY/2) return CornerOrder.C_NW.getOrder();
            else return CornerOrder.W.getOrder();
        }
        return null;
    }

    public List<int[]> getLayerOfTilesForHosts(int layer) {
        if(layer == 1) {
            return Arrays.asList(new int[]{1,2,3},
                    new int[]{1,3,4},
                    new int[]{1,4,5},
                    new int[]{1,5,6},
                    new int[]{1,6,7},
                    new int[]{1,2,7});
        }

        List<int[]> hostTilesLists = new ArrayList<>();

        int base = 2 + 6*(((layer-2)*(layer-1))/2);
        int top = 2 + 6*(((layer-1)*layer)/2);

        hostTilesLists.add(new int[]{base,top-1,top});

        for(int i = 0; i < 5; i++) {
            for(int j = 0; j<layer-2; j++) {
                hostTilesLists.add(new int[]{base,top,top+1});
                hostTilesLists.add(new int[]{base,base+1,top+1});
                base++;top++;
            }
            hostTilesLists.add(new int[]{base,top,top+1});
            hostTilesLists.add(new int[]{base,top+1,top+2});
            hostTilesLists.add(new int[]{base,base+1,top+2});
            base++;top+=2;
        }
        for(int j = 0; j<layer-2; j++) {
            hostTilesLists.add(new int[]{base,top,top+1});
            hostTilesLists.add(new int[]{base,base+1,top+1});
            base++;top++;
        }
        hostTilesLists.add(new int[]{base,top,top+1});
        hostTilesLists.add(new int[]{base,top+1,base+1});

        return hostTilesLists;
    }




}
