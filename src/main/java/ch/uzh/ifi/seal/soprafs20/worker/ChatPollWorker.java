package ch.uzh.ifi.seal.soprafs20.worker;

import ch.uzh.ifi.seal.soprafs20.entity.Chat;
import ch.uzh.ifi.seal.soprafs20.repository.ChatRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
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
import java.util.Iterator;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
@Transactional
public class ChatPollWorker implements Runnable {

    private Thread thread;

    private volatile boolean start = true;

    public LinkedBlockingQueue<Pair<Long, Chat>> queue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Long> notifications = new LinkedBlockingQueue<>();

    private ArrayList<Pair<Long, Chat>> subscriptions = new ArrayList();
    private ChatRepository chatRepository;

    @Autowired
    public ChatPollWorker(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    // subscribe the resource
    public void subscribe(Long id) {
        // create a new subscription
        Pair<Long, Chat> subscribed = new Pair(id, getExistingChat(id));
        // check if we are already subscribed to that chat
        for (Pair<Long, Chat> subscription: subscriptions) {
            if (subscription.x == id) {
                return;
            }
        }
        // add the subcription to the queue
        subscriptions.add(subscribed);
        startThread();
    }

    public void notify(Long id) {
        notifications.add(id);
    }

    // make sure that at least one thread is running
    private void startThread() {
        if (start) {
            synchronized (this) {
                if (start) {
                    start = false;
                    thread = new Thread(this, "Chat Worker Thread");
                    thread.start();
                }
            }
        }
    }

    // unsubscribe the resource
    public void unsubscribe(Long id) {
        Iterator<Pair<Long, Chat>> iter = subscriptions.iterator();
        while (iter.hasNext()) {
            if (iter.next().x == id) {
                iter.remove();
                return;
            }
        }
    }

    private Chat getExistingChat(long id) {
        Optional<Chat> optionalChat = chatRepository.findById(id);
        if (!optionalChat.isPresent()) {
            subscriptions.remove(id);
            return new Chat();
        }
        return optionalChat.get();
    }

    @Override
    public void run() {
        while(true) {
            try {
                Long chatId = notifications.take();
                for (Pair<Long, Chat> subscription: subscriptions) {
                    if (chatId == subscription.x) {
                        Chat chat = getExistingChat(subscription.x);
                        Pair<Long, Chat> newData = new Pair(subscription.x, chat);
                        queue.add(newData);
                        subscription.y = chat;
                    }
                }
             } catch (InterruptedException e) {
                 throw new ServiceException("Cannot get latest update. ");
             }
        }
    }
}
