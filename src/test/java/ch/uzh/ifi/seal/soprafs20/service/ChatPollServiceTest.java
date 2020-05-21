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
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Optional;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.List;

public class ChatPollServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatPollService chatPollService;

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

        chatPollService = new ChatPollService(chatRepository);
    }

    @Test
    public void pollChat_success() throws Exception {
		/*
		* This is testing multithreaded code, so we just test the happy paths
		* and allow some time for execution.
		* NOTE: This is brittle and could break if execution requires longer than a
		* second for some reason.
		* Also note that this is in effect an integration test of the ChatPollWorker
		* as well
		*/

		// build a new chat object with an extra message
		Chat newTestChat = new Chat();
		newTestChat.setId(1L);
        List<String> messages = testChat.getChatHistory();
        messages.add("Hi!");
		newTestChat.setChatHistory(messages);

		// subscribe twice to test branches
        chatPollService.subscribe(1L);
        chatPollService.subscribe(1L);

        DeferredResult<List<ChatMessageDTO>> result = new DeferredResult<>();
        chatPollService.pollGetUpdate(result, 1L);
	    chatPollService.notify(1L);
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(testChat));
		TimeUnit.SECONDS.sleep(1);
	    chatPollService.notify(1L);
        Mockito.when(chatRepository.findById(Mockito.any())).thenReturn(Optional.of(newTestChat));

        TimeUnit.SECONDS.sleep(1);
        chatPollService.unsubscribe(1L);
    }

}
