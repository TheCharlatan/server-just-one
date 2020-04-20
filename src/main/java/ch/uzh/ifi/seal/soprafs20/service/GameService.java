package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

/**
 * Game Service
 * This class is the "worker" and responsible for all functionality related to the game
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class GameService {

    private Logger log = LoggerFactory.getLogger(GameService.class);

    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository, @Qualifier("userRepository") UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    public Long createGame(List<Long> players) {
        if (players.size() < 3) {
            throw new ServiceException("Need more players to start a game");
        }
        if (players.size() > 7) {
            throw new ServiceException("Can only have a maximum of 7 players to start this game");
        }
        // Create the game with the provided player id's, populate the game word list, initialize score and round
        Game newGame = new Game();
        newGame.setPlayerIds(players);
        newGame.setGameStatus(GameStatus.AWAITING_INDEX);
        newGame.setRound(1);
        newGame.setScore(0);
        newGame.setActivePlayer(newGame.getPlayerIds().get(0));
        newGame.setWords(selectGameWords());

        // save the game to the database
        newGame = gameRepository.save(newGame);
        Long gameId = newGame.getId();
        gameRepository.flush();

        // add the game id to the players and remove them from the lobby
        List<Long> playerIds = newGame.getPlayerIds();
        for (Long playerId: playerIds) {
            User user = userRepository.findById(playerId)
                .orElseThrow(
                    () -> new NotFoundException(String.format("A user with the id %d was not found", playerId))
                );
            user.setGameId(gameId);
            user.setLobbyId(0);
            userRepository.save(user);
            userRepository.flush();
        }

        log.debug("Created Information for Game: {}", newGame);
        return gameId;
    }

    private ArrayList<String> selectGameWords() {
        // select 5 * 13 random unique words from the english word list
        // 5 * 13 from 5 words every round for 13 rounds
        ArrayList<String> gameWords = new ArrayList<String>();
        ArrayList<String> allWords = getAllWordsFromWordList();
        Random rand = new Random();
        for (int i =0; i < 5*13; i++) {
            int randomIndex = rand.nextInt(allWords.size());
            gameWords.add(allWords.get(randomIndex));
            allWords.remove(randomIndex);
        }
        return gameWords;
    }

    private ArrayList<String> getAllWordsFromWordList() {
        // Read in the entire word list and return it as a ArrayList of strings.
        String filename = "cards-EN.txt";
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(filename);
        if (resource == null) {
            throw new IllegalArgumentException("The cards word list file was not found!");
        }
        BufferedReader reader;
        ArrayList<String> words = new ArrayList<String>();
        try {
            reader = new BufferedReader(new FileReader(
                        resource.getFile()));
            String line = reader.readLine();
            while (line != null) {
                if (!line.equals("")) {
                    words.add(line);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            throw new ServiceException("Error while reading word list");
        }
        return words;
    }
    // checks if the mysteryWord matches with the guess
    public GamePutDTO checkGuess(GamePutDTO gamePutDTO, long id) {
        int index = gamePutDTO.getWordIndex();
        String guess = gamePutDTO.getGuess();
        Game game = this.gameRepository.findById(id).get();
        String mysteryWord = game.getWords().get(index);

        //Skipped Guess
        if (guess.equals("SKIP")) {
            gamePutDTO.setGuessCorrect("skip");
            //handle according to a skipped guess -> the card is put away
        }

        //Successful Guess
        if (mysteryWord.equals(guess)) {
            gamePutDTO.setGuessCorrect("correct");
            //set the guesses and card numbers according to a correct guess
            game.setWordsGuessedCorrect(game.getWordsGuessedCorrect() + 1);
            game.setCardGuessedCount(game.getCardGuessedCount() + 1);
            game.setCardStackCount(game.getCardGuessedCount() - 1);
        }
        // Wrong Guess
        else {
            gamePutDTO.setGuessCorrect("wrong");
            //Handle according to a wrong guess -> this card and the next card is put away

        }

        // call the function "roundEnd" to set all the information needed for a new round
        //  or wrap up the game if no cards are left on the stack
        gameRepository.save(game);
        gameRepository.flush();
        //roundEnd(game);
        return gamePutDTO;
    }

    private void roundEnd (Game game) {
        //update the score of the active player
        //if cardStackCount != 0 a new round is started
            //choose a new active player
            // provide new list of words
            //update game status
            //reset list of clues
        //else
            //set game status == "GAME OVER"
            //
    }

}
