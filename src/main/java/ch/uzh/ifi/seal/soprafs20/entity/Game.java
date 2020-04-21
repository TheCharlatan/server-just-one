package ch.uzh.ifi.seal.soprafs20.entity;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Internal Game Representation
 * This class composes the internal representation of the game and defines how the game is stored in the database.
 * Every variable will be mapped into a database field with the @Column annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes the primary key
 */
@Entity
@Table(name = "GAME")
public class Game implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column()
    @ElementCollection
    private List<Long> playerIds = new ArrayList<Long>();

    @Column(nullable = false)
    private int round;

    @Column(nullable = false)
    private GameStatus gameStatus;

    @Column()
    @ElementCollection
    private List<String> words = new ArrayList<String>();

    @Column(nullable = false)
    private int wordIndex;

    @Column(nullable = false)
    private int score;

    @Column(nullable = false)
    private Long activePlayerId;

    @Column()
    @ElementCollection
    private List<String> clues = new ArrayList<String>();

    @Column(nullable = false)
    private int timestamp;

    @Column(nullable = false)
    private int wordsGuessedCorrect;

    @Column(nullable = false)
    private int wordsGuessedWrong;

    @Column(nullable = false)
    private int cardStackCount;

    @Column(nullable = false)
    private int cardGuessedCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(List<Long> playerIds) {
        this.playerIds = playerIds;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Long getActivePlayer() {
        return activePlayerId;
    }

    public void setActivePlayer(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    public List<String> getClues() {
        return clues;
    }

    public void setClues(List<String> clues) {
        this.clues = clues;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getWordsGuessedCorrect() {return  wordsGuessedCorrect;}

    public void setWordsGuessedCorrect(int wordsGuessedCorrect) {this.wordsGuessedCorrect = wordsGuessedCorrect;}

    public int getWordsGuessedWrong() {return  wordsGuessedWrong;}

    public void setWordsGuessedWrong(int wordsGuessedWrong) {this.wordsGuessedWrong = wordsGuessedWrong;}

    public int getCardStackCount() {return  cardStackCount;}

    public void setCardStackCount(int cardStackCount) {this.cardStackCount = cardStackCount;}

    public int getCardGuessedCount() {return  cardGuessedCount;}

    public void setCardGuessedCount(int cardGuessedCount) {this.cardGuessedCount = cardGuessedCount;}




}
