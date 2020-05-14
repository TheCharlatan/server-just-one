package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;


    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testName");
        testUser.setUsername("testUsername");
        testUser.setPassword("testPassword");
        testUser.setToken("supersecrettokenvalue");

        // when -> any object is being save in the userRepository -> return the dummy testUser
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
    }

    @Test
    public void getUsers_success() {
        ArrayList<User> userList = new ArrayList<>();
        userList.add(testUser);
        Mockito.when(userRepository.findAll()).thenReturn(userList);
        List<User> users =  userService.getUsers();
        assertEquals(userList, users);
    }

    @Test
    public void getUser_success() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        User user =  userService.getUser(1l);
        assertEquals(testUser, user);
    }

    @Test
    public void createUser_validInputs_success() {
        // when -> any object is being save in the userRepository -> return the dummy testUser
        User createdUser = userService.createUser(testUser);

        // then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

        assertEquals(testUser.getId(), createdUser.getId());
        assertEquals(testUser.getName(), createdUser.getName());
        assertEquals(testUser.getUsername(), createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateName_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void createUser_duplicateInputs_throwsException() {
        // given -> a first user has already been created
        userService.createUser(testUser);

        // when -> setup additional mocks for UserRepository
        Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

        // then -> attempt to create second user with same user -> check that an error is thrown
        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
    }

    @Test
    public void login_success() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        User user = userService.login("testUsername", "testPassword");

        assertEquals("supersecrettokenvalue", user.getToken());
    }

    @Test
    public void login_invalid_username() {
        assertThrows(AuthenticationException.class, () -> userService.login("invalidUsername", "testPassword"));
    }

    @Test
    public void login_invalid_password() {
        Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);
        assertThrows(AuthenticationException.class, () -> userService.login("testUsername", "invalidPassword"));
    }

    @Test

    public void logout_success() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        userService.logout(1L);

        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
    }

    @Test
    public void authenticate() {
        Mockito.when(userRepository.findByToken(Mockito.any())).thenReturn(null);
        assertThrows(AuthenticationException.class, () -> userService.authenticate("testUsern"));
    }

    @Test
    public void invite() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        List<Long> invitations = testUser.getInvitations();
        invitations.add(1l);

        userService.invite(1l, 1l);
        assertEquals(testUser.getInvitations(), invitations);
    }

    @Test
    public void updateUser() {
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.of(testUser));
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setName("updateName");
        userUpdateDTO.setUsername("updateUsername");
        userUpdateDTO.setCountry("updateCountry");
        userUpdateDTO.setGender('f');
        Date date = new Date();
        userUpdateDTO.setBirthDay(date);

        userService.updateUser(1L, userUpdateDTO);

        assertEquals(testUser.getUsername(), userUpdateDTO.getUsername());
        assertEquals(testUser.getName(), userUpdateDTO.getName());
        assertEquals(testUser.getCountry(), userUpdateDTO.getCountry());
        assertEquals(testUser.getGender(), userUpdateDTO.getGender());
        assertEquals(testUser.getBirthDay(), userUpdateDTO.getBirthDay());


    }


}
