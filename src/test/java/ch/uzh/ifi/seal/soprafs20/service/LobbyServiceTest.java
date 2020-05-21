package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.LobbyException;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.awt.image.AreaAveragingScaleFilter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;

public class LobbyServiceTest {


    @Mock
    LobbyRepository lobbyRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ChatRepository chatRepository;

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
        testUser.setPassword("12345");
        testUser.setStatus(UserStatus.ONLINE);

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
    public void createLobbyInvalidUser_Exception(){
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.findByName(Mockito.any())).thenReturn(lobbyTest);

        assertThrows(LobbyException.class, ()->lobbyService.createLobby(lobbyTest));
    }

    @Test
    public void updateLobby(){

        Lobby lobbyUpdateTest = new Lobby();
        lobbyUpdateTest.setId(2l);
        lobbyUpdateTest.setName("testUpdateLobby");
        lobbyUpdateTest.setHostPlayerId(1L);

        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.of(lobbyUpdateTest));
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        long createdLobby = lobbyService.createLobby(lobbyTest);

        lobbyUpdateTest.setName("NewLobby");
        lobbyService.saveOrUpdate(lobbyUpdateTest);
        assertEquals("NewLobby",lobbyUpdateTest.getName());
    }

    @Test
    public void getLobby(){
        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(java.util.Optional.ofNullable(lobbyTest));

        Lobby lobby = lobbyService.getLobby(1L);
        assertEquals("testLobby",lobby.getName());
    }

    @Test
    public void getLobbyException(){
        Mockito.when(lobbyRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(lobbyTest));
        assertThrows(LobbyException.class,()->lobbyService.getLobby(3L));
    }

    @Test
    public void checkLobbyExistsException(){
        Mockito.when(lobbyRepository.findByName(Mockito.any())).thenReturn(lobbyTest);
        assertThrows(LobbyException.class,()->lobbyService.checkIfLobbyExist(lobbyTest));
    }

    @Test
    public void checkLobbyExists_Success(){
        Lobby lobbyTest1 = new Lobby();
        lobbyTest1.setId(1l);
        lobbyTest1.setName("LobbyTest");
        lobbyTest1.setHostPlayerId(1L);
        assertDoesNotThrow(()->lobbyService.checkIfLobbyExist(lobbyTest1));
    }

    @Test
    public void getAllLobbies(){
        Lobby testLobby1 = new Lobby();
        testLobby1.setId(2L);
        testLobby1.setName("testLobby1");
        testLobby1.setHostPlayerId(2L);

        Lobby testLobby2 = new Lobby();
        testLobby2.setId(3L);
        testLobby2.setName("testLobby2");
        testLobby2.setHostPlayerId(3L);

        List<Lobby> lobbyList = new ArrayList<>();
        lobbyList.add(testLobby1);
        lobbyList.add(testLobby2);
        lobbyList.add(lobbyTest);

        Mockito.when(lobbyRepository.findAll()).thenReturn(lobbyList);
        List<LobbyGetDTO> lobbyGetDTOList = lobbyService.getAllLobbies();

        assertEquals(3, lobbyGetDTOList.size());
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
    public void addNotExistingPlayerToLobby(){

        Mockito.when(lobbyRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(lobbyTest));
        Mockito.when(userRepository.findById(5L)).thenThrow(new LobbyException("Error"));

        assertThrows(LobbyException.class,()->lobbyService.addPlayerToLobby(1L,5L));
    }

    @Test
    public void addPlayerToLobby(){
        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);

        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(2L)).thenReturn(java.util.Optional.ofNullable(testUser));
        Mockito.when(lobbyRepository.findById(1L)).thenReturn(Optional.ofNullable(lobbyTest));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        lobbyService.addPlayerToLobby(1L,2L);
        assertEquals(7, lobbyTest.getPlayerIds().size());
    }

    @Test
    public void addExistingUserToLobby(){
        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{1L, 2L,3L,4L,5L,6L,7L};
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
        Long[] longList = new Long[]{2L,3L,4L,5L,6L,7L};
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
        Long[] longList = new Long[]{2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setPassword("testUserName");

        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(testUser2));
        Mockito.when(lobbyRepository.findById(1L)).thenReturn(Optional.ofNullable(lobbyTest));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);

        lobbyService.removePlayerFromLobby(1l,1l,false);
        assertFalse(lobbyTest.getPlayerIds().contains(1));
        assertEquals(-1, testUser.getLobbyId());

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

        assertThrows(LobbyException.class,()->lobbyService.removePlayerFromLobby(1L,8L,false));
    }

    @Test
    public void removePlayerFromLobbyBrowserCloses(){
        List<Long> playerList  = new ArrayList<>();
        Long[] longList = new Long[]{2L,3L,4L,5L,6L,7L};
        Collections.addAll(playerList,longList);
        lobbyTest.setPlayerIds(playerList);
        Mockito.when(userRepository.getOne(Mockito.any())).thenReturn(testUser);
        lobbyService.createLobby(lobbyTest);

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setName("testName2");
        testUser2.setUsername("testUsername2");
        testUser2.setStatus(UserStatus.ONLINE);
        testUser2.setPassword("testUserName");


        Mockito.when(lobbyRepository.getOne(anyLong())).thenReturn(lobbyTest);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(testUser));
        Mockito.when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(testUser2));
        Mockito.when(lobbyRepository.findById(1L)).thenReturn(Optional.ofNullable(lobbyTest));
        Mockito.when(lobbyRepository.save(Mockito.any(Lobby.class))).thenReturn(lobbyTest);
        lobbyService.removePlayerFromLobby(1l,1l,true);
        assertEquals(false, lobbyTest.getPlayerIds().contains(1));
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        assertNotEquals(1l,lobbyTest.getHostPlayerId());
    }

    @Test
    public void lobbyEntity_toString_success() {
        assertEquals("Lobby{id=1, name='testLobby', hostPlayerId=1, playerIds=[], gameId=null, status=0}", lobbyTest.toString());
    }

}
