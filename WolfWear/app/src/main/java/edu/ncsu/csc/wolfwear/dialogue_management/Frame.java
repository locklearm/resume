package edu.ncsu.csc.wolfwear.dialogue_management;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;

/**
 * This class stores the speech frame for our dialogue
 *
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class Frame {

    /**
     * The slots in our frame, keyed by the SlotName.
     */
    private Map<SlotName, Slot> slots;
    /**
     * This is used by the dialogue manager. If frameFull == true,
     * then we move to the recommendation generation step.
     */
    private boolean frameFull;

    //TODO Consider keeping a history of all the utterances here

    /**
     * Constructor.  Goes ahead and fills the slots field
     */
    public Frame() {

        //Create the map to hold the slots in.  Initialize to the right size
        this.slots = new HashMap<SlotName, Slot>(SlotName.values().length);

        initializeAllSlots();

    }

    public void initializeAllSlots () {
        //Iterate through all values of the SlotName enum, and
        //create slots for each
        for (SlotName name : SlotName.values()) {
            this.slots.put(name, new Slot());
        }
    }

    /**
     * @param name The name of the slot to look for
     * @return Returns the given slot
     */
    public Slot getSlot(SlotName name) {
        return this.slots.get(name);
    }

    /**
     * @return True if we are ready to make a recommendation
     */
    public boolean isFrameFull() {
        return this.frameFull;
    }

    public void setFrameFull(boolean frameFull) {
        this.frameFull = frameFull;
    }

    /**
     * Clears all slots in the frame - allows us to end cleaner than we did otherwise
     */
    public void clearAllSlots () {
        //clear all of the slots
        this.slots.clear();

        //tell the frame that it is no longer full
        setFrameFull(false);

        //reinitialize all of the slots
        initializeAllSlots();
    }

    /**
     * Prints field names and values of all slots in the frame -
     */
    public String frameFieldsToString () {

        String myToString = "";

        //Iterate through all values of the SlotName enum, adding to string
        for (SlotName name : SlotName.values()) {
            myToString += name + ": " + getSlot(name).getValue() + "\n";
        }

        //log this in a more log file format friendly state
        frameFieldsToLogString();

        return myToString;
    }

    /**
     * Prints field names and values of all slots in the frame -
     */
    public void frameFieldsToLogString () {

        String myToString = "";

        //Iterate through all values of the SlotName enum, adding to string
        for (SlotName name : SlotName.values()) {
            myToString += name + ": " + getSlot(name).getValue() + "     ; ";
        }

        WolfWearLogger.log(System.currentTimeMillis() + " current frame fields are " + myToString + " ");
    }

    public boolean onlyInterviewTypeFilled() {
        ArrayList<SlotName> wearingItems = new ArrayList<Frame.SlotName>(Arrays.asList(Frame.SlotName.OUTERWEAR_ITEM,
                Frame.SlotName.OUTERWEAR_ITEM_COLOR, Frame.SlotName.TOP_ITEM, Frame.SlotName.TOP_ITEM_COLOR, Frame.SlotName.BOTTOM_ITEM,
                Frame.SlotName.BOTTOM_ITEM_COLOR, Frame.SlotName.SHOE_TYPE, Frame.SlotName.SHOE_COLOR));

        for (int i = 0; i < wearingItems.size(); i++) {
            if (this.getSlot(wearingItems.get(i)).isFilled()) {
                return false;
            }
        }
        return true;
    }

    /**
     * This class represents a slot in our frame
     */
    public class Slot {

        /**
         * Whether this slot has been filled yet
         */
        private boolean filled;
        /**
         * If filled, the value of the slot
         */
        private String value;
        /**
         * If filled, the confidence we have in this value
         */
        private double confidence;

        public Slot() {
            this.filled = false;
        }

        public String getValue() {
            return this.value;
        }

        public void setValue(String value) {
            //will have this also control setting whether or not a slot is filled
            this.value = value;
            if(value == "") {
                setFilled(false);
            } else {
                setFilled(true);
            }
        }

        public double getConfidence() {
            return this.confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        /**
         * @return  Returns true if this slot has been filled, false otherwise
         */
        public boolean isFilled() {
            return this.filled;
        }

        public void setFilled(boolean filled) {
            this.filled = filled;
        }

    }

    /**
     * These are the names of all the slots that we have
     */
    public enum SlotName {

        INTERVIEW_TYPE,
        OUTERWEAR_ITEM,
        OUTERWEAR_ITEM_COLOR,
        TOP_ITEM,
        TOP_ITEM_COLOR,
        BOTTOM_ITEM,
        BOTTOM_ITEM_COLOR,
        SHOE_TYPE,
        SHOE_COLOR
    }

}
