package com.rieke.bmore.catan.base.board.item.tile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rieke.bmore.catan.base.board.item.BoardItem;
import com.rieke.bmore.catan.base.board.item.SelectableBoardItem;
import com.rieke.bmore.catan.base.board.item.corner.Corner;
import com.rieke.bmore.catan.base.board.item.edge.Edge;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;

public abstract class Tile extends SelectableBoardItem {
    private static final int MAX_NUM_EDGES = 6;
    private static final int MAX_NUM_CORNERS = 6;

    private LinkedHashSet<Corner> corners = new LinkedHashSet<>();
    private LinkedHashSet<Edge> edges = new LinkedHashSet<>();
    private List<Integer> cornerOrder;
    private int x;
    private int y;
    private int layer;

    public Tile(int id) {
        super(id);
    }

    public void finalize() {
        LinkedHashSet<Edge> finalizedEdges = new LinkedHashSet<>();
        LinkedHashSet<Corner> finalizedCorners = new LinkedHashSet<>();
        List<Corner> sorted = new ArrayList<>(corners);

        Collections.sort(sorted, new Comparator<Corner>() {
            @Override
            public int compare(Corner a, Corner b) {
                return a.getId() - b.getId();
            }
        });
        for(int i = 1; i <= cornerOrder.size(); i++) {
            finalizedCorners.add(sorted.get(cornerOrder.indexOf(i)));
        }
        this.corners = finalizedCorners;
        List<Corner> tmpList= new ArrayList<>(finalizedCorners);

        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(0),tmpList.get(5)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(1),tmpList.get(0)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(2),tmpList.get(1)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(3),tmpList.get(2)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(4),tmpList.get(3)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        for(Edge edge:edges) {
            if(edge.getCorners().containsAll(Arrays.asList(new Corner[]{tmpList.get(5),tmpList.get(4)}))) {
                finalizedEdges.add(edge);
                break;
            }
        }
        this.edges = finalizedEdges;
    }

    public Tile addCorner(Corner corner) {
        if(corners.size() == MAX_NUM_CORNERS && !corners.contains(corner)) {
            //TODO: Throw exception
        } else {
            corners.add(corner);
            corner.addTile(this);
        }
        return this;
    }

    public Tile addEdge(Edge edge) {
        if(edges.size() == MAX_NUM_EDGES && !edges.contains(edge)) {
            //TODO: Throw exception
        } else {
            edges.add(edge);
            edge.addTile(this);
        }
        return this;
    }

    public LinkedHashSet<Corner> getCorners() {
        return corners;
    }

    public LinkedHashSet<Edge> getEdges() {
        return edges;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(int layer) {
        this.layer = layer;
    }

    @JsonIgnore
    public List<Integer> getCornerOrder() {
        return cornerOrder;
    }

    public void setCornerOrder(Integer[] cornerOrder) {
        this.cornerOrder = Arrays.asList(cornerOrder);
    }
}
