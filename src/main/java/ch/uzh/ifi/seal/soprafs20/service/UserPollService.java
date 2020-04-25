package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.constant.UserStatus;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.rest.dto.*;
import ch.uzh.ifi.seal.soprafs20.rest.mapper.DTOMapper;
import ch.uzh.ifi.seal.soprafs20.worker.UserPollWorker;
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

//@Service("UserPollService")
@Service
@Transactional
public class UserPollService implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(UserPollService.class);

    private ArrayList<Pair<Long, DeferredResult<UserGetDTO>>> resultList = new ArrayList<Pair<Long, DeferredResult<UserGetDTO>>>();
    private UserPollWorker worker;

    private Thread thread;

    private volatile boolean start = true;

    @Autowired
    public UserPollService(UserRepository userRepository) {
        worker = new UserPollWorker(userRepository);
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
            Pair<Long, User> message = worker.queue.take();
            ArrayList<Pair<Long, DeferredResult<UserGetDTO>>> resolvedRequests = new ArrayList<>();
            for (Pair<Long, DeferredResult<UserGetDTO>> request: resultList) {
                //compare ids
                resolvedRequests.add(request);
                if (request.x == message.x) {
                    // set the result
                    request.y.setResult(DTOMapper.INSTANCE.convertEntityToUserGetDTO(message.y));
                }
            }
            for (Pair<Long, DeferredResult<UserGetDTO>> resolved: resolvedRequests) {
                resultList.remove(resolved);
            }
        } catch (InterruptedException e) {
            throw new ServiceException("Cannot get latest update. ");
        }
      }
    }

    public void pollGetUpdate(DeferredResult<UserGetDTO> result, Long id) {
        resultList.add(new Pair<Long, DeferredResult<UserGetDTO>> (id, result));
    }
}
