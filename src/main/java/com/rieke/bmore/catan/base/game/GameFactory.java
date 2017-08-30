package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.Board;
import com.rieke.bmore.catan.base.resources.ResourceService;
import com.rieke.bmore.common.connection.ConnectionFactory;
import org.springframework.stereotype.Service;

/**
 * Created by tcrie on 8/7/2017.
 */
@Service
public class GameFactory {
    private ResourceService resourceService;

    public GameFactory(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public Game create(String id, Board board) {
        return new Game(id, new ConnectionFactory(), board, resourceService);
    }

}
