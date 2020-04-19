package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.net.URI;


/**
 * Game Controller
 * This class is responsible for handling all REST request that are related to an existing game.
 * The controller will receive the request and delegate the execution to the LoginService and finally return the result.
 */
@RestController
public class GameController {

    private GameService gameService;

    GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/game")
    public ResponseEntity createGame(@RequestHeader("X-Auth-Token") String token, @RequestBody GamePostDTO gamePostDTO) {
        long gameId = gameService.createGame(gamePostDTO.getPlayerIds());

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .buildAndExpand(String.format("%d", gameId))
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/game/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO getGameInfo(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        Game game = gameService.getExistingGame(id);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }

    @PutMapping("/game/{id}/number")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void chooseWord(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long id,
            @RequestBody GamePutDTO gamePutDTO) {
        gameService.chooseWord(id, gamePutDTO.getWordIndex());
        return;
    }

    @DeleteMapping("/game/{id}/number")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void rejectWord(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        gameService.rejectWord(id);
    }

    @PutMapping("/game/{id}/clue")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void clue(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long id,
            @RequestBody GamePutDTO gamePutDTO) {
        gameService.submitWord(id, gamePutDTO.getClue());
        return;
    }

    @PutMapping("/game/{id}/guess")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GamePutDTO guess(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long id,
            @RequestBody GamePutDTO gamePutDTO) {

        return this.gameService.checkGuess(gamePutDTO, id);

    }

    @DeleteMapping("/game/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void wrapup(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id, @RequestBody long playerId) {
        gameService.wrapup(id, playerId);
    }
}
