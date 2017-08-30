package com.rieke.bmore.catan.player;

import com.rieke.bmore.common.connection.ConnectionFactory;
import com.rieke.bmore.common.player.PlayerService;

/**
 * Created by tcrie on 8/6/2017.
 */
public class CatanPlayerService extends PlayerService<CatanPlayer> {
    public CatanPlayerService(CatanPlayerFactory playerFactory, ConnectionFactory connectionFactory) {
        super(playerFactory, connectionFactory);
    }
}
