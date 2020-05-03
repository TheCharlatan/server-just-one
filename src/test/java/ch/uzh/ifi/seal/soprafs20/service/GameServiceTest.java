package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.CardStatus;
import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePutDTO;
import ch.uzh.ifi.seal.soprafs20.wordcheck.Stemmer;
import ch.uzh.ifi.seal.soprafs20.wordcheck.WordCheck;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

public class GameServiceTest {

    //@Mock
    //private UserRepository userRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private WordCheck wordChecker;
    @Mock
    private Stemmer stemmer;

    @InjectMocks
    private GameService gameService;

    private User testUser;
    private Game testGame;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        testUser = new User();
        testUser.setId(1L);

        testGame = new Game();
        testGame.setId(1L);
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(0L);
        playerIds.add(1L);
        playerIds.add(2L);
        testGame.setPlayerIds(playerIds);

        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        Mockito.when(gameRepository.save(Mockito.any())).thenReturn(testGame);
    }

    @Test
    public void createGame_validInputs_success() {
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(0L);
        playerIds.add(1L);
        playerIds.add(2L);

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        long id = gameService.createGame(playerIds);

        assertEquals(id, testGame.getId());
    }

    @Test
    public void createGame_playerNotFound() {
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(0L);
        playerIds.add(1L);
        playerIds.add(2L);

        assertThrows(NotFoundException.class,()->gameService.createGame(playerIds));
    }


    @Test
    public void createGame_NotEnoughPlayers_throwsException() {
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(0L);
        playerIds.add(1L);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        assertThrows(ServiceException.class, () -> gameService.createGame(playerIds));
    }

    @Test
    public void createGame_TooManyPlayers_throwsException() {
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(0L);
        playerIds.add(1L);
        playerIds.add(1L);
        playerIds.add(1L);
        playerIds.add(1L);
        playerIds.add(1L);
        playerIds.add(1L);
        playerIds.add(1L);
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        assertThrows(ServiceException.class, () -> gameService.createGame(playerIds));
    }

    @Test
    public void chooseWordTest() {

        ArrayList<String> sampleWordList = new ArrayList<>(Arrays.asList("TW0", "TW1", "TW2", "TW3", "TW4", "TW5", "TW6", "TW7",
                "TW8", "TW9"));
        testGame.setRound(2);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        //Mockito.when(gameRepository.getOne(anyLong())).thenReturn(testGame);
        int wordIndexChoosedByPlayerForRound2 = 2;
        gameService.chooseWord(testGame.getId(), wordIndexChoosedByPlayerForRound2);

        //In second round, word selected for 2nd position will be at index 2
        assertEquals(6, testGame.getWordIndex());
        assertEquals("TW6", sampleWordList.get(testGame.getWordIndex()));
    }

    @Test
    public void chooseWordExceptionForInvalidNo() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        assertThrows(ServiceException.class,()->gameService.chooseWord(testGame.getId(), 7));
    }

    @Test
    public void chooseWordExceptionForSameChoiceInCaseOfRejection() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.getLastWordIndex().add(1);
        assertThrows(ServiceException.class,()->gameService.chooseWord(testGame.getId(), 1));
    }

    @Test
    public void rejectWordTestTimeFailure() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.setTimestamp(java.time.LocalTime.now().minus(35, ChronoUnit.SECONDS));
        assertThrows(ServiceException.class, ()->gameService.rejectWord(1L));
    }

    @Test
    public void rejectWordTestMoreThanThreeTimes() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.getLastWordIndex().add(1);
        testGame.getLastWordIndex().add(2);
        testGame.getLastWordIndex().add(3);
        testGame.getLastWordIndex().add(4);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        assertThrows(ServiceException.class, ()->gameService.rejectWord(1L));
    }

    @Test
    public void rejectWord_wrongStateThrowsException() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.getLastWordIndex().add(1);
        testGame.getLastWordIndex().add(2);
        testGame.getLastWordIndex().add(3);
        testGame.setGameStatus(GameStatus.AWAITING_GUESS);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        assertThrows(ServiceException.class, ()->gameService.rejectWord(1L));
    }

    @Test
    public void rejectWordSuccess() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.setGameStatus(GameStatus.AWAITING_CLUES);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        gameService.rejectWord(1L);

        assertEquals(GameStatus.AWAITING_INDEX, testGame.getGameStatus());
        assertEquals(CardStatus.USER_REJECTED_WORD, testGame.getCardStatus());
    }

    @Test
    public void getExistingGame_success() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Game game = gameService.getExistingGame(1L);

        assertEquals(game, testGame);
    }

    @Test
    public void getExistingGame_notFoundException() {
        assertThrows(NotFoundException.class, ()->gameService.getExistingGame(1L));
    }

    @Test
    public void getExstingUser_sucess() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        User user = gameService.getExistingUser(1L);
        assertEquals(user, testUser);
    }

    @Test
    public void getExstingUser_notFoundException() {
        assertThrows(NotFoundException.class, ()->gameService.getExistingUser(1L));
    }

    @Test
    public void checkGuess_successfulGuess() {
        GamePutDTO gamePutDTO = new GamePutDTO();
        ArrayList<String> words = new ArrayList<>();
        words.add("Alcatraz");
        words.add("Smoke");
        words.add("Hazelnut");
        words.add("Diamond");
        words.add("Rose");
        testGame.setWords(words);
        testGame.setTimestamp(java.time.LocalTime.now().minus(30, ChronoUnit.SECONDS));
        testGame.setWordIndex(1);
        testGame.setCardStackCount(2);
        testGame.setWordsGuessedCorrect(3);
        testGame.setCardGuessedCount(4);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        gamePutDTO.setGuess(testGame.getWords().get(testGame.getWordIndex()));
        gamePutDTO.setWordIndex(testGame.getWordIndex());

        int startWordsGuessedCorrect = testGame.getWordsGuessedCorrect();
        int startCardGuessedCount = testGame.getCardGuessedCount();
        int startCardStackCount = testGame.getCardStackCount();

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

        GamePutDTO gamePutDTOTest = gameService.checkGuess(gamePutDTO, testGame.getId());

        assertEquals(startWordsGuessedCorrect + 1, testGame.getWordsGuessedCorrect());
        assertEquals(startCardGuessedCount + 1, testGame.getCardGuessedCount());
        assertEquals(startCardStackCount - 1, testGame.getCardStackCount());
        assertEquals("correct", gamePutDTOTest.getGuessCorrect());
    }

    @Test
    public void checkGuess_wrongGuess() {
        GamePutDTO gamePutDTO = new GamePutDTO();
        ArrayList<String> words = new ArrayList<>();
        words.add("Alcatraz");
        words.add("Smoke");
        words.add("Hazelnut");
        words.add("Diamond");
        words.add("Rose");
        testGame.setWords(words);
        testGame.setWordIndex(1);
        testGame.setCardStackCount(2);
        testGame.setWordsGuessedWrong(3);
        testGame.setCardGuessedCount(4);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));

        //Put a wrong guess
        gamePutDTO.setGuess(testGame.getWords().get(testGame.getWordIndex() + 1));
        gamePutDTO.setWordIndex(testGame.getWordIndex());

        int startWordsGuessedWrong = testGame.getWordsGuessedWrong();
        int startCardGuessedCount = testGame.getCardGuessedCount();
        int startCardStackCount = testGame.getCardStackCount();

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));

        GamePutDTO gamePutDTOTest = gameService.checkGuess(gamePutDTO, testGame.getId());

        assertEquals(startWordsGuessedWrong + 1, testGame.getWordsGuessedWrong());
        assertEquals(startCardGuessedCount + 1, testGame.getCardGuessedCount());
        assertEquals(startCardStackCount - 2, testGame.getCardStackCount());
        assertEquals("wrong", gamePutDTOTest.getGuessCorrect());
    }

    @Test
    public void checkGuess_skipGuess() {
        GamePutDTO gamePutDTO = new GamePutDTO();
        ArrayList<String> words = new ArrayList<>();
        words.add("Alcatraz");
        words.add("Smoke");
        words.add("Hazelnut");
        words.add("Diamond");
        words.add("Rose");
        testGame.setWords(words);
        testGame.setWordIndex(1);
        testGame.setCardStackCount(2);
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        //Put a skip guess
        gamePutDTO.setGuess("SKIP");
        gamePutDTO.setWordIndex(testGame.getWordIndex());

        int startCardStackCount = testGame.getCardStackCount();

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));

        GamePutDTO gamePutDTOTest = gameService.checkGuess(gamePutDTO, testGame.getId());

        assertEquals(startCardStackCount - 1, testGame.getCardStackCount());
        assertEquals("skip", gamePutDTOTest.getGuessCorrect());
    }

    @Test
    public void checkGuess_timeoutGuess() {
        ArrayList<String> words = new ArrayList<>();
        words.add("Alcatraz");
        words.add("Smoke");
        words.add("Hazelnut");
        words.add("Diamond");
        words.add("Rose");
        testGame.setWords(words);
        testGame.setWordIndex(1);
        testGame.setCardStackCount(2);
        testGame.setWordsGuessedWrong(3);
        testGame.setCardGuessedCount(4);
        testGame.setTimestamp(java.time.LocalTime.now().minus(31, ChronoUnit.SECONDS));
        GamePutDTO gamePutDTO = new GamePutDTO();
        gamePutDTO.setGuess("test");
        gamePutDTO.setWordIndex(testGame.getWordIndex());

        int startWordsGuessedWrong = testGame.getWordsGuessedWrong();
        int startCardGuessedCount = testGame.getCardGuessedCount();
        int startCardStackCount = testGame.getCardStackCount();

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));

        GamePutDTO gamePutDTOTest = gameService.checkGuess(gamePutDTO, testGame.getId());

        assertEquals(startWordsGuessedWrong + 1, testGame.getWordsGuessedWrong());
        assertEquals(startCardGuessedCount + 1, testGame.getCardGuessedCount());
        assertEquals(startCardStackCount - 2, testGame.getCardStackCount());
        assertEquals("timeout", gamePutDTOTest.getGuessCorrect());
    }

    @Test
    public void wrapup_playerLeavesGame () {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn((Optional.of(testGame)));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        long playerIdLeft = testGame.getPlayerIds().get(1);
        gameService.wrapup(testGame.getId(), playerIdLeft);

        assertNotSame(playerIdLeft, testGame.getPlayerIds().get(1));
    }

    @Test
    public void wrapup_lastPlayerLeavesGame () {
        Game game = new Game();
        game.setId(2L);
        ArrayList<Long> playerIds = new ArrayList<Long>();
        playerIds.add(1L);
        game.setPlayerIds(playerIds);
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn((Optional.of(game)));
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
        long playerIdLeft = game.getPlayerIds().get(0);
        gameService.wrapup(game.getId(), playerIdLeft);

        assertFalse(gameRepository.existsById(game.getId()));
    }

    @Test
    public void clueAccepted() {
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Mockito.when(wordChecker.checkEnglishWord(Mockito.any())).thenReturn(true);
        Mockito.when(stemmer.checkStemMatch(Mockito.any(),Mockito.any())).thenReturn(true);
        testGame.setWordIndex(0);
        testGame.setWords(Arrays.asList("break","making","split","test","word"));
        gameService.submitWord(1L,"word");
        assert(testGame.getClues().size() >= 1);
        for (String clue: testGame.getClues()) {
            assertEquals("word", clue);
        }
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Mockito.when(wordChecker.checkEnglishWord(Mockito.any())).thenReturn(true);
        gameService.submitWord(1L,"word");
        assert(testGame.getClues().size() >= 2);
        for (String clue: testGame.getClues()) {
            assertEquals("word", clue);
        }
    }


    @Test
    public void timeForSubmitClueException(){
        testGame.setTimestamp(java.time.LocalTime.now().minus(40, ChronoUnit.SECONDS));
        ArrayList<String> clues =  new ArrayList<>();
        clues.add("REJECTED");
        clues.add("REJECTED");
        clues.add("REJECTED");
        testGame.setClues(clues);
        testGame.setWords(Arrays.asList("GameWord","GameWord2","TestGame"));
        testGame.setWordIndex(0);

        ArrayList<Long> playerIdList = new ArrayList<>();
        playerIdList.add(1L);
        playerIdList.add(2L);
        playerIdList.add(3L);
        playerIdList.add(4L);
        playerIdList.add(5L);
        testGame.setPlayerIds(playerIdList);

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));

        gameService.submitWord(1L,"word");
        assertEquals(CardStatus.NO_VALID_CLUE_ENTERED, testGame.getCardStatus());

    }

    @Test
    public void englishWordCheckInvalid(){
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        ArrayList<String> clues =  new ArrayList<>();
        clues.add("REJECTED");
        clues.add("REJECTED");
        clues.add("REJECTED");
        testGame.setClues(clues);
        ArrayList<Long> playerIdList = new ArrayList<>();
        playerIdList.add(1L);
        playerIdList.add(2L);
        playerIdList.add(3L);
        playerIdList.add(4L);
        playerIdList.add(5L);
        testGame.setPlayerIds(playerIdList);

        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));

        gameService.submitWord(1L,"asfdj");
        assertEquals(CardStatus.NO_VALID_CLUE_ENTERED, testGame.getCardStatus());
    }

    @Test
    public void stemCheckInvalid(){
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        testGame.setTimestamp(java.time.LocalTime.now().minus(15, ChronoUnit.SECONDS));
        testGame.setWords(Arrays.asList("break","making","split","test","word"));
        testGame.setWordIndex(0);
        ArrayList<String> clues =  new ArrayList<>();
        clues.add("REJECTED");
        clues.add("REJECTED");
        clues.add("REJECTED");
        testGame.setClues(clues);
        ArrayList<Long> playerIdList = new ArrayList<>();
        playerIdList.add(1L);
        playerIdList.add(2L);
        playerIdList.add(3L);
        playerIdList.add(4L);
        playerIdList.add(5L);
        testGame.setPlayerIds(playerIdList);

        gameService.submitWord(1L,"breaking");
        assertEquals(CardStatus.NO_VALID_CLUE_ENTERED, testGame.getCardStatus());
    }

    @Test
    public void getAllWordsFromList_Full() {
        ArrayList<String> words = gameService.getAllWordsFromWordList();
        for (String word: words) {
            assert (!word.equals(""));
        }
        assert(words.get(0).equals("Alcatraz"));
        assert(words.get(274).equals("Book"));
    }


    @Test
    public void selectGameWords() {
        ArrayList<String> words = gameService.selectGameWords();
        assert(words.size() == 5*13);
        for (String word: words) {
            assert (!word.equals(""));
        }
    }
}
