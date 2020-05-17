package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.entity.Lobby;
import ch.uzh.ifi.seal.soprafs20.repository.LobbyRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.worker.LobbyPollWorker;
import ch.uzh.ifi.seal.soprafs20.utils.Pair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.ArrayList;
import java.util.Iterator;

@Service
@Transactional
public class LobbyPollService implements Runnable {

    private ArrayList<Pair<Long, DeferredResult<LobbyGetDTO>>> resultList = new ArrayList<>();
    private LobbyPollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public LobbyPollService(LobbyRepository lobbyRepository) {
        worker = new LobbyPollWorker(lobbyRepository);
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
            Pair<Long, Lobby> message = worker.queue.take();

            // remove the expired or set results
            Iterator<Pair<Long, DeferredResult<LobbyGetDTO>>> expiredIter = resultList.iterator();
            while (expiredIter.hasNext()) {
                if (expiredIter.next().y.isSetOrExpired()) {
                    expiredIter.remove();
                }
            }

            for (Pair<Long, DeferredResult<LobbyGetDTO>> request: resultList) {
                //compare ids
                if (request.x == message.x) {
                    // set the result
                    request.y.setResult(DTOMapper.INSTANCE.convertEntityToLobbyGetDTO(message.y));
                }
            }

            // remove the expired or set results
            Iterator<Pair<Long, DeferredResult<LobbyGetDTO>>> iter = resultList.iterator();
            while (iter.hasNext()) {
                if (iter.next().y.isSetOrExpired()) {
                    iter.remove();
                }
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
