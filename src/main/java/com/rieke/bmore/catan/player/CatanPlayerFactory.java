package com.rieke.bmore.catan.player;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.common.connection.Connection;
import com.rieke.bmore.common.connection.ConnectionFactory;
import com.rieke.bmore.common.player.PlayerFactory;

public class CatanPlayerFactory extends PlayerFactory<CatanPlayer> {
    private int maxPlayers;
    private Game game;

    public CatanPlayerFactory(ConnectionFactory connectionFactory, int maxPlayers, Game game) {
        super(connectionFactory);
        this.maxPlayers = maxPlayers;
        this.game = game;
    }

    @Override
    protected CatanPlayer initPlayer(Connection connection, String s, String s1) {
        if(getAllPlayers().size()==maxPlayers) {
            return null;
        }
        return new CatanPlayer(connection, s, s1, game);
    }
}
