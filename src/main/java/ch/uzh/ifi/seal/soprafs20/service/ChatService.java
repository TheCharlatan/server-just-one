package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.rest.dto.ChatMessageDTO;
import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.service.ChatPollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;

import java.util.ArrayList;
import java.util.Queue;
import java.util.List;
import java.util.Optional;
import java.util.LinkedList;
import java.util.Collections;

/**
 * Chat Service
 * This class is the "worker" and responsible for all functionality related to the chat
 * (e.g., it creates, modifies). The result will be passed back to the caller.
 */

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatPollService chatPollService;

    @Autowired
    public ChatService(@Qualifier("chatRepository") ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
        this.chatPollService = new ChatPollService(chatRepository);
        Chat chat = new Chat();
        chat.setId(1l);
        saveChat(chat);
    }

    public void addChatMessage(long id, ChatMessageDTO message){
        if (message.getMessage().length() + message.getUsername().length() >= 250) {
            throw new ServiceException("This message is too long");
        }

        Chat chat = getExistingChat(id);
        List<String> chatListOrig = chat.getChatHistory();
        Collections.reverse(chatListOrig);
        Queue<String> chatQueue = new LinkedList<>(chatListOrig);
        chatQueue.add(message.getUsername() + ":" + message.getMessage());
        if (chatQueue.size() > 100) {
            chatQueue.remove();
        }
        ArrayList<String> chatList = new ArrayList<>(chatQueue);
        Collections.reverse(chatList);
        chat.setChatHistory(chatList);
        saveChat(chat);
    }

    public List<ChatMessageDTO> getChatMessages(long id) {
        Chat chat = getExistingChat(id);
        ArrayList<ChatMessageDTO> chatHistory = new ArrayList<>();
        for (String message: chat.getChatHistory()) {
            ChatMessageDTO chatMessage = new ChatMessageDTO();
            String[] splitMessage = message.split(":", 2);
            if (splitMessage.length == 1) {
                chatMessage.setMessage(splitMessage[0]);
            } else {
                chatMessage.setUsername(splitMessage[0]);
                chatMessage.setMessage(splitMessage[1]);
            }
            chatHistory.add(chatMessage);
        }
        return chatHistory;
    }

    // subscription method for a certain chat id
    public void subscribe(Long id) {
        Optional<Chat> chat = chatRepository.findById(id);
        if (!chat.isPresent()) {
            throw new NotFoundException("Cannot subscribe to a non-existing chat");
        }
        chatPollService.subscribe(id);
    }

    // unsubscription method for a certain chat id
    public void unsubscribe(Long id) {
        chatPollService.unsubscribe(id);
    }

    // async, returns once there is a change for the chat id
    public void pollGetUpdate(DeferredResult<List<ChatMessageDTO>> result, Long id) {
        Optional<Chat> chat = chatRepository.findById(id);
        if (!chat.isPresent()) {
            throw new NotFoundException("Cannot subscribe to a non-existing chat");
        }
        chatPollService.pollGetUpdate(result, id);
    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
        chatRepository.flush();
        chatPollService.notify(chat.getId());
    }

    public Chat getExistingChat(long id) {
        Optional<Chat> optionalChat = chatRepository.findById(id);
        if (optionalChat.isPresent()) {
            return optionalChat.get();
        }
        // the chat does not exist yet, create it
        Chat chat = new Chat();
        chat.setId(id);
        saveChat(chat);
        return chat;
    }
}
