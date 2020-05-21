package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GameGetDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.GamePostDTO;
import ch.uzh.ifi.seal.soprafs20.service.GameService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


/**
 * GameControllerTest
 * This is a WebMvcTest which allows to test the GameController i.e. GET/POST request without actually sending them over the network.
 * This tests if the GameController works.
 */
@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void createGame() throws Exception {
        GamePostDTO gamePostDTO = new GamePostDTO();

        MockHttpServletRequestBuilder postRequest = post("/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePostDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        Mockito.when(gameService.createGame(Mockito.any())).thenReturn(0l);

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(header().string("location", is("http://localhost/game/0")));
    }

    @Test
    public void getGameInfo() throws Exception {
        GameGetDTO gameGetDTO = new GameGetDTO();

        MockHttpServletRequestBuilder getRequest = get("/game/1")
            .contentType(MediaType.APPLICATION_JSON)
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void chooseWord() throws Exception {
        GamePutDTO gamePutDTO = new GamePutDTO();
        MockHttpServletRequestBuilder putRequest = put("/game/1/number")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void rejectWord() throws Exception {
        MockHttpServletRequestBuilder deleteRequest = delete("/game/1/number")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(deleteRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void clue() throws Exception {
        GamePutDTO gamePutDTO = new GamePutDTO();
        MockHttpServletRequestBuilder putRequest = put("/game/1/clue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void guess() throws Exception {
        GamePutDTO gamePutDTO = new GamePutDTO();
        MockHttpServletRequestBuilder putRequest = put("/game/1/guess")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(gamePutDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void wrapupGame() throws Exception {
        MockHttpServletRequestBuilder deleteRequest = delete("/game/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(1))
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(deleteRequest)
            .andExpect(status().isOk());
    }

    @Test
    public void getStats() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/game/stat/1")
            .header("X-Auth-Token", "supersecrettokenvalue");

        mockMvc.perform(getRequest)
            .andExpect(status().isOk());
    }

    @Test
    public void acceptWord() throws Exception {
        MockHttpServletRequestBuilder putRequest = put("/game/1/accept")
            .contentType(MediaType.APPLICATION_JSON)
            .content(asJsonString(1))
            .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
            .andExpect(status().isAccepted());
    }

    /**
     * Helper Method to convert gamePostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test Game", "gamename": "testGamename"}
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
