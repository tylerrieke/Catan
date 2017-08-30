package com.rieke.bmore.catan.player;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.game.GameService;
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

import javax.servlet.http.HttpServletRequest;
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
        Turn turn = game.getCurrentTurn();
        CatanPlayer player = game.getPlayer(getIpAddr(request));
        responseMap.put("player", player);
        responseMap.put("active", player.equals(game.getActivePlayer()));
        responseMap.put("state", game.getState());
        responseMap.put("confirm", player.equals(game.getActivePlayer()) && turn.isConfirmPrompt());
        responseMap.put("actions", game.getTurnActions());
        responseMap.put("discardCount", game.getDiscardCount(player));
        responseMap.put("canCancel", game.canCancel(player));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> update(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "display") String display,
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.updatePlayer(getIpAddr(request),name,display);
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
        game.startBuildSelection(game.getPlayer(getIpAddr(request)),type);
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
        game.createSettlement(game.getPlayer(getIpAddr(request)),cornerId);
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
        game.createRoad(game.getPlayer(getIpAddr(request)),edgeId);
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
        game.playRobber(game.getPlayer(getIpAddr(request)),tileId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/roll", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> roll(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.roll(game.getPlayer(getIpAddr(request)));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/end_turn", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> endTurn(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        game.endTurn(game.getPlayer(getIpAddr(request)));
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
        game.applyExchange(game.getPlayer(getIpAddr(request)),exchange);
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
        CatanPlayer player = game.getPlayer(getIpAddr(request));
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
        CatanPlayer player = game.getPlayer(getIpAddr(request));
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
        responseMap.put("robbable", game.getRobbable(game.getPlayer(getIpAddr(request))));
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/buildable", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getBuildable(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("buildable", game.getPlayer(getIpAddr(request)).getAvailablePieces());
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/dcs", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getDCs(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("dcs", game.getPlayer(getIpAddr(request)).getAvailableDCs());
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
        game.playDC(game.getPlayer(getIpAddr(request)),type);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
    }

    @RequestMapping(value = "/tradeable", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Map<String, Object>> getTradeable(
            @RequestParam(value = "gameId") String gameId,
            HttpServletRequest request) throws InvalidPlayerException {
        Map<String, Object> responseMap = new HashMap<>();
        Game game = gameService.getGame(gameId);
        responseMap.put("tradeable", game.getPlayer(getIpAddr(request)).getTradeable());
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
        game.robPlayer(game.getPlayer(getIpAddr(request)), robbedId);
        return new ResponseEntity<>(responseMap,
                HttpStatus.OK);
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
}
