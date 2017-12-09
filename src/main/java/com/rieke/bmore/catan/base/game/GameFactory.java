package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.pieces.PieceService;
import com.rieke.bmore.catan.base.resources.ResourceService;
import com.rieke.bmore.common.connection.ConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * Created by tcrie on 8/7/2017.
 */
@Service
public class GameFactory {
    private ResourceService resourceService;
    private PieceService pieceService;

    public GameFactory(ResourceService resourceService, PieceService pieceService) {
        this.resourceService = resourceService;
        this.pieceService = pieceService;
    }

    public Game create(String id, Settings settings, Board board) {
        return new Game(id, settings, new ConnectionFactory(), board, resourceService, pieceService);
    }

}
