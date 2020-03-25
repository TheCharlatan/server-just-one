package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;

import java.util.ArrayList;


public class GameGetDTO {

    private Long id;
    private ArrayList<Long> playerIds;
    private int round;
    private GameStatus gameStatus;
    private int score;
    private Long activePlayerId;
    private ArrayList<String> clues;
    private int timestamp;
    private String currentWord;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ArrayList<Long> getPlayerIds() {
        return playerIds;
    }

    public void setPlayerIds(ArrayList<Long> playerIds) {
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

    public ArrayList<String> getClues() {
        return clues;
    }

    public void setClues(ArrayList<String> clues) {
        this.clues = clues;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }
}
