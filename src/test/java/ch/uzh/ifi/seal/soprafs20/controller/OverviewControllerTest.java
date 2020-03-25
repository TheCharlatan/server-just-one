package ch.uzh.ifi.seal.soprafs20.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * OverviewControllerTest
 * This is a WebMvcTest which allows to test the OverviewController i.e. GET/POST request without actually sending them over the network.
 * This tests if the OverviewController works.
 */
@WebMvcTest(OverviewController.class)
public class OverviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void addChatMessage() throws Exception {
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        MockHttpServletRequestBuilder postRequest = post("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(chatMessageDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated());
    }

    @Test
    public void getChatMessages() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest)
                .andExpect(status().isOk());
    }


    /**
     * Helper Method to convert overviewPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test Overview", "overviewname": "testOverviewname"}
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
