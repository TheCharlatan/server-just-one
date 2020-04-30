package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.CardStatus;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;

import java.time.LocalTime;
import java.util.List;


public class GameGetDTO {

    private Long id;
    private List<Long> playerIds;
    private int round;
    private GameStatus gameStatus;
    private CardStatus cardStatus;
    private int score;
    private Long activePlayerId;
    private List<String> clues;
    private LocalTime timestamp;
    private int wordsGuessedCorrect;
    private int wordsGuessedWrong;
    private int cardStackCount;
    private int cardGuessedCount;

    private int wordIndex;
    private List<String> words;

    public int getWordIndex() {return wordIndex;}
    public List<String> getWords() {return words;}
    
    public void setWordIndex(int wordIndex) {this.wordIndex = wordIndex;}
    public void setWords(List<String> words) {this.words = words;}

    public int getWordsGuessedCorrect() {return wordsGuessedCorrect;}
    public int getWordsGuessedWrong() {return wordsGuessedWrong;}
    public int getCardStackCount() {return cardStackCount;}
    public int getCardGuessedCount() {return cardGuessedCount;}

    public void setActivePlayerId(Long id){this.activePlayerId = id;}
    public void setWordsGuessedCorrect(int i) {this.wordsGuessedCorrect = i;}
    public void setWordsGuessedWrong(int i) {this.wordsGuessedWrong = i;}
    public void setCardStackCount(int i) {this.cardStackCount = i;}
    public void setCardGuessedCount(int i) {this.cardGuessedCount = i;}

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

    public LocalTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalTime timestamp) {
        this.timestamp = timestamp;
    }


    public CardStatus getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(CardStatus cardStatus) {
        this.cardStatus = cardStatus;
    }
}
