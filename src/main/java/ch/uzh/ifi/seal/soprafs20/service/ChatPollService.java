package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.worker.ChatPollWorker;
import ch.uzh.ifi.seal.soprafs20.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

//@Service("ChatPollService")
@Service
@Transactional
public class ChatPollService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ChatPollService.class);

    private ArrayList<Pair<Long, DeferredResult<List<ChatMessageDTO>>>> resultList = new ArrayList<>();
    private ChatPollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public ChatPollService(ChatRepository chatRepository) {
        worker = new ChatPollWorker(chatRepository);
    }

    public void subscribe(Long id) {
        logger.info("Starting server");
        worker.subscribe(id);
        startThread();
    }

    public void unsubscribe(Long id) {
        worker.unsubscribe(id);
    }

    private void startThread() {
        if (start) {
            synchronized (this) {
                if (start) {
                    start = false;
                    thread = new Thread(this, "Service Thread");
                    thread.start();
                }
            }
        }
    }

    @Override
    public void run() {

      while (true) {
        try {
            Pair<Long, Chat> message = worker.queue.take();
            ArrayList<Pair<Long, DeferredResult<List<ChatMessageDTO>>>> resolvedRequests = new ArrayList<>();
            for (Pair<Long, DeferredResult<List<ChatMessageDTO>>> request: resultList) {
                //compare ids
                resolvedRequests.add(request);
                if (request.x == message.x) {
                    // set the result
                    List<ChatMessageDTO> chatMessages = new ArrayList<>();
                    for (String sMessage: message.y.getChatHistory()) {
                        ChatMessageDTO chatMessage = new ChatMessageDTO();
                        String splitMessage[] = sMessage.split(":", 2);
                        if (splitMessage.length == 1) {
                            chatMessage.setMessage(splitMessage[0]);
                        } else {
                            chatMessage.setUsername(splitMessage[0]);
                            chatMessage.setMessage(splitMessage[1]);
                        }
                        chatMessages.add(chatMessage);
                    }
                    request.y.setResult(chatMessages);

                }
            }
            for (Pair<Long, DeferredResult<List<ChatMessageDTO>>> resolved: resolvedRequests) {
                resultList.remove(resolved);
            }
        } catch (InterruptedException e) {
            throw new ServiceException("Cannot get latest update. ");
        }
      }
    }

    public void pollGetUpdate(DeferredResult<List<ChatMessageDTO>> result, Long id) {
        resultList.add(new Pair<Long, DeferredResult<List<ChatMessageDTO>>> (id, result));
    }
}
