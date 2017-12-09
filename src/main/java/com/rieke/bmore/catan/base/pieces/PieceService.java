package com.rieke.bmore.catan.base.pieces;

import com.rieke.bmore.catan.base.resources.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by tcrie on 11/8/2017.
 */
@Service
public class PieceService {
    private Map<String, Piece> pieceMap;
    private SortedMap<String,Map<String,Integer>> pieceCosts = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String piece1, String piece2) {
            return getLegendSortValueByDisplayName(piece1) - getLegendSortValueByDisplayName(piece2);
        }
    });

    public PieceService() {

    }

    @Autowired
    public void setPieces(List<Piece> pieces) {
        pieceMap = new HashMap<>();
        for(Piece piece:pieces) {
            String displayName = piece.getSpecificDisplayName();
            pieceMap.put(displayName, piece);
            Map<Class<? extends Resource>,Integer> cost = piece.getCost();
            Map<String,Integer> displayableCost = new HashMap<>();
            for(Map.Entry<Class<? extends Resource>,Integer> entry: cost.entrySet()) {
                displayableCost.put(entry.getKey().getSimpleName(), entry.getValue());
            }
            pieceCosts.put(displayName, displayableCost);
        }
    }

    public int getLegendSortValueByDisplayName(String name) {
        return pieceMap.get(name).getLegendSortValue();
    }

    public SortedMap<String, Map<String, Integer>> getPieceCosts() {
        return pieceCosts;
    }
}
