package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Internal Lobby Representation
 * This class composes the internal representation of the lobby and defines how the lobby is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "LOBBY")
public class Lobby implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long hostPlayerId;

    @Basic
    private ArrayList<Long> playerIds;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
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

    public ArrayList<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }
}
