package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

/**
 * Overview Controller
 * This class is responsible for handling all REST request that are related to the game's main page / overview.
 * The controller will receive the request and delegate the execution to the OverviewService and finally return the result.
 */
@RestController
public class OverviewController {

    private ChatService chatService;
    OverviewController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChatMessageDTO> getChatMessages(@RequestHeader("X-Auth-Token") String token, @PathVariable Long chatId) {
        return chatService.getChatMessages(chatId);
    }

    @PostMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(
            @PathVariable Long chatId,
            @RequestHeader("X-Auth-Token") String token,
            @RequestBody ChatMessageDTO chatMessageDTO) {
        chatService.addChatMessage(chatId, chatMessageDTO);
    }

    @PostMapping("/chatpoll/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public void subscribe(@PathVariable Long chatId){
        chatService.subscribe(chatId);
    }

    @DeleteMapping("/chatpoll/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public void unsubscribe(@PathVariable Long chatId){
        chatService.unsubscribe(chatId);
    }

    @GetMapping("/chatpoll/{chatId}")
    @ResponseStatus(HttpStatus.OK)
    public DeferredResult<List<ChatMessageDTO>> poll(@PathVariable Long chatId){
        // create deferred result that times out after 60 seconds
        final DeferredResult<List<ChatMessageDTO>> finalResult  = new DeferredResult<List<ChatMessageDTO>>(10000l);
        chatService.pollGetUpdate(finalResult, chatId);
        return finalResult;
    }
}
