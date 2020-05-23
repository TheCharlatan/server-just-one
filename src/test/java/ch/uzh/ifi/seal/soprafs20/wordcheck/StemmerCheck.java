package ch.uzh.ifi.seal.soprafs20.service;

import ch.uzh.ifi.seal.soprafs20.wordcheck.Stemmer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

public class StemmerCheck {

    @InjectMocks
    private Stemmer stemmer;

    @Test
    public void stemmer_Test_Vector() {
        stemmer = new Stemmer();
        assertTrue(stemmer.checkStemMatch("luck" , "luck"));
        assertFalse(stemmer.checkStemMatch("lucky" , "unlucky"));
        assertFalse(stemmer.checkStemMatch("unlucky" , "lucky"));
        assertTrue(stemmer.checkStemMatch("snowed" , "snow"));
        assertTrue(stemmer.checkStemMatch("running" , "run"));
        assertTrue(stemmer.checkStemMatch("having" , "have"));
        assertFalse(stemmer.checkStemMatch("dying" , "die"));
        assertFalse(stemmer.checkStemMatch("generously" , "generous"));
        assertTrue(stemmer.checkStemMatch("caresses", "caress"));
        assertTrue(stemmer.checkStemMatch("ponies", "poni"));
        assertFalse(stemmer.checkStemMatch("plays", "play"));
        assertFalse(stemmer.checkStemMatch("am", "be"));
        assertFalse(stemmer.checkStemMatch("troubling", "trouble"));
        assertTrue(stemmer.checkStemMatch("cats", "cat"));
        assertFalse(stemmer.checkStemMatch("troubled", "trouble"));
        assertFalse(stemmer.checkStemMatch("friend", "friendship"));
        assertFalse(stemmer.checkStemMatch("friendship", "friend"));
        assertTrue(stemmer.checkStemMatch("destabilize", "destabil"));
        assertTrue(stemmer.checkStemMatch("misunderstanding", "misunderstand"));
        assertTrue(stemmer.checkStemMatch("football", "footbal"));
        assertFalse(stemmer.checkStemMatch("python", "pythoners"));
        assertTrue(stemmer.checkStemMatch("pythoners", "python"));
        assertTrue(stemmer.checkStemMatch("very", "veri"));
        assertTrue(stemmer.checkStemMatch("intelligent", "intellig"));
        assertTrue(stemmer.checkStemMatch("scientific", "scientif"));
        assertTrue(stemmer.checkStemMatch("processes", "process"));
        assertTrue(stemmer.checkStemMatch("mining", "mine"));
        assertTrue(stemmer.checkStemMatch("hopping", "hop"));
        assertTrue(stemmer.checkStemMatch("filing", "file"));
        assertFalse(stemmer.checkStemMatch("relational", "relate"));
        assertTrue(stemmer.checkStemMatch("relational", "relat"));
        assertFalse(stemmer.checkStemMatch("vietnamization", "vietnamize"));
        assertFalse(stemmer.checkStemMatch("vietnamization", "vietnamiz"));
        assertTrue(stemmer.checkStemMatch("feudalism", "feudal"));
        assertFalse(stemmer.checkStemMatch("hopefulness", "hopeful"));
        assertFalse(stemmer.checkStemMatch("sensitiviti", "sensitive"));
        assertFalse(stemmer.checkStemMatch("sensitiviti", "sensitiv"));
        assertTrue(stemmer.checkStemMatch("triplicate", "triplic"));
        assertTrue(stemmer.checkStemMatch("communism", "commun"));
        assertTrue(stemmer.checkStemMatch("homologou", "homolog"));
        assertTrue(stemmer.checkStemMatch("adjustable", "adjust"));
        assertTrue(stemmer.checkStemMatch("defensible", "defens"));
        assertTrue(stemmer.checkStemMatch("effective", "effect"));
        assertTrue(stemmer.checkStemMatch("allowance", "allow"));
        assertTrue(stemmer.checkStemMatch("irritant", "irrit"));
        assertTrue(stemmer.checkStemMatch("dependent", "depend"));
        assertTrue(stemmer.checkStemMatch("activate", "activ"));
        assertTrue(stemmer.checkStemMatch("adoption", "adopt"));
    }
}

