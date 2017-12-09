package com.rieke.bmore.catan.base.game;

import com.rieke.bmore.catan.base.board.Board;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class GameService {

    private GameFactory gameFactory;
    private Map<String,Game> gameMap;

    public GameService(GameFactory gameFactory) {
        this.gameFactory = gameFactory;
        gameMap = new HashMap<>();
    }

    public synchronized Game createGame(Board board, Settings settings) {
        return createGame(board, settings, getSaltString());
    }

    public synchronized Game createGame(Board board, Settings settings, String id) {
        Game game = gameFactory.create(id, settings, board);
        gameMap.put(id, game);
        return game;
    }

    public Game getGame(String id) {
        return gameMap.get(id);
    }

    public Game startGame(String id) {
        Game game = gameMap.get(id);
        game.init();
        return game;
    }

    private String generateId() {
        String id;
        do {
            id = getSaltString();
        } while (gameMap.get(id) != null);
        return id;
    }

    private String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
