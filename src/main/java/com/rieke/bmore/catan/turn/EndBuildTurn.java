package com.rieke.bmore.catan.turn;

import com.google.common.collect.Lists;
import com.rieke.bmore.catan.player.CatanPlayer;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tcrie on 12/9/2017.
 */
public class EndBuildTurn extends Turn {
    public EndBuildTurn(CatanPlayer player) {
        super(player, false);
    }

    @Override
    public List<String> getActions() {
        return Lists.newArrayList( Arrays.asList(State.BUILD.toString(), State.END.toString()));
    }
}
