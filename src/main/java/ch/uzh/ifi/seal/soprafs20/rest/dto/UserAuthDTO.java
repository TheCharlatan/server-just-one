package ch.uzh.ifi.seal.soprafs20.rest.dto;

public final class UserAuthDTO {
    private String token;
    private Long id;

    public UserAuthDTO(String token, Long id){
        this.token = token;
        this.id = id;
    }

    public String getToken() {
        return token;
    }
    public Long getId() {
        return id;
    }

}
