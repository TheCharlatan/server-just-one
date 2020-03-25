package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * LobbyControllerTest
 * This is a WebMvcTest which allows to test the LobbyController i.e. GET/POST request without actually sending them over the network.
 * This tests if the LobbyController works.
 */
@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void createLobby() throws Exception {
        LobbyPostDTO lobbyPostDTO = new LobbyPostDTO();

        MockHttpServletRequestBuilder postRequest = post("/lobby")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(lobbyPostDTO))
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void getLobbies() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/lobby/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void getLobbyById() throws Exception {
        LobbyGetDTO lobbyGetDTO = new LobbyGetDTO();

        MockHttpServletRequestBuilder getRequest = get("/lobby/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void join() throws Exception {
        MockHttpServletRequestBuilder putRequest = put("/lobby/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void removePlayer() throws Exception {
        MockHttpServletRequestBuilder deleteRequest = delete("/lobby/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void addChatMessage() throws Exception {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        MockHttpServletRequestBuilder putRequest = put("/lobby/1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatMessageDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isCreated());
    }

    @Test
    public void getChatMessages() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/lobby/1/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }


    /**
     * Helper Method to convert lobbyPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test Lobby", "lobbyname": "testLobbyname"}
     * @param object
     * @return string
     */
    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("The request body could not be created.%s", e.toString()));
        }
    }
}
