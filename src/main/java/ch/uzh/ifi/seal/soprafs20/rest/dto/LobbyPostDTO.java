package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.List;

public class LobbyPostDTO {

    private String name;
    private Long hostPlayerId;
    private List<Long> playerIds;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getHostPlayerId() {
        return hostPlayerId;
    }

    public void setHostPlayerId(Long hostPlayerId) {
        this.hostPlayerId = hostPlayerId;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }
}
