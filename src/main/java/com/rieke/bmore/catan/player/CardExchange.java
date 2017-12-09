package com.rieke.bmore.catan.player;

import java.util.HashSet;
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
        sanatize(discard);
        sanatize(receive);
    }

    private void sanatize(Map<String, Integer> resources) {
        if(resources != null) {
            for(Map.Entry<String, Integer> resource:new HashSet<>(resources.entrySet())) {
                if(resource.getValue().intValue() == 0) {
                    resources.remove(resource.getKey());
                }
            }
        }
    }

    public Map<String, Integer> getDiscard() {
        return discard;
    }

    public void setDiscard(Map<String, Integer> discard) {
        sanatize(discard);
        this.discard = discard;
    }

    public Map<String, Integer> getReceive() {
        return receive;
    }

    public void setReceive(Map<String, Integer> receive) {
        sanatize(receive);
        this.receive = receive;
    }
}
