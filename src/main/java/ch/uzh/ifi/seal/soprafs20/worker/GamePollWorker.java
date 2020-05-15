package ch.uzh.ifi.seal.soprafs20.worker;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
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
public class GamePollWorker implements Runnable {

    private Thread thread;

    private volatile boolean start = true;

    public LinkedBlockingQueue<Pair<Long, Game>> queue = new LinkedBlockingQueue<>();

    private ArrayList<Pair<Long, Game>> subscriptions = new ArrayList();
    private GameRepository gameRepository;

    @Autowired
    public GamePollWorker(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    // subscribe the resource
    public void subscribe(Long id) {
        // create a new subscription
        Pair<Long, Game> subscribed = new Pair<>(id, getExistingGame(id));
        // check if we are already subscribed to that game
        for (Pair<Long, Game> subscription: subscriptions) {
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
                    thread = new Thread(this, "Game Worker Thread");
                    thread.start();
                }
            }
        }
    }

    // unsubscribe the resource
    public void unsubscribe(Long id) {
        Iterator<Pair<Long, Game>> iter = subscriptions.iterator();
        while (iter.hasNext()) {
            if (iter.next().x == id) {
                iter.remove();
            }
        }
    }

    private Game getExistingGame(long id) {
        Optional<Game> optionalGame = gameRepository.findById(id);
        if (!optionalGame.isPresent()) {
            throw new NotFoundException(String.format("Could not find game with id %d.", id));
        }
        return optionalGame.get();
    }

    @Override
    public void run() {
        while(true) {
            try {
                for (Pair<Long, Game> subscription: subscriptions) {
                    Game game = getExistingGame(subscription.x);
                    Game subscribedGame = subscription.y;

                    if (!game.toString().equals(subscribedGame.toString())) {
                        Pair<Long, Game> newData = new Pair(subscription.x, game);
                        queue.add(newData);
                        subscription.y = game;
                    }
                }
                TimeUnit.SECONDS.sleep(2);
             } catch (InterruptedException e) {
                 throw new ServiceException("Cannot get latest update. ");
             }
        }
    }
}
