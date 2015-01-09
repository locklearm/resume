package edu.ncsu.csc.wolfwear.dialogue_management;

import java.util.HashMap;
import java.util.List;

import edu.ncsu.csc.wolfwear.WolfWearMainActivity;
import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;
import edu.ncsu.csc.wolfwear.natural_language_generation.FrameFillingRequestGenerator;
import edu.ncsu.csc.wolfwear.natural_language_generation.RecommendationGenerator;
import edu.ncsu.csc.wolfwear.natural_language_understanding.FrameFiller;

/**
 * This class is the main class of the application.  It handles the flow
 * of the program.  This is a singleton class to prevent accidentally
 * creating more than one.
 *
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class DialogueManager {

    /**
     * Singleton instance of the Dialogue Manager
     */
    private static DialogueManager instance = null;

    /**
     * The speech frame that we are currently working with
     */
    private Frame frame;
    /**
     * The class that handles filling the frame, based on user utterances
     */
    private FrameFiller frameFiller;
    /**
     * The class that builds requests from the user for more information.
     */
    private FrameFillingRequestGenerator frameRequester;
    /**
     * The class that builds recommendation utterances
     */
    private RecommendationGenerator recommender;

    // boolean to determine if system is currently trying to verify user responses
    private boolean inVerifyState = false;

    //latest hashmap of values that will need to be verified for accuracy
    private HashMap<Frame.SlotName, String> valsToVerify = new HashMap<Frame.SlotName, String>();

    /**
     * Private constructor prevents accidentally creating
     * a new instance of this class
     */
    private DialogueManager(){

    }

    /**
     * The real constructor we use.
     * @param activity  The activity for our app.
     */
    private DialogueManager(WolfWearMainActivity activity) {

        this.frame = new Frame();
        this.frameFiller = new FrameFiller();
        this.frameRequester = new FrameFillingRequestGenerator();
        this.recommender = new RecommendationGenerator();

    }

    /**
     * Returns the instance of this class, or null, if the init
     * method has not been called yet
     *
     * @return Returns the instance of this class
     */
    public static DialogueManager getInstance() {

        return DialogueManager.instance;
    }

    /**
     * This method is used to let us
     * know that Google has analyzed the speech.
     * @param possibleUtterances
     */
    public String reportUserSpeech(List<UserSpeechPossibility> possibleUtterances) {

        String returnThis = "";
        if (this.frame.isFrameFull()) {
            //frame is full as result of last bit, where person got useful advice and all.
            // Now we wipe it so they can start over.
            System.out.println("I think the frame is full!");
            WolfWearLogger.log(System.currentTimeMillis() + " DialogueManager's reportUserSpeech reset the frame. ");

            this.frame.clearAllSlots();
        }
        //if system is taking in information, not trying to verify it
        WolfWearLogger.log(System.currentTimeMillis() + " reportUserSpeech believes value of inVerifyState is " + inVerifyState + " ");

        if (!inVerifyState) {

            //If we haven't filled the frame yet, then we use this to get the user's response
            if (!this.frame.isFrameFull()) {

                //Pass everything to the frame filler
                valsToVerify = this.frameFiller.fillFrame(possibleUtterances, this.frame);

                if (valsToVerify.size() > 0) {
                    inVerifyState = true;
                    returnThis = this.frameRequester.makeRequestForMoreInformation(this.frame, valsToVerify);

                } else {
                    //Check if the frame is actually full
                    this.frameFiller.checkFrameFull(this.frame);

                    //check that the frame is still not full after the new knowledge.
                    // If still not full, make a request for information
                    if (!this.frame.isFrameFull()) {
                        returnThis = this.frameRequester.makeRequestForMoreInformation(this.frame);
                    }
                }
            }
        } else { //in verification state...
            //verification selection returns whether it was successfully completed.
            // If it was, we should toggle out of verify state.
            //note that this is also the code that actually updates the frame values
            inVerifyState = !this.frameFiller.verifyFrameSelection(this.frame, valsToVerify, possibleUtterances);
            if (!inVerifyState) {
                valsToVerify.clear(); //reset the values to be verified

                //Check if the frame is actually full
                this.frameFiller.checkFrameFull(this.frame);

                //check that the frame is still not full after the new knowledge.
                // If still not full, make a request for information
                if (!this.frame.isFrameFull()) {
                    returnThis = this.frameRequester.makeRequestForMoreInformation(this.frame);
                }

                if (this.frame.isFrameFull()) {
                    //The rest of the kludge is here
                    System.out.println("I think the frame is full!");

                    returnThis = this.recommender.makeRecommendationUtterance(this.frame);
                    System.out.println(returnThis);
                }
            } else {
                returnThis = this.frameRequester.makeRequestForMoreInformation(this.frame, valsToVerify);
            }

        }
        WolfWearLogger.log(System.currentTimeMillis() + " reportUserSpeech returns a system utterance of " + returnThis + " ");
        return returnThis;
    }

    /**
     * Gets the frame we are currently using
     * @return the dialogue frame
     */
    public Frame getFrame() {
        return this.frame;
    }

    /**
     * Gets the framefiller we are currently using
     * (useful for getting all the values the frame filler is looking for)
     * @return the dialogue frame
     */
    public FrameFiller getFrameFiller() {
        return this.frameFiller;
    }

    /**
     * Gets the framefiller we are currently using
     * (useful for getting all the values the frame filler is looking for)
     * @return the dialogue frame
     */
    public boolean getInVerifyState() {
        return this.inVerifyState;
    }

    /**
     * This method replaces the main method in the UML, and is how everything starts.
     */
    public static void init(WolfWearMainActivity activity) {

        if (DialogueManager.instance == null) {
            DialogueManager.instance = new DialogueManager(activity);
        }

    }

}
