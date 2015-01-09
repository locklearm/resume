package edu.ncsu.csc.wolfwear.dialogue_management;

/**
 * This class represents a single possible utterance from the user.  This will often
 * come in a collection of other possible utterances.
 *
 * TODO finish commenting this class
 *
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class UserSpeechPossibility {

    private String utterance;
    private double reportedConfidence;

    public UserSpeechPossibility(String utterance, double reportedConfidence) {

        this.utterance = utterance;
        this.reportedConfidence = reportedConfidence;

    }

    public String getUtterance() {
        return this.utterance;
    }

    public double getReportedConfidence() {
        return this.reportedConfidence;
    }

}
