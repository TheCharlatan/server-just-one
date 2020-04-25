package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.worker.LobbyPollWorker;
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

//@Service("LobbyPollService")
@Service
@Transactional
public class LobbyPollService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(LobbyPollService.class);

    private ArrayList<Pair<Long, DeferredResult<LobbyGetDTO>>> resultList = new ArrayList<Pair<Long, DeferredResult<LobbyGetDTO>>>();
    private LobbyPollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public LobbyPollService(LobbyRepository lobbyRepository) {
        worker = new LobbyPollWorker(lobbyRepository);
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
            Pair<Long, Lobby> message = worker.queue.take();
            ArrayList<Pair<Long, DeferredResult<LobbyGetDTO>>> resolvedRequests = new ArrayList<>();
            for (Pair<Long, DeferredResult<LobbyGetDTO>> request: resultList) {
                //compare ids
                resolvedRequests.add(request);
                if (request.x == message.x) {
                    // set the result
                    request.y.setResult(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(message.y));
                }
            }
            for (Pair<Long, DeferredResult<LobbyGetDTO>> resolved: resolvedRequests) {
                resultList.remove(resolved);
            }
        } catch (InterruptedException e) {
            throw new ServiceException("Cannot get latest update. ");
        }
      }
    }

    public void pollGetUpdate(DeferredResult<LobbyGetDTO> result, Long id) {
        resultList.add(new Pair<Long, DeferredResult<LobbyGetDTO>> (id, result));
    }
}
