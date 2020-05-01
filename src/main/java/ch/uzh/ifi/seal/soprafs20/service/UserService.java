package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.hibernate.boot.model.relational.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User getUser(long id){
        return getExistingUser(id);
    }

    public User createUser(User newUser) {

        checkIfUserExists(newUser);

        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.OFFLINE);
        newUser.setCreationDate(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));


        // saves the given entity but data is only persisted in the database once flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    public User login(String username, String password) {
        User userByUsername = userRepository.findByUsername(username);
        if (userByUsername == null) {
            throw new AuthenticationException("Invalid login credentials, make sure that username and password are correct.");
        }
        if (!userByUsername.getPassword().equals(password)) {
            throw new AuthenticationException("Invalid login credentials, make sure that username and password are correct.");
        }
        return userByUsername;
    }

    public void authenticate(String token) {
        User authUser = userRepository.findByToken(token);
        if (authUser == null) {
            throw new AuthenticationException("Invalid token, user is not authenticated");
        }
    }

    public void invite(long userId, long lobbyId) {
        User invitedUser = getExistingUser(userId);
        List<Long> invitations = invitedUser.getInvitations();
        invitations.add(lobbyId);
        invitedUser.setInvitations(invitations);
        userRepository.save(invitedUser);
        userRepository.flush();

        log.debug("Add invitation to lobby {} for User: {}", userId, lobbyId);
    }

    private User getExistingUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException(String.format("Could not find user with id %d.", id));
        }
        return optionalUser.get();
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the username and the name
     * defined in the User entity. The method will do nothing if the input is unique and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());

        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(baseErrorMessage, "username and the name", "are"));
        }
    }

    public List<User> getUserScoreBoard(){

        List<User> userList = this.userRepository.findAll();
        userList.sort(Comparator.comparing(User::getScore).reversed());
        return userList;
    }
}
