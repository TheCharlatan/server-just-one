package ch.uzh.ifi.seal.soprafs20.rest.dto;

import java.util.ArrayList;

public class GamePutDTO {

    private int wordIndex;
    private String clue;
    private String guess;

    public int getWordIndex() {
        return wordIndex;
    }

    public void setWordIndex(int wordIndex) {
        this.wordIndex = wordIndex;
    }

    public String getClue() {
        return clue;
    }

    public void setClue(String clue) {
        this.clue = clue;
    }

    public String getGuess() {
        return guess;
    }

    public void setGuess(String guess) {
        this.guess = guess;
    }
}
