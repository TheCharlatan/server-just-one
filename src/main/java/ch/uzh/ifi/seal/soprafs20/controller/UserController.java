package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to the user.
 * The controller will receive the request and delegate the execution to the UserService and finally return the result.
 */
@RestController
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);

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
    public ResponseEntity registerUser(@RequestBody UserPostDTO userPostDTO) {
        User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User createdUser = userService.createUser(user);


        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .buildAndExpand("1")
                .toUri();
        return ResponseEntity.created(location).build();

    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@RequestHeader("X-Auth-Token") String token, @PathVariable Long userId){
        UserGetDTO userGetDTO = new UserGetDTO();
        User user = userService.getUser(userId);

        userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
        return  userGetDTO;

    }

    @GetMapping("/user/login")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public UserAuthDTO login(@RequestBody UserPostDTO userPostDTO){
        UserAuthDTO userAuthDTO = new UserAuthDTO("supersecrettokenvalue");
        return userAuthDTO;
    }

    @PutMapping("/user/{userId}/logout")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void logout(@RequestHeader("X-Auth-Token") String token,@PathVariable Long userId){
        return;
    }

    @PutMapping("/user/{userId}/edit")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserUpdateDTO updateUser(@RequestHeader("X-Auth-Token") String token, @RequestBody UserUpdateDTO user, @PathVariable Long userId){
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        return userUpdateDTO;
    }


    @PutMapping("/user/{userId}/invitation")
    @ResponseStatus(HttpStatus.OK)
    public void invitation(@RequestHeader("X-Auth-Token") String token, @PathVariable Long userId, @RequestBody UserPutDTO userPutDTO){
        return;

    }



}
