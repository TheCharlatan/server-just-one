package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.worker.GamePollWorker;
import ch.uzh.ifi.seal.soprafs20.utils.Pair;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;

@Service
@Transactional
public class GamePollService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(GamePollService.class);

    private ArrayList<Pair<Long, DeferredResult<GameGetDTO>>> resultList = new ArrayList<>();
    private GamePollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public GamePollService(GameRepository gameRepository) {
        worker = new GamePollWorker(gameRepository);
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
            Pair<Long, Game> message = worker.queue.take();
            ArrayList<Pair<Long, DeferredResult<GameGetDTO>>> resolvedRequests = new ArrayList<>();
            for (Pair<Long, DeferredResult<GameGetDTO>> request: resultList) {
                //compare ids
                resolvedRequests.add(request);
                if (request.x == message.x) {
                    // set the result
                    request.y.setResult(DTOMapper.INSTANCE.convertEntityToGameGetDTO(message.y));
                }
            }
            for (Pair<Long, DeferredResult<GameGetDTO>> resolved: resolvedRequests) {
                resultList.remove(resolved);
            }
        } catch (InterruptedException e) {
            throw new ServiceException("Cannot get latest update. ");
        }
      }
    }

    public void pollGetUpdate(DeferredResult<GameGetDTO> result, Long id) {
        resultList.add(new Pair<Long, DeferredResult<GameGetDTO>> (id, result));
    }
}
