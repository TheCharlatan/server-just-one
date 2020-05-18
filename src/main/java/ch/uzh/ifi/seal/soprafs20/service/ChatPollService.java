package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.worker.ChatPollWorker;
import ch.uzh.ifi.seal.soprafs20.utils.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

@Service
@Transactional
public class ChatPollService implements Runnable {

    private ArrayList<Pair<Long, DeferredResult<List<ChatMessageDTO>>>> resultList = new ArrayList<>();
    private ChatPollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public ChatPollService(ChatRepository chatRepository) {
        worker = new ChatPollWorker(chatRepository);
    }

    public void subscribe(Long id) {
        worker.subscribe(id);
        startThread();
    }

    public void unsubscribe(Long id) {
        worker.unsubscribe(id);
    }

    public void notify(Long id) {
        worker.notify(id);
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
                // remove the expired or set results
                Iterator<Pair<Long, DeferredResult<List<ChatMessageDTO>>>> expiredIter = resultList.iterator();
                while (expiredIter.hasNext()) {
                    if (expiredIter.next().y.isSetOrExpired()) {
                        expiredIter.remove();
                    }
                }

                //construct the message list
                List<ChatMessageDTO> chatMessages = new ArrayList<>();
                for (String sMessage: message.y.getChatHistory()) {
                    ChatMessageDTO chatMessage = new ChatMessageDTO();
                    String[] splitMessage = sMessage.split(":", 2);
                    if (splitMessage.length == 1) {
                        chatMessage.setMessage(splitMessage[0]);
                    } else {
                        chatMessage.setUsername(splitMessage[0]);
                        chatMessage.setMessage(splitMessage[1]);
                    }
                    chatMessages.add(chatMessage);
                }

                for (Pair<Long, DeferredResult<List<ChatMessageDTO>>> request: resultList) {
                    //compare ids
                    if (request.x == message.x) {
                        // set the result
                        request.y.setResult(chatMessages);
                    }
                }

                // remove the expired or set results
                Iterator<Pair<Long, DeferredResult<List<ChatMessageDTO>>>> iter = resultList.iterator();
                while (iter.hasNext()) {
                    if (iter.next().y.isSetOrExpired()) {
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                throw new ServiceException("Cannot get latest update. ");
            }
        }
    }

    public void pollGetUpdate(DeferredResult<List<ChatMessageDTO>> result, Long id) {
        resultList.add(new Pair<Long, DeferredResult<List<ChatMessageDTO>>> (id, result));
    }
}
