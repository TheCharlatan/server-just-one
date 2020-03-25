package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class LobbyGetDTO {

    private Long id;
    private String name;
    private Long hostPlayerId;
    private ArrayList<Long> playerIds;
    private Long gameId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long gethostPlayerId() {
        return hostPlayerId;
    }

    public void sethostPlayerId(Long hostPlayerIdPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public ArrayList<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
