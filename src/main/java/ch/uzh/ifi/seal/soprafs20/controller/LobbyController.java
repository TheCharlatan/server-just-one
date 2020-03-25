package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;

/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to an existing lobby.
 * The controller will receive the request and delegate the execution to the LobbyService and finally return the result.
 */
@RestController
public class LobbyController {

    LobbyController() {
    }

    @PostMapping("/lobby")
    public ResponseEntity createLobby(@RequestHeader("X-Auth-Token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {
        // FIXME expand to the created lobby's id
        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .buildAndExpand("1")
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/lobby")
    public ArrayList<LobbyGetDTO> getLobbies(@RequestHeader("X-Auth-Token") String token) {
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();
        ArrayList<LobbyGetDTO> lobbies = new ArrayList<LobbyGetDTO>();
        lobbies.add(lobbyGetDTO);
        return lobbies;
    }

    @GetMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyById(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        LobbyGetDTO lobbyInfoDTO = new LobbyGetDTO();
        return lobbyInfoDTO;
    }

    @PutMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
     public void join(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        return;
    }

    @DeleteMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removePlayer(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        return;
    }

    @GetMapping("/lobby/{id}/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ArrayList<ChatMessageDTO> getChatMessages(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        ArrayList<ChatMessageDTO> chatHistory = new ArrayList<ChatMessageDTO>();
        chatHistory.add(chatMessageDTO);
        return chatHistory;
    }

    @PutMapping("/lobby/{id}/chat")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody ChatMessageDTO chatMessageDTO) {
        return;
    }
}
