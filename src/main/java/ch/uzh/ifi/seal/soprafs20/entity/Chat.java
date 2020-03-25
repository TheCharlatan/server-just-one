package ch.uzh.ifi.seal.soprafs20.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;

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
    @GeneratedValue
    private Long id;

    @Basic
    private ArrayList<String> chatHistory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<String> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(ArrayList<String> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
