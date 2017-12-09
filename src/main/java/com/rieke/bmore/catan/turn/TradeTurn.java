package com.rieke.bmore.catan.turn;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.catan.base.resources.Resource;
import com.rieke.bmore.catan.player.CardExchange;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tcrie on 9/5/2017.
 */
public class TradeTurn extends CancellableTurn {

    private Map<CatanPlayer,Boolean> playersTradeMap;
    private CardExchange tradeOffer;
    private CatanPlayer acceptedPlayer = null;
    private Game game;

    public TradeTurn(CatanPlayer player, List<CatanPlayer> players, CardExchange tradeOffer, Game game) {
        super(player, true);
        this.playersTradeMap = new HashMap<>();
        for(CatanPlayer tmpPlayer:players) {
            if(!tmpPlayer.equals(player)) {
                this.playersTradeMap.put(tmpPlayer,null);
            }
        }
        this.tradeOffer = tradeOffer;
        this.game = game;
    }

    @Override
    public boolean applyState() {
        if(super.applyState()) {
            return true;
        }
        if(acceptedPlayer != null) {
            return onSuccess();
        }
        return false;
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean onSuccess() {
        for(Map.Entry<String, Integer> entry:tradeOffer.getReceive().entrySet()) {
            Class<? extends Resource> resource = game.getResourceService().getResourceByName(entry.getKey());
            getPlayer().addResource(resource,entry.getValue());
            acceptedPlayer.addResource(resource,-entry.getValue());
        }
        for(Map.Entry<String, Integer> entry:tradeOffer.getDiscard().entrySet()) {
            Class<? extends Resource> resource = game.getResourceService().getResourceByName(entry.getKey());
            getPlayer().addResource(resource,-entry.getValue());
            acceptedPlayer.addResource(resource,entry.getValue());
        }
        return true;
    }

    public void setAcceptedPlayer(CatanPlayer acceptedPlayer) {
        this.acceptedPlayer = acceptedPlayer;
    }

    public void setPlayerResponse(CatanPlayer player, boolean response) {
        playersTradeMap.put(player,response);
    }

    public boolean waitingPlayerResponse(CatanPlayer player) {
        return playersTradeMap.containsKey(player) && playersTradeMap.get(player) == null;
    }

    public Map<CatanPlayer, Boolean> getPlayerResponses() {
        return playersTradeMap;
    }

    public CardExchange getTradeOffer() {
        return tradeOffer;
    }
}
