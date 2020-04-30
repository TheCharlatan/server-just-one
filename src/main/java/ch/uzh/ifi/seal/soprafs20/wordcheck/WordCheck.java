package ch.uzh.ifi.seal.soprafs20.wordcheck;

import ch.uzh.ifi.seal.soprafs20.exceptions.ServiceException;

import java.net.URL;
import java.net.HttpURLConnection;


/**
 * Game WordCheck
 * This class abstracts the checking of the submitted clues.
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back to the caller.
 */
public class WordCheck {

    public WordCheck() {
        // in the future fetch the api key here
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
            throw new ServiceException("Could not connect to dicionary at wordsapi");
        }
        return true;
    }
}

