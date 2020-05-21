package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.exceptions.AuthenticationException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

public class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    private Chat testChat;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);

        // given
        testChat = new Chat();
        testChat.setId(1L);
        ArrayList<String> messages = new ArrayList<>();
        messages.add("Hi!");
        testChat.setChatHistory(messages);

        // when -> any object is being save in the chatRepository -> return the dummy testChat
        Mockito.when(chatRepository.save(Mockito.any())).thenReturn(testChat);
    }

    @Test
    public void getChat_success() {
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
        List<ChatMessageDTO> chat = chatService.getChatMessages(1l);
        assertEquals("Hi!", chat.get(0).getMessage());
    }

    @Test
    public void getChat_twoMessage() {
        List<String> messages = testChat.getChatHistory();
        messages.add("user:message");
        testChat.setChatHistory(messages);
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
        List<ChatMessageDTO> chat = chatService.getChatMessages(1l);
        assertEquals("Hi!", chat.get(0).getMessage());
        assertEquals("user", chat.get(1).getUsername());
        assertEquals("message", chat.get(1).getMessage());
    }

    @Test
    public void submitMessage_success() {
        ChatMessageDTO chatMessage = new ChatMessageDTO();
        chatMessage.setMessage("hi");
        chatMessage.setUsername("user");
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
        chatService.addChatMessage(1l, chatMessage);
        assertEquals(2, testChat.getChatHistory().size());
        assertEquals("Hi!", testChat.getChatHistory().get(1));
        assertEquals("user:hi", testChat.getChatHistory().get(0));
    }

    @Test
    public void submitManyMessages_success() {
        // test the initializor
        assertEquals("Hi!", testChat.getChatHistory().get(0));
        ChatMessageDTO chatMessage = new ChatMessageDTO();
        chatMessage.setMessage("hi");
        chatMessage.setUsername("user");
        for (int i = 0; i < 99; i++) {
            Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
            chatService.addChatMessage(1l, chatMessage);

        }
        assertEquals(100, testChat.getChatHistory().size());
        assertEquals("Hi!", testChat.getChatHistory().get(99));
        chatMessage.setMessage("testMessage");
        chatMessage.setUsername("testUsername");
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
        chatService.addChatMessage(1l, chatMessage);
        assertEquals(100, testChat.getChatHistory().size());
        assertEquals("user:hi", testChat.getChatHistory().get(99));
        assertEquals("testUsername:testMessage", testChat.getChatHistory().get(0));
    }

    @Test
    public void submitTooLongMessage_exception() {
        ChatMessageDTO chatMessage = new ChatMessageDTO();
        chatMessage.setUsername("user");
        chatMessage.setMessage("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        assertThrows(ServiceException.class, ()->chatService.addChatMessage(1l, chatMessage));
    }

    @Test
    public void chatEntity_toString_success() {
        assertEquals("Chat{id=1, chatHistory='[Hi!]}", testChat.toString());
    }
}
