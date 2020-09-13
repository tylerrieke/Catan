package com.rieke.bmore.catan.player;

import com.google.appengine.repackaged.com.google.common.hash.Hashing;
import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.game.GameService;
import com.rieke.bmore.catan.base.resources.ResourceSelection;
import com.rieke.bmore.catan.gui.CatanStartup;
import com.rieke.bmore.catan.turn.Turn;
import com.rieke.bmore.common.player.InvalidPlayerException;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/player")
public class PlayerController {

    @Autowired
    private GameService gameService;

    @RequestMapping(method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getPlayer(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        if (game != null) {
            Turn turn = game.getCurrentTurn();
            CatanPlayer player = game.getPlayer(getPlayerToken(request));
            if(player != null) {
                responseMap.put("player", player);
                responseMap.put("active", player.equals(game.getActivePlayer()));
                responseMap.put("state", game.getState());
                responseMap.put("confirm", player.equals(game.getActivePlayer()) && turn.isConfirmPrompt());
                responseMap.put("actions", game.getTurnActions());
                responseMap.put("discardCount", game.getDiscardCount(player));
                responseMap.put("tradeRequest", game.getTradeRequest(player));
                responseMap.put("tradeResponses", game.getTradeResponses(player));
                responseMap.put("canCancel", game.canCancel(player));
                return new ResponseEntity<>(responseMap,
                        HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(responseMap,
                    HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> update(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "display") String display,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.updatePlayer(getPlayerToken(request),name,display);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/build_selection", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> build_selection(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.startBuildSelection(game.getPlayer(getPlayerToken(request)),type);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/build_settlement", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> buildSettlement(
            @RequestParam(value = "cornerId") int cornerId,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.createSettlement(game.getPlayer(getPlayerToken(request)),cornerId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/build_road", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> buildRoad(
            @RequestParam(value = "edgeId") int edgeId,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.createRoad(game.getPlayer(getPlayerToken(request)),edgeId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/robber", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> robber(
            @RequestParam(value = "tileId") int tileId,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.playRobber(game.getPlayer(getPlayerToken(request)),tileId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/roll", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> roll(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.roll(game.getPlayer(getPlayerToken(request)));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/end_turn", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> endTurn(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.endTurn(game.getPlayer(getPlayerToken(request)));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }
    @RequestMapping(value = "/exchange", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> discard(
            @RequestParam(value = "gameId") String gameId,
            @RequestBody CardExchange exchange,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.applyExchange(game.getPlayer(getPlayerToken(request)),exchange);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/confirm", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> ok(
            @RequestParam(value = "ok") boolean ok,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        CatanPlayer player = game.getPlayer(getPlayerToken(request));
        game.ok(player,ok);
        responseMap.put("player", player);
        responseMap.put("active", player.equals(game.getActivePlayer()));
        responseMap.put("state", game.getState());
        responseMap.put("confirm", false);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> cancel(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        CatanPlayer player = game.getPlayer(getPlayerToken(request));
        game.cancelTurn(player);
        responseMap.put("player", player);
        responseMap.put("active", player.equals(game.getActivePlayer()));
        responseMap.put("state", game.getState());
        responseMap.put("confirm", false);
        responseMap.put("canCancel", false);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/robbable", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getRobbable(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("robbable", game.getRobbable(game.getPlayer(getPlayerToken(request))));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/buildable", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getBuildable(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("buildable", game.getPlayer(getPlayerToken(request)).getAvailablePieces());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/card_selection", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getCardSelection(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("selection", game.getResourceSelection(game.getPlayer(getPlayerToken(request))));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/card_selection", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> playCards(
            @RequestParam(value = "gameId") String gameId,
            @RequestBody Map<String, Integer> selection,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.playCardSelection(game.getPlayer(getPlayerToken(request)),selection);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/dcs", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getDCs(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("dcs", game.getPlayer(getPlayerToken(request)).getAvailableDCs());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/play_dc", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> play_dc(
            @RequestParam(value = "type") String type,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.playDC(game.getPlayer(getPlayerToken(request)),type);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/tradeable", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getTradeable(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("tradeable", game.getPlayer(getPlayerToken(request)).getTradeable());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/rob", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> rob(
            @RequestParam(value = "gameId") String gameId,
            @RequestParam(value = "robbedId") int robbedId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.robPlayer(game.getPlayer(getPlayerToken(request)), robbedId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/answer_trade", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> answer(
            @RequestParam(value = "gameId") String gameId,
            @RequestParam(value = "answer") boolean answer,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.answerTradeRequest(game.getPlayer(getPlayerToken(request)), answer);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/accept_trade", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> acceptTrade(
            @RequestParam(value = "gameId") String gameId,
            @RequestParam(value = "acceptedId", required = false) Integer accepted,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.acceptPlayerTrade(game.getPlayer(getPlayerToken(request)), accepted);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/init_trade", method = RequestMethod.POST, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> initTrade(
            @RequestParam(value = "gameId") String gameId,
            @RequestBody CardExchange exchange,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.requestTrade(game.getPlayer(getPlayerToken(request)),exchange);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @PostConstruct
    public void start() {
        //CatanStartup.jettyServer.afterDeployed();
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (null != ip && !"".equals(ip.trim())
                && !"unknown".equalsIgnoreCase(ip)) {
            // get first ip from proxy ip
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        return request.getRemoteAddr();
    }

    public static String getPlayerToken(HttpServletRequest request) {
        return Hashing.sha256()
                .hashString(getIpAddr(request) + request.getHeader("User-Agent"), StandardCharsets.UTF_8)
                .toString();
    }
}
