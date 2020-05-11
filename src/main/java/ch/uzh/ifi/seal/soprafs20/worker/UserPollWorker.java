package ch.uzh.ifi.seal.soprafs20.worker;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
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
public class UserPollWorker implements Runnable {

    private Thread thread;

    private volatile boolean start = true;

    public LinkedBlockingQueue<Pair<Long, User>> queue = new LinkedBlockingQueue<>();

    private ArrayList<Pair<Long, User>> subscriptions = new ArrayList();
    private UserRepository userRepository;

    @Autowired
    public UserPollWorker(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // subscribe the resource
    public void subscribe(Long id) {
        // create a new subscription
        Pair<Long, User> subscribed = new Pair(id, getExistingUser(id));
        // check if we are already subscribed to that user
        for (Pair<Long, User> subscription: subscriptions) {
            if (subscription.x == id) {
                return;
            }
        }
        // add the subcription to the queue
        subscriptions.add(subscribed);
        startThread();
    }

    // make sure that at least one thread is running
    private void startThread() {
        if (start) {
            synchronized (this) {
                if (start) {
                    start = false;
                    thread = new Thread(this, "User Worker Thread");
                    thread.start();
                }
            }
        }
    }

    // unsubscribe the resource
    public void unsubscribe(Long id) {
        Iterator<Pair<Long, User>> iter = subscriptions.iterator();
        while (iter.hasNext()) {
            if (iter.next().x == id) {
                iter.remove();
            }
        }
    }

    private User getExistingUser(long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new NotFoundException(String.format("Could not find user with id %d.", id));
        }
        return optionalUser.get();
    }

    @Override
    public void run() {
        while(true) {
            try {
                for (Pair<Long, User> subscription: subscriptions) {
                    User user = getExistingUser(subscription.x);
                    User subscribedUser = subscription.y;

                    if (!user.toString().equals(subscribedUser.toString())) {
                        Pair<Long, User> newData = new Pair(subscription.x, user);
                        queue.add(newData);
                        subscription.y = user;
                    }
                }
                TimeUnit.SECONDS.sleep(2);
             } catch (InterruptedException e) {
                 throw new ServiceException("Cannot get latest update. ");
             }
        }
    }
}
