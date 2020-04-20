package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.List;

public class LobbyGetDTO {

    private Long id;
    private String name;
    private Long hostPlayerId;
    private List<Long> playerIds;
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

    public void sethostPlayerId(Long hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
