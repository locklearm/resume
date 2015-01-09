package edu.ncsu.csc.wolfwear.dialogue_management;

import java.util.List;

import edu.ncsu.csc.wolfwear.WolfWearMainActivity;

/**
 * This class manages our interactions with the user interface.
 *
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class UserInterfaceManager {

    /**
     * Singleton instance
     */
    private static UserInterfaceManager instance = null;

    /**
     * The activity associated with our app
     */
    private WolfWearMainActivity activity;

    /**
     * Private constructor prevents accidental instantiation
     */
    private UserInterfaceManager(WolfWearMainActivity activity) {
        this.activity = activity;
    }

    /**
     * Returns the instance of this class, or null if init hasn't been called
     * yet.
     * @return  The instance of this class
     */
    public static UserInterfaceManager getInstance() {

        return UserInterfaceManager.instance;
    }

    /**
     * Displays the given text to the user in the main text field.  This
     * replaces any text currently there.
     * @param text the text to display
     */
    public void displayText(String text) {

        this.activity.updateMainTextField(text);

    }

    /**
     * Sets the text of the button the user sees at the bottom of the screen
     * @param text  The text to display
     */
    public void setButtonText(String text) {

        this.activity.setButtonText(text);

    }

    /**
     * Speaks the given phrase to the user
     * @param phrase The phrase to speak
     */
    public void speak(String phrase) {
        this.activity.speakWords(phrase);
    }

    /**
     * Initializes this class
     * @param activity  The activity associated with our app
     */
    public static void init(WolfWearMainActivity activity) {

        if (UserInterfaceManager.instance == null) {
            UserInterfaceManager.instance = new UserInterfaceManager(activity);
        }

    }

    /**
     * Closes the application
     */
    public void closeApp() {
        this.activity.finish();
    }

}
