package com.rieke.bmore.catan.base.board;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.game.GameService;
import com.rieke.bmore.catan.base.game.Settings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tcrie on 7/25/2017.
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    private Map<String, BasicBoardFactory> boardFactories;
    private GameService gameService;

    public BoardController(GameService gameService) {
        this.gameService = gameService;
    }

    @Autowired
    public void setResources(List<BasicBoardFactory> boardFactories) {
        this.boardFactories = new HashMap<>();
        for(BasicBoardFactory factory:boardFactories) {
            this.boardFactories.put(factory.getName(), factory);
        }
    }

    @PostConstruct
    public void testInit() {
        gameService.createGame(
                boardFactories.get(BasicBoardFactory.NAME).create(
                        new Settings()),new Settings(),"A");
    }

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getBoard(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("board", game.getBoard().getTiles());
        responseMap.put("state", game.getState());
        responseMap.put("players", game.getSimplePlayers());
        responseMap.put("dice1", game.getDice1());
        responseMap.put("dice2", game.getDice2());
        responseMap.put("actions", game.getTurnActions());
        responseMap.put("robberId", game.getRobberId());
        responseMap.put("robberFriendly", game.isFriendlyRobber());
        responseMap.put("pieceCosts", game.getPieceCosts());
        responseMap.put("message", game.getMessage());
        if(game.getCurrentTurn() != null) {
            responseMap.put("currentPlayer", game.getCurrentTurn().getPlayer().getName());
        }
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/start", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> startBoard(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        gameService.startGame(gameId);
        responseMap.put("board", game.getBoard().getTiles());
        responseMap.put("state", game.getState());
        responseMap.put("players", game.getSimplePlayers());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }


    @RequestMapping(value = "/new", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> createBoard(
            @RequestBody Settings settings,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        BasicBoardFactory boardFactory = boardFactories.get(settings.getBoardType());
        boardFactory = (boardFactory!=null
                ?boardFactory
                :(settings.getNumberOfTiles()>19
                    ? boardFactories.get(ExpansionBoardFactory.NAME)
                    : boardFactories.get(BasicBoardFactory.NAME)));
        Game game = gameService.createGame(boardFactory.create(settings),settings);
        responseMap.put("board", game.getBoard().getTiles());
        responseMap.put("state", game.getState());
        responseMap.put("gameId", game.getId());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/friendly", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> toggleFriendly(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.setFriendlyRobber(!game.isFriendlyRobber());
        responseMap.put("board", game.getBoard().getTiles());
        responseMap.put("state", game.getState());
        responseMap.put("gameId", game.getId());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/playerFirst", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> playerFirst(
            @RequestParam(value = "gameId") String gameId,
            @RequestParam(value = "playerName") String name,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.makePlayerFirst(name);
        responseMap.put("players", game.getSimplePlayers());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }
}
