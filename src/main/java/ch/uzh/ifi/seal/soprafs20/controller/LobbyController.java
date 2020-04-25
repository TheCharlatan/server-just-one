package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

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

    private final LobbyService lobbyService;

    LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @PostMapping("/lobby")
    public ResponseEntity<String> createLobby(@RequestHeader("X-Auth-Token") String token, @RequestBody LobbyPostDTO lobbyPostDTO) {
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
        return DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(lobby);
    }

    @GetMapping("/lobbypoll/{lobbyId}/subscribe")
    @ResponseStatus(HttpStatus.OK)
    public void subscribe(@PathVariable Long lobbyId){
        lobbyService.subscribe(lobbyId);
    }

    @GetMapping("/lobbypoll/{lobbyId}/unsubscribe")
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribe(@PathVariable Long lobbyId){
        lobbyService.unsubscribe(lobbyId);
    }

    @GetMapping("/lobbypoll/{lobbyId}")
    @ResponseStatus(HttpStatus.OK)
    DeferredResult<LobbyGetDTO> poll(@PathVariable Long lobbyId){
        // create deferred result that times out after 60 seconds
        final DeferredResult<LobbyGetDTO> finalResult  = new DeferredResult<LobbyGetDTO>(60000l);
        lobbyService.pollGetUpdate(finalResult, lobbyId);
        return finalResult;
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
    public List<ChatMessageDTO> getChatMessages(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        ArrayList<ChatMessageDTO> chatHistory = new ArrayList<>();
        chatHistory.add(chatMessageDTO);
        return chatHistory;
    }

    @PostMapping("/lobby/{id}/chat")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody ChatMessageDTO chatMessageDTO) {
    }
}
