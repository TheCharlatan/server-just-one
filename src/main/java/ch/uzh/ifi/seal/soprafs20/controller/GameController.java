package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.context.request.async.DeferredResult;

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
    public ResponseEntity<String> createGame(@RequestHeader("X-Auth-Token") String token, @RequestBody GamePostDTO gamePostDTO) {
        long gameId = gameService.createGame(gamePostDTO.getPlayerIds());

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
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

    @PostMapping("/gamepoll/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void subscribe(@PathVariable Long gameId){
        gameService.subscribe(gameId);
    }

    @DeleteMapping("/gamepoll/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribe(@PathVariable Long gameId){
        gameService.unsubscribe(gameId);
    }

    @GetMapping("/gamepoll/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    DeferredResult<GameGetDTO> poll(@PathVariable Long gameId){
        // create deferred result that times out after 60 seconds
        final DeferredResult<GameGetDTO> finalResult  = new DeferredResult<GameGetDTO>(60000l);
        gameService.pollGetUpdate(finalResult, gameId);
        return finalResult;
    }

    @PutMapping("/game/{id}/number")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void chooseWord(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long id,
            @RequestBody GamePutDTO gamePutDTO) {
        gameService.chooseWord(id, gamePutDTO.getWordIndex());
    }

    @DeleteMapping("/game/{id}/number")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void rejectWord(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id) {
        gameService.rejectWord(id);
    }

    @PutMapping("/game/{id}/accept")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @ResponseBody
    public void acceptWord(
            @RequestHeader("X-Auth-Token") String token,
            @PathVariable("id") long id,
            @RequestBody long userId) {
        gameService.acceptWord(id, userId);
    }

    @PutMapping("/game/{id}/clue")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public void clue(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id,
            @RequestBody GamePutDTO gamePutDTO) {

        gameService.submitWord(id,gamePutDTO.getClue());

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

    @GetMapping("game/stat/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameStat getStat(@RequestHeader("X-Auth-Token") String token, @PathVariable("id") long id){
        return this.gameService.getFinalStats(id);
    }

    @DeleteMapping("game/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public GameGetDTO removePlayerFromGame(@RequestHeader("X-Auth-Token") String token,
                                           @PathVariable("id") long gameId, @RequestBody GameDeleteDTO gameDeleteDTO){
        Game game = gameService.removePlayerFromGame(gameId,gameDeleteDTO);
        return DTOMapper.INSTANCE.convertEntityToGameGetDTO(game);
    }
}
