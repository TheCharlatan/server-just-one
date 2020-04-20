package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    //@Mock
    //private UserRepository userRepository;
    @Mock
    private GameRepository gameRepository;
    @Mock
    private UserRepository userRepository;

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
    public void getExistingGame_success() {
        Mockito.when(gameRepository.findById(Mockito.any())).thenReturn(Optional.of(testGame));
        Game game = gameService.getExistingGame(1L);

        assertEquals(game, testGame);
    }

    /* These are some tests for the private methods. They are commented sinc private methods cannot be
     * tested under normal circumstances and cannot be tested under normal circumstances. They are kept
     * here to still allow some quick implementation testing.
     */

    //@Test
    //public void getAllWordsFromList_Full() {
    //    ArrayList<String> words = gameService.getAllWordsFromWordList();
    //    for (String word: words) {
    //        assert (!word.equals(""));
    //    }
    //    assert(words.get(0).equals("Alcatraz"));
    //    assert(words.get(274).equals("Book"));
    //}

    //@Test
    //public void selectGameWords() {
    //    ArrayList<String> words = gameService.selectGameWords();
    //    assert(words.size() == 5*13);
    //    for (String word: words) {
    //        assert (!word.equals(""));
    //    }
    //}

}
