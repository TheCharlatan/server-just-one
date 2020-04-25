package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.net.URI;
import java.util.List;


/**
 * Lobby Controller
 * This class is responsible for handling all REST request that are related to an existing lobby.
 * The controller will receive the request and delegate the execution to the LobbyService and finally return the result.
 */
@RestController
public class LobbyController {

    private final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/lobby")
    public ResponseEntity createLobby(@RequestHeader("X-Auth-Token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {
        // FIXME expand to the created lobby's id
        Lobby lobby  = DTOMapper.INSTANCE.convertLobbyPostDTOToEntity(lobbyPostDTO);

        long lobbyId = lobbyService.createLobby(lobby);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(String.format("%d", lobbyId))
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/lobby")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<LobbyGetDTO> getAllLobbies(@RequestHeader("X-Auth-Token") String token){
        return this.lobbyService.getAllLobbies();
    }

    @GetMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public LobbyGetDTO getLobbyInfo(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        Lobby lobby = lobbyService.getLobby(id);
        LobbyGetDTO lobbyInfoDTO = DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
        return lobbyInfoDTO;
    }

    @PutMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
     public void join(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody long userId) {
        lobbyService.addPlayerToLobby(id,userId);
    }

    @DeleteMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removePlayer(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody long userId) {
        lobbyService.removePlayerFromLobby(id,userId);
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

    @PostMapping("/lobby/{id}/chat")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody ChatMessageDTO chatMessageDTO) {
        return;
    }
}
