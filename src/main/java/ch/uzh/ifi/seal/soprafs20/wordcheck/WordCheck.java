package ch.uzh.ifi.seal.soprafs20.wordcheck;

import ch.uzh.ifi.seal.soprafs20.constant.GameStatus;
import ch.uzh.ifi.seal.soprafs20.entity.Game;
import ch.uzh.ifi.seal.soprafs20.entity.User;
import ch.uzh.ifi.seal.soprafs20.exceptions.NotFoundException;
import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;
import ch.uzh.ifi.seal.soprafs20.repository.GameRepository;
import ch.uzh.ifi.seal.soprafs20.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;


/**
 * Game WordCheck
 * This class abstracts the checking of the submitted clues.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
public class WordCheck {

    public WordCheck() {
    }

    public boolean checkEnglishWord(String word) {
        String wordsApiUrl = "https://wordsapiv1.p.rapidapi.com/words/" + word;
        try {
            URL url = new URL(wordsApiUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-rapidapi-host", "wordsapiv1.p.rapidapi.com");
            con.setRequestProperty("x-rapidapi-key", "6tOFozeIBpmshh7WXvuDxArPPBFWp1NDUDvjsn6DLJkBRHGn1x");
            int responseCode = con.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return false;
            }
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
        return true;
    }
}
