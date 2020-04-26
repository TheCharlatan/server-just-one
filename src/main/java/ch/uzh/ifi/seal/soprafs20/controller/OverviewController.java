package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Overview Controller
 * This class is responsible for handling all REST request that are related to the game's main page / overview.
 * The controller will receive the request and delegate the execution to the OverviewService and finally return the result.
 */
@RestController
public class OverviewController {

    OverviewController() {
    }

    @GetMapping("/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChatMessageDTO> getChatMessages(@RequestHeader("X-Auth-Token") String token) {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        ArrayList<ChatMessageDTO> chatHistory = new ArrayList<ChatMessageDTO>();
        chatHistory.add(chatMessageDTO);
        return chatHistory;
    }

    @PostMapping("/chat")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public void addChatMessage(@RequestHeader("X-Auth-Token") String token, @RequestBody ChatMessageDTO chatMessageDTO) {
    }
}
