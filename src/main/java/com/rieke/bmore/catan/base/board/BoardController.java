package com.rieke.bmore.catan.base.board;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.game.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tcrie on 7/25/2017.
 */
@Controller
@RequestMapping("/board")
public class BoardController {

    private BasicBoardFactory boardFactory;
    private GameService gameService;

    public BoardController(BasicBoardFactory boardFactory, GameService gameService) {
        this.boardFactory = boardFactory;
        this.gameService = gameService;
    }

    @PostConstruct
    public void testInit() {
        gameService.createGame(boardFactory.create(),"A");
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


    @RequestMapping(value = "/new", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> createBoard(
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.createGame(boardFactory.create());
        responseMap.put("board", game.getBoard().getTiles());
        responseMap.put("state", game.getState());
        responseMap.put("gameId", game.getId());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }
}
