package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class GamePostDTO {

    private ArrayList<Long> playerIds;

    public ArrayList<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<Long> playerIds) {
        this.playerIds = playerIds;
    }
}
