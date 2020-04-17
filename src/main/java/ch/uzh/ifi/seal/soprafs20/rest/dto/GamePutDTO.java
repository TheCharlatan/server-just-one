package ch.uzh.ifi.seal.soprafs20.rest.dto;

public class GamePutDTO {

    private int wordIndex;
    private String clue;
    private String guess;
    private String guessCorrect;

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

    public String getGuessCorrect() {return guessCorrect;}

    public void setGuessCorrect(String guessCorrect) {this.guessCorrect = guessCorrect; }
}
