package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.LobbyException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;

public class LobbyServiceTest {


    @Mock
    LobbyRepository lobbyRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    LobbyService lobbyService;

    private User testUser;
    private Lobby lobbyTest;

    @BeforeEach
    public void setupLobby(){
        MockitoAnnotations.initMocks(this);

        lobbyTest = new Lobby();
        lobbyTest.setId(1l);
        lobbyTest.setName("testLobby");
        lobbyTest.setHostPlayerId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);

        Mockito.when(lobbyRepository.save(Mockito.any())).thenReturn(lobbyTest);

    }

    @Test
    public void createdLobby_validInputs_success(){


        //Mockito.verify(lobbyRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(lobbyTest));
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        long createdLobby = lobbyService.createLobby(lobbyTest);
        assertEquals(createdLobby,lobbyTest.getId());
    }

    @Test
    public void createdLobbyExist_Exception(){
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.findByName(Mockito.any())).thenReturn(lobbyTest);

        assertThrows(LobbyException.class, ()->lobbyService.createLobby(lobbyTest));
    }

    @Test
    public void addUserToLobbyWhenGameGoingOn(){

        lobbyTest.setStatus(1);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(lobbyTest));
        lobbyService.createLobby(lobbyTest);
        assertThrows(LobbyException.class,()->lobbyService.addPlayerToLobby(1L,1L));


    }

    @Test
    public void addUserToLobby(){

        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);

        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);



        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        lobbyService.addPlayerToLobby(1L,1L);
        assertEquals(lobbyTest.getPlayerIds().size(),7);

    }

    @Test
    public void addExistingUserToLobby(){


        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{1L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        assertThrows(LobbyException.class,()->lobbyService.addPlayerToLobby(1L,1L));


    }

    @Test
    public void addMoreThanSevenPlayerToLobby(){

        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{1L,2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        assertThrows(LobbyException.class,()->lobbyService.addPlayerToLobby(1L,8L));

    }

    @Test
    public void removePlayerFromLobby(){

        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{1L,2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        lobbyService.removePlayerFromLobby(1l,1l);
        assertEquals(lobbyTest.getPlayerIds().contains(1),false);

    }

    @Test
    public void notAvailablePlayerFromLobby(){

        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{1L,2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);

        assertThrows(LobbyException.class,()->lobbyService.removePlayerFromLobby(1L,8L));
    }


}
