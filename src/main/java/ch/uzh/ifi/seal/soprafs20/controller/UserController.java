package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getAllUsers() {
        // fetch all users in the internal representation
        List<User> users = userService.getUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        return userGetDTOs;
    }

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<String> registerUser(@RequestBody UserPostDTO userPostDTO) {
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User createdUser = userService.createUser(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", createdUser.getId()))
                .toUri();
        return ResponseEntity.created(location).build();

    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@RequestHeader("X-Auth-Token") String token, @PathVariable Long userId){
        User user = userService.getUser(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @GetMapping("/user/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public UserAuthDTO login(@RequestHeader("Authorization") String authorizationHeader) {
        String password;
        String username;
        try {
            String base64Encoded = authorizationHeader.split(" ")[1];
            byte[] asBytes = Base64.getDecoder().decode(base64Encoded);
            String base64Decoded = new String(asBytes, StandardCharsets.UTF_16LE);
            String[] authStrings = base64Decoded.split(":");
            username = authStrings[0];
            password = authStrings[1];
        }
        catch(Exception e) {
            throw new AuthenticationException("Unable to decode username and password");
        }
        // now call the user service with authStrings[0] and authStrings[1]
        User user = userService.login(username, password);
        Long id = user.getId();
        return new UserAuthDTO(user.getToken(), id);
    }

    @PutMapping("/user/{userId}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logout(@RequestHeader("X-Auth-Token") String token,@PathVariable Long userId){
    }

    @PutMapping("/user/{userId}/edit")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserUpdateDTO updateUser(@RequestHeader("X-Auth-Token") String token, @RequestBody UserUpdateDTO user, @PathVariable Long userId){
        return new UserUpdateDTO();
    }

    @PutMapping("/user/{userId}/invitation")
    @ResponseStatus(HttpStatus.OK)
    public void invitation(@RequestHeader("X-Auth-Token") String token, @PathVariable Long userId, @RequestBody UserPutDTO userPutDTO){
        userService.invite(userId, userPutDTO.getInvitation());
    }
}
