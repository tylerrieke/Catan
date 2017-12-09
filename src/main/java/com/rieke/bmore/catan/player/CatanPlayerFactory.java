package com.rieke.bmore.catan.player;

import com.rieke.bmore.catan.base.game.Game;
import com.rieke.bmore.common.connection.Connection;
import com.rieke.bmore.common.connection.ConnectionFactory;
import com.rieke.bmore.common.player.PlayerFactory;

import java.util.*;

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

    @Override
    public Collection<CatanPlayer> getAllPlayers() {
        List<CatanPlayer> players = new ArrayList<>(super.getAllPlayers());
        Collections.sort(players, new Comparator<CatanPlayer>() {
            @Override
            public int compare(CatanPlayer o1, CatanPlayer o2) {
                return Integer.valueOf(""+(o2.getOrder()-o1.getOrder()));
            }
        });
        return players;
    }
}
