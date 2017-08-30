package com.rieke.bmore.catan.player;

import java.util.Map;

/**
 * Created by tcrie on 8/25/2017.
 */
public class CardExchange {
    private Map<String, Integer> discard;
    private Map<String, Integer> receive;

    public CardExchange() {
    }

    public CardExchange(Map<String, Integer> discard, Map<String, Integer> receive) {
        this.discard = discard;
        this.receive = receive;
    }

    public Map<String, Integer> getDiscard() {
        return discard;
    }

    public void setDiscard(Map<String, Integer> discard) {
        this.discard = discard;
    }

    public Map<String, Integer> getReceive() {
        return receive;
    }

    public void setReceive(Map<String, Integer> receive) {
        this.receive = receive;
    }
}
