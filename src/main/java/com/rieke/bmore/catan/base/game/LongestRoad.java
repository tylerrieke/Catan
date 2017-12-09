package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;
import com.rieke.bmore.catan.base.pieces.Road;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.*;

public class LongestRoad extends Special {
    public static final int MINIMUM_LENGTH = 5;

    public LongestRoad(Game game, String name, int points) {
        super(game, name, points, MINIMUM_LENGTH);
    }

    public int evaluateOne(CatanPlayer player) {
        int maxLength = 0;
        List<Corner> ends = new ArrayList<>();
        List<Road> playerRoads = new ArrayList<>(player.getPiecesByType(Road.class));
        for(Road road:playerRoads) {
            if(road.getBoardItem() != null) {
                for (Corner corner : road.getBoardItem().getCorners()) {
                    if (corner.getPiece() != null && !player.equals(corner.getPiece().getPlayer())) {
                        ends.add(corner);
                        break;
                    }
                    int emptyCount = 0;
                    for (Edge edge : corner.getEdges()) {
                        if (edge.getRoad() == null || !player.equals(edge.getRoad().getPlayer())) {
                            emptyCount++;
                        }
                    }
                    if (emptyCount == corner.getEdges().size() - 1) {
                        ends.add(corner);
                        break;
                    }
                }
            }
        }
        Set<Road> unique = new HashSet<>();
        for(Corner end:ends) {
            List<Road> endRoadPath = evaluateCorner(player, end, new ArrayList<Road>());
            unique.addAll(endRoadPath);
            if(endRoadPath.size() > maxLength) {
                maxLength = endRoadPath.size();
            }
        }
        playerRoads.removeAll(unique);
        for(Road road:playerRoads) {
            if(road.getBoardItem() != null) {
                int size = evaluateCorner(player, road.getBoardItem().getCorners().iterator().next(), new ArrayList<Road>()).size();
                if(size > maxLength) {
                    maxLength = size;
                }
            }
        }
        player.setLongestRoad(maxLength);
        return maxLength;
    }

    private List<Road> evaluateCorner(CatanPlayer player, Corner corner, List<Road> previousRoads) {
        List<Road> longestChild = new ArrayList<>();
        if(corner.getPiece() == null || player.equals(corner.getPiece().getPlayer())) {
            for(Edge edge:corner.getEdges()) {
                Road road = edge.getPiece();
                if(road !=null && player.equals(road.getPlayer()) && !previousRoads.contains(road)) {
                    List<Road> childPrevious = new ArrayList<>(previousRoads);
                    childPrevious.add(road);
                    List<Road> child = new ArrayList<>();
                    for(Corner subCorner:edge.getCorners()) {
                        if(!corner.equals(subCorner)) {
                            child = evaluateCorner(player, subCorner, childPrevious);
                            child.add(road);
                        }
                    }
                    if(child.size() > longestChild.size()) {
                        longestChild = child;
                    }
                }
            }
        }
        return longestChild;
    }
}
