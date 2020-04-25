package ch.uzh.ifi.seal.soprafs20.controller;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.rest.dto.LobbyPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPostDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserPutDTO;
import ch.uzh.ifi.seal.soprafs20.rest.dto.UserUpdateDTO;
import ch.uzh.ifi.seal.soprafs20.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void createUser() throws Exception {
        User user = new User();
        user.setUsername("Saurabh");
        user.setPassword("1234");

        MockHttpServletRequestBuilder postRequest = post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(postRequest).andExpect(status().isCreated());
    }

    @Test
    public void getUser() throws Exception{
        MockHttpServletRequestBuilder getRequest = get("/user/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(getRequest).andExpect(status().isOk());
    }

    @Test
    public void login() throws Exception{
        MockHttpServletRequestBuilder getRequest = get("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Basic bAD8AGwAOgBsAPwAbAA=");

        User testUser = new User();
        testUser.setId(1L);
        testUser.setToken("supersecrettokenvalue");
        Mockito.when(userService.login(Mockito.any(), Mockito.any())).thenReturn(testUser);

        mockMvc.perform(getRequest)
                .andExpect(status().isAccepted());
    }

    @Test
    public void updateUser() throws Exception{
        UserUpdateDTO user = new UserUpdateDTO();
        MockHttpServletRequestBuilder putRequest = put("/user/101/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void logout() throws Exception{
        MockHttpServletRequestBuilder putRequest = put("/user/101/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void  invitation() throws Exception{
        UserPutDTO userPutDTO = new UserPutDTO();

        MockHttpServletRequestBuilder putRequest = put("/user/101/invitation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPutDTO))
                .header("X-Auth-Token","supersecrettokenvalue");

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

    }

    /**
     * Helper Method to convert userPostDTO into a JSON string such that the input can be processed
     * Input will look like this: {"name": "Test User", "username": "testUsername"}
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
