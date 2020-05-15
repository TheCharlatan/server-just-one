package ch.uzh.ifi.seal.soprafs20.worker;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.utils.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Transactional
public class LobbyPollWorker implements Runnable {

    private Thread thread;

    private volatile boolean start = true;

    public LinkedBlockingQueue<Pair<Long, Lobby>> queue = new LinkedBlockingQueue<>();

    private ArrayList<Pair<Long, Lobby>> subscriptions = new ArrayList();
    private LobbyRepository lobbyRepository;

    @Autowired
    public LobbyPollWorker(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    // subscribe the resource
    public void subscribe(Long id) {
        // create a new subscription
        Pair<Long, Lobby> subscribed = new Pair<>(id, getExistingLobby(id));
        // check if we are already subscribed to that lobby
        for (Pair<Long, Lobby> subscription: subscriptions) {
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
                    thread = new Thread(this, "Lobby Worker Thread");
                    thread.start();
                }
            }
        }
    }

    // unsubscribe the resource
    public void unsubscribe(Long id) {
        Iterator<Pair<Long, Lobby>> iter = subscriptions.iterator();
        while (iter.hasNext()) {
            if (iter.next().x == id) {
                iter.remove();
                return;
            }
        }
    }

    private Lobby getExistingLobby(long id) {
        Optional<Lobby> optionalLobby = lobbyRepository.findById(id);
        if (!optionalLobby.isPresent()) {
            throw new NotFoundException(String.format("Could not find lobby with id %d.", id));
        }
        return optionalLobby.get();
    }

    @Override
    public void run() {
        while(true) {
            try {
                for (Pair<Long, Lobby> subscription: subscriptions) {
                    Lobby lobby = getExistingLobby(subscription.x);
                    Lobby subscribedLobby = subscription.y;
                    System.out.println(lobby.toString() + subscribedLobby.toString());

                    if (!lobby.toString().equals(subscribedLobby.toString())) {
                        Pair<Long, Lobby> newData = new Pair(subscription.x, lobby);
                        queue.add(newData);
                        subscription.y = lobby;
                    }
                }
                TimeUnit.SECONDS.sleep(1);
             } catch (InterruptedException e) {
                 throw new ServiceException("Cannot get latest update. ");
             }
        }
    }
}
