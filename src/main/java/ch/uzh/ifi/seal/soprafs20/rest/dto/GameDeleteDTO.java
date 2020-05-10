package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GameDeleteDTO {
    private long userId;
    private boolean browserClose;
    private long lobbyId;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public boolean isBrowserClose() {
        return browserClose;
    }

    public void setBrowserClose(boolean browserClose) {
        this.browserClose = browserClose;
    }

    public long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
