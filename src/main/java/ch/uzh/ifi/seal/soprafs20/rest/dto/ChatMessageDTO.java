package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class ChatMessageDTO {

    private String message;
    private String username;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
