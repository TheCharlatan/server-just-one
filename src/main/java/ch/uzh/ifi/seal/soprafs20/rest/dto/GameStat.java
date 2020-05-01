package ch.uzh.ifi.seal.soprafs20.rest.dto;

import ch.uzh.ifi.seal.soprafs20.constant.CardStatus;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStat {
    private Long id;
    private int score;
    private Map<Long,Integer> scorePlayerWise = new HashMap<>();
    private int wordsGuessedCorrect;
    private int wordsGuessedWrong;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Map<Long, Integer> getScorePlayerWise() {
        return scorePlayerWise;
    }

    public void setScorePlayerWise(Map<Long, Integer> scorePlayerWise) {
        this.scorePlayerWise = scorePlayerWise;
    }

    public int getWordsGuessedCorrect() {
        return wordsGuessedCorrect;
    }

    public void setWordsGuessedCorrect(int wordsGuessedCorrect) {
        this.wordsGuessedCorrect = wordsGuessedCorrect;
    }

    public int getWordsGuessedWrong() {
        return wordsGuessedWrong;
    }

    public void setWordsGuessedWrong(int wordsGuessedWrong) {
        this.wordsGuessedWrong = wordsGuessedWrong;
    }
}
