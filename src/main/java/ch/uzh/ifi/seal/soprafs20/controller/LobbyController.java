package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.LobbyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;

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

    @PutMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
     public void join(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody long userId) {
        lobbyService.addPlayerToLobby(id,userId);
    }

    @DeleteMapping("/lobby/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void removePlayer(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id,
                             @RequestBody long userId, @RequestParam Boolean browserClose) {
        lobbyService.removePlayerFromLobby(id,userId, browserClose);
    }
}
