package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Chat Representation
 * This class composes the internal representation of the chat and defines how the chat is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "CHAT")
public class Chat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column()
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> chatHistory = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(List<String> chatHistory) {
        this.chatHistory = chatHistory;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", chatHistory='" + chatHistory +
                '}';
    }
}
