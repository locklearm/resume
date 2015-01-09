package edu.ncsu.csc.wolfwear.natural_language_generation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.ncsu.csc.wolfwear.dialogue_management.Frame;
import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;

/**
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class FrameFillingRequestGenerator {

    public String makeRequestForMoreInformation(Frame f) {

        if (!f.getSlot(Frame.SlotName.INTERVIEW_TYPE).isFilled()) {
            return "Hahaha, Great! Let us begin. \n First, tell me what type of interview you are going to.  " +
                    "Your options are 'business attire', 'business casual', and 'casual'";
        }

        if (f.onlyInterviewTypeFilled()) {
            return "Got you! Now, please tell me what you plan to wear for your interview? " +
                    "An example answer is shown below.";
        }
        else {
            if (!f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).isFilled()) {
                return "Thank you! I still need a little more information from you. " +
                        "Please tell me what type of outerwear you plan to wear.  " +
                        "Your options are listed below.";
            }
            if (!f.getSlot(Frame.SlotName.OUTERWEAR_ITEM_COLOR).isFilled()) {
                return "Please tell me what color " + f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).getValue() +
                        " you plan to wear.";
            }
            if (!f.getSlot(Frame.SlotName.TOP_ITEM).isFilled()) {
                return "Next, please tell me what type of top clothing you plan to wear.";
            }
            if (!f.getSlot(Frame.SlotName.TOP_ITEM_COLOR).isFilled()) {
                return "Can you specify the color of the " + f.getSlot(Frame.SlotName.TOP_ITEM).getValue() +
                        " for me?";
            }
            if (!f.getSlot(Frame.SlotName.BOTTOM_ITEM).isFilled()) {
                return "Okay.  Now please tell me what type of bottom clothing you plan to wear.";
            }
            if (!f.getSlot(Frame.SlotName.BOTTOM_ITEM_COLOR).isFilled()) {
                return "And the color of your " + f.getSlot(Frame.SlotName.BOTTOM_ITEM).getValue() +
                        " ?";
            }
            if (!f.getSlot(Frame.SlotName.SHOE_TYPE).isFilled()) {
                return "All right, we're almost done!  Please tell me what type of shoes you plan to wear.";
            }
            if (!f.getSlot(Frame.SlotName.SHOE_COLOR).isFilled()) {
                return "Finally, please tell me what color " + f.getSlot(Frame.SlotName.SHOE_TYPE).getValue() +
                        " you plan to wear.";
            }
        }

        return "We still need more information.  Say more things about what you plan to wear.";
    }

    //if there are values to verify, ask the user to verify all of them
    public String makeRequestForMoreInformation(Frame f, HashMap<Frame.SlotName, String> valsToVerifyOriginal) {

        String returnString = "So you are wearing ";

        //make a deep copy so it don't mess up with the original instance
        Map<Frame.SlotName, String> valsToVerify = new HashMap<Frame.SlotName, String>();
        for (Map.Entry<Frame.SlotName, String> entry : valsToVerifyOriginal.entrySet()) {
            Frame.SlotName key = entry.getKey();
            String value = entry.getValue();
            valsToVerify.put(key, value);
        }

        if (valsToVerify.containsKey(Frame.SlotName.OUTERWEAR_ITEM)){
            if (valsToVerify.containsKey(Frame.SlotName.OUTERWEAR_ITEM_COLOR)){
                    returnString += valsToVerify.get(Frame.SlotName.OUTERWEAR_ITEM_COLOR) + " " + valsToVerify.get(Frame.SlotName.OUTERWEAR_ITEM)+", ";
                valsToVerify.remove(Frame.SlotName.OUTERWEAR_ITEM_COLOR);
            }
            else{
                returnString += valsToVerify.get(Frame.SlotName.OUTERWEAR_ITEM)+", ";
            }
            valsToVerify.remove(Frame.SlotName.OUTERWEAR_ITEM);
        }
        if (valsToVerify.containsKey(Frame.SlotName.BOTTOM_ITEM)){
            if (valsToVerify.containsKey(Frame.SlotName.BOTTOM_ITEM_COLOR)){
                returnString += valsToVerify.get(Frame.SlotName.BOTTOM_ITEM_COLOR) + " " + valsToVerify.get(Frame.SlotName.BOTTOM_ITEM)+", ";
                valsToVerify.remove(Frame.SlotName.BOTTOM_ITEM_COLOR);
            }
            else{
                returnString += valsToVerify.get(Frame.SlotName.BOTTOM_ITEM)+", ";
            }
            valsToVerify.remove(Frame.SlotName.BOTTOM_ITEM);
        }
        if (valsToVerify.containsKey(Frame.SlotName.TOP_ITEM)){
            if (valsToVerify.containsKey(Frame.SlotName.TOP_ITEM_COLOR)){
                returnString += valsToVerify.get(Frame.SlotName.TOP_ITEM_COLOR) + " " + valsToVerify.get(Frame.SlotName.TOP_ITEM)+", ";
                valsToVerify.remove(Frame.SlotName.TOP_ITEM_COLOR);
            }
            else{
                returnString += valsToVerify.get(Frame.SlotName.TOP_ITEM)+", ";
            }
            valsToVerify.remove(Frame.SlotName.TOP_ITEM);
        }
        if (valsToVerify.containsKey(Frame.SlotName.SHOE_TYPE)){
            if (valsToVerify.containsKey(Frame.SlotName.SHOE_COLOR)){
                returnString += valsToVerify.get(Frame.SlotName.SHOE_COLOR) + " " + valsToVerify.get(Frame.SlotName.SHOE_TYPE)+", ";
                valsToVerify.remove(Frame.SlotName.SHOE_COLOR);
            }
            else{
                returnString += valsToVerify.get(Frame.SlotName.SHOE_TYPE)+", ";
            }
            valsToVerify.remove(Frame.SlotName.SHOE_TYPE);
        }
        if (valsToVerify.containsKey(Frame.SlotName.INTERVIEW_TYPE)){
            returnString = "So you are going to a " + valsToVerify.get(Frame.SlotName.INTERVIEW_TYPE) + " interview, ";
            valsToVerify.remove(Frame.SlotName.INTERVIEW_TYPE);
        }

        //answers about colors, possible only one color
        if (!valsToVerify.isEmpty()){
            for (Map.Entry<Frame.SlotName, String> entry : valsToVerify.entrySet()) {
                returnString ="So your ";
                if(entry.getKey().equals(Frame.SlotName.SHOE_COLOR)){
                    returnString += f.getSlot(Frame.SlotName.SHOE_TYPE).getValue() + " color is " + entry.getValue().toString().toLowerCase() + ", ";
                }
                if(entry.getKey().equals(Frame.SlotName.OUTERWEAR_ITEM_COLOR)){
                    returnString += f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).getValue() + " color is " + entry.getValue().toString().toLowerCase() + ", ";
                }
                if(entry.getKey().equals(Frame.SlotName.TOP_ITEM_COLOR)){
                    returnString += f.getSlot(Frame.SlotName.TOP_ITEM).getValue() + " color is " + entry.getValue().toString().toLowerCase() + ", ";
                }
                if(entry.getKey().equals(Frame.SlotName.BOTTOM_ITEM_COLOR)){
                    returnString += f.getSlot(Frame.SlotName.BOTTOM_ITEM).getValue()  + " color is " + entry.getValue().toString().toLowerCase() + ", ";
                }
            }
        }

        returnString += "Is that all correct?";
        WolfWearLogger.log(System.currentTimeMillis() + " requestMoreInformation (for verify) string was " + returnString + " ");

        return returnString;
    }

}