package edu.ncsu.csc.wolfwear.natural_language_understanding;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.ncsu.csc.wolfwear.dialogue_management.Frame;
import edu.ncsu.csc.wolfwear.dialogue_management.UserSpeechPossibility;
import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;

/**
 * This class manages translating strings of words (that have been interpreted by google) into
 * the frame slots we have.
 *
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class FrameFiller {

    //ideally, this works for debugging via logs.
    private static final String TAG = "FrameFiller";

    //the minimum confidence level an utterance should have before the system will bother to check it
    double minimumConfidenceThreshold = .5;

    //slot the system is currently focusing on filling
    String slotToFill = null;

    //all possible interviewTypes
    List<String> interviewTypes = Arrays.asList("business attire", "business casual", "casual");

    //examples for general question what are you plan to wear
    List<String> generalExample = Arrays.asList("Black cardigan, white polo, black pants and purple shoes.");

    //all possible outerwear items
    List<String> outerwearItems = Arrays.asList("blazer", "cardigan",
            "hoodie",  "over coat", "peacoat", "sports jacket",
            "sweater", "sweatshirt", "trench coat", "coat", "jacket");

    //all possible top items
    List<String> topItems = Arrays.asList("blouse", "cami", "camisole",
            "cover up", "knit", "long sleeve shirt", "polo",
            "sleeveless", "sport shirt", "t-shirt", "tank top", "tank", "tee", "tunic", "vest", "shirt",
            "dress", "jump suit", "jumpsuit", "pajama",
            "robe", "tuxedo", "uniform");

    //all possible bottom items
    List<String> bottomItems = Arrays.asList("denim", "jeans", "leggings", "mini skirt",
            "shorts", "skirt", "slacks", "sport pants", "sweatpants", "tights", "trousers", "yoga pants", "pants",
            "dress", "jump suit", "jumpsuit", "pajama",
            "robe", "tuxedo", "uniform");

    //all possible whole body items
    List<String> wholeBodyItems = Arrays.asList("dress", "jump suit", "jumpsuit", "pajama",
            "robe", "tuxedo", "uniform");

    //all possible shoe item types
    List<String> shoeTypes = Arrays.asList( "comfortable shoes", "clogs", "flats",
            "flip flops", "galoshes", "heels", "loafers", "oxfords", "platform", "rain boots",
            "sandals", "slippers", "sneakers", "sport", "tennis shoes", "wedges", "boots", "shoes");

    //all possible colors
    List<String> itemColors = Arrays.asList("black", "blue", "brown", "green", "gray", "grey", "navy", "orange",
            "pink", "purple", "red", "tan", "white", "yellow",
            "crimson", "scarlet", "cardinal", "ruby", "lava", "flamingo", "carmine", "fire",
            "azure", "sky", "navy", "sapphire", "indigo", "celeste", "midnight","denim",
            "carrot", "apricot", "tangerine", "coral", "pumpkin", "sunset", "ginger","sunset",
            "gold", "amber", "maize", "buff", "lemon", "citron", "buff", "wheat","mustard",
            "snow", "ivory",  "pale", "pearl", "milk",
            "tan", "vanilla"," sand", "cream", "seashell",
            "charcoal", "licorice",
            "mint", "spring", "olive", "forest", "emerald", "tea", "jade", "grass",
            "silver", "ash", "smoky", "metallic", "taupe", "steel", "misty",
            "bronze", "earth", "caramel", "chestnut", "chocolate", "cafe", "coffee", "maroon", "wood",
            "violet", "orchid", "lavender", "plum", "wine",
            "raspberry", "rose", "magenta", "coral", "salmon"
    );

    //a hashmap to store all the frame slot / value pairs the frame filler came up with
    HashMap <Frame.SlotName, String> valuesToFill = new HashMap<Frame.SlotName, String>();

    /*
    * Looks over the user speech and determines if it can be used to fill any frame fields
    *
    * Fields to fill are:
        OUTERWEAR_ITEM,
        OUTERWEAR_ITEM_COLOR,
        TOP_ITEM,
        TOP_ITEM_COLOR,
        BOTTOM_ITEM,
        BOTTOM_ITEM_COLOR,
        SHOE_TYPE,
        SHOE_COLOR
    *
    */
    public HashMap <Frame.SlotName, String> fillFrame(List<UserSpeechPossibility> recognizedUtterances, Frame f) {
        //Stores what the user possibly said
        String possSaid;

        Log.d(TAG, "reached fillFrame method");

        //go through all recognized utterances, determine if worth parsing, then check for frame slot words
        for (UserSpeechPossibility possSpeech : recognizedUtterances) {
            //if confidence higher than threshold, we'll try looking through this utterance
            double confidence = possSpeech.getReportedConfidence();

            Log.d(TAG, "My confidence is " + confidence);

            //note that occasionally no confidence scores are returned, so values would default to 0
            if (confidence == 0.0 || confidence > minimumConfidenceThreshold) {
                possSaid = possSpeech.getUtterance();
                WolfWearLogger.log(System.currentTimeMillis() + " fillFrame method believes utterance was possibly " + possSaid + " \n");
            } else {
                //not enough confidence in speech to bother moving forward with this check
                WolfWearLogger.log(System.currentTimeMillis() + " fillFrame method was not confident enough to parse utterance \n");

                possSaid = null;
            }

            //if there is an utterance to check from this input, then start looking to check individual fields
            if (possSaid != null) {

                //check the interview type field
                if (!f.getSlot(Frame.SlotName.INTERVIEW_TYPE).isFilled()) {
                    //run helper method - check utterance for anything that looks like interview type
                    String couldFillWith = checkForItemFromList(possSaid, interviewTypes);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.INTERVIEW_TYPE, couldFillWith);
                    }
                }

                //check the outerwear field
                if (!f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).isFilled()) {
                    //run helper method - check utterance for anything that looks like outerwear items
                    String couldFillWith = checkForItemFromList(possSaid, outerwearItems);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.OUTERWEAR_ITEM, couldFillWith);
                    }
                }
                //if outerwear item exists, check for the outerwear color
                if ((f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).isFilled() || valuesToFill.containsKey(Frame.SlotName.OUTERWEAR_ITEM))
                    && !f.getSlot(Frame.SlotName.OUTERWEAR_ITEM_COLOR).isFilled()) {
                    //run helper method - check utterance for anything that looks like outerwear items
                    String associatedItem = f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).getValue();
                    if(f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).getValue() == null) {
                        associatedItem = valuesToFill.get(Frame.SlotName.OUTERWEAR_ITEM);
                    }
                    String couldFillWith = checkForColorFromList(possSaid, itemColors, associatedItem, f);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.OUTERWEAR_ITEM_COLOR, couldFillWith);
                    }
                }

                //check the top item field
                if (!f.getSlot(Frame.SlotName.TOP_ITEM).isFilled()) {
                    //run helper method - check utterance for anything that looks like top items
                    String couldFillWith = checkForItemFromList(possSaid, topItems);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.TOP_ITEM, couldFillWith);
                    }
                }
                //if top item exists, check for the top item color
                if ((f.getSlot(Frame.SlotName.TOP_ITEM).isFilled() || valuesToFill.containsKey(Frame.SlotName.TOP_ITEM))
                        && !f.getSlot(Frame.SlotName.TOP_ITEM_COLOR).isFilled()) {
                    //run helper method - check utterance for anything that looks like top item colors
                    String associatedItem = f.getSlot(Frame.SlotName.TOP_ITEM).getValue();
                    if(f.getSlot(Frame.SlotName.TOP_ITEM).getValue() == null) {
                        associatedItem = valuesToFill.get(Frame.SlotName.TOP_ITEM);
                    }
                    String couldFillWith = checkForColorFromList(possSaid, itemColors, associatedItem, f);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.TOP_ITEM_COLOR, couldFillWith);
                    }
                }

                //check the bottom item field
                if (!f.getSlot(Frame.SlotName.BOTTOM_ITEM).isFilled()) {
                    //run helper method - check utterance for anything that looks like bottom items
                    String couldFillWith = checkForItemFromList(possSaid, bottomItems);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.BOTTOM_ITEM, couldFillWith);
                    }
                }
                //if bottom item exists, check for the bottom item color
                if ((f.getSlot(Frame.SlotName.BOTTOM_ITEM).isFilled() || valuesToFill.containsKey(Frame.SlotName.BOTTOM_ITEM))
                        && !f.getSlot(Frame.SlotName.BOTTOM_ITEM_COLOR).isFilled()) {
                    //run helper method - check utterance for anything that looks like bottom item colors
                    String associatedItem = f.getSlot(Frame.SlotName.BOTTOM_ITEM).getValue();
                    if(f.getSlot(Frame.SlotName.BOTTOM_ITEM).getValue() == null) {
                        associatedItem = valuesToFill.get(Frame.SlotName.BOTTOM_ITEM);
                    }
                    String couldFillWith = checkForColorFromList(possSaid, itemColors, associatedItem, f);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.BOTTOM_ITEM_COLOR, couldFillWith);
                    }
                }


                //check the shoe type field
                if (!f.getSlot(Frame.SlotName.SHOE_TYPE).isFilled()) {
                    //run helper method - check utterance for anything that looks like shoe types
                    String couldFillWith = checkForItemFromList(possSaid, shoeTypes);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.SHOE_TYPE, couldFillWith);
                    }
                }
                //if shoe type exists, check for the shoe color
                if ((f.getSlot(Frame.SlotName.SHOE_TYPE).isFilled() || valuesToFill.containsKey(Frame.SlotName.SHOE_TYPE))
                        && !f.getSlot(Frame.SlotName.SHOE_COLOR).isFilled()) {
                    //run helper method - check utterance for anything that looks like shoe colors
                    String associatedItem = f.getSlot(Frame.SlotName.SHOE_TYPE).getValue();
                    if(f.getSlot(Frame.SlotName.SHOE_TYPE).getValue() == null) {
                        associatedItem = valuesToFill.get(Frame.SlotName.SHOE_TYPE);
                    }
                    String couldFillWith = checkForColorFromList(possSaid, itemColors, associatedItem, f);
                    if (couldFillWith != null) {
                        valuesToFill.put(Frame.SlotName.SHOE_COLOR, couldFillWith);
                    }
                }

            }
        }
        WolfWearLogger.log(System.currentTimeMillis() + " fillFrame method returns  " + valuesToFill.size() + " potential frame slot values \n");
        return valuesToFill;
    }

    //checks if the whole frame is full, and if so, reacts accordingly
    public void checkFrameFull(Frame f) {
        //if all the slots are filled, call the frame full.  This is ugly as sin, but should work.
        if (f.getSlot(Frame.SlotName.INTERVIEW_TYPE).isFilled() &&
                f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).isFilled() &&
                f.getSlot(Frame.SlotName.OUTERWEAR_ITEM_COLOR).isFilled() &&
                f.getSlot(Frame.SlotName.TOP_ITEM).isFilled() &&
                f.getSlot(Frame.SlotName.TOP_ITEM_COLOR).isFilled() &&
                f.getSlot(Frame.SlotName.BOTTOM_ITEM).isFilled() &&
                f.getSlot(Frame.SlotName.BOTTOM_ITEM_COLOR).isFilled() &&
                f.getSlot(Frame.SlotName.SHOE_TYPE).isFilled() &&
                f.getSlot(Frame.SlotName.SHOE_COLOR).isFilled()) {

            f.setFrameFull(true);
            WolfWearLogger.log(System.currentTimeMillis() + " checkFrameFull method believes frame is full \n");

        } else {
            f.setFrameFull(false);
            //set the slot we should focus next steps on
            slotToFill = determineSlotToFill(f);
            WolfWearLogger.log(System.currentTimeMillis() + " checkFrameFull method believes frame is not full, thinks next slot to fill is " + slotToFill + " \n");
        }
    }

    private String checkForColorFromList(String possSaid, List<String> itemColors, String associatedItem, Frame f) {
        //to store the potential matches we come across in list
        List<String> listMatches = new ArrayList<String>();

        //ideally, only matches very first instance of clothing article it finds in this group
        for (String itemColor : itemColors) {
            //pattern 1 = "red shirt"
            String pattern1ToMatch = "" + itemColor + "\\s" + associatedItem;
            Pattern pattern1 = Pattern.compile(pattern1ToMatch);
            Matcher matcher1 = pattern1.matcher(possSaid);

            //if we find at least one match on this pattern, add the color to the list of matches
            while (matcher1.find()) {
                listMatches.add(itemColor);
            }

            //pattern 2 = "shirt that is/are red"
            String pattern2ToMatch = "" + associatedItem + "\\sthat('s|\\sis|\\sare)\\s" + itemColor;
            Pattern pattern2 = Pattern.compile(pattern2ToMatch);
            Matcher matcher2 = pattern2.matcher(possSaid);

            //if we find at least one match on this pattern, add the color to the list of matches
            while (matcher2.find()) {
                listMatches.add(itemColor);
            }

            //pattern 3 = single word, attaches to current slot we're trying to fill
            if(slotToFill.equals("OUTERWEAR_ITEM_COLOR") ||
                slotToFill.equals("TOP_ITEM_COLOR") ||
                slotToFill.equals("BOTTOM_ITEM_COLOR") ||
                slotToFill.equals("SHOE_COLOR")) {
                //note that this can only work if this is the only word in the line
                String pattern3ToMatch = "^" + itemColor + "$";
                Pattern pattern3 = Pattern.compile(pattern3ToMatch);
                Matcher matcher3 = pattern3.matcher(possSaid);

                //if we find at least one match on this pattern, add the color to the list of matches
                while (matcher3.find()) {
                    listMatches.add(itemColor);
                }
            }

        }

        //if no matches were found, return null
        if (listMatches.size() == 0) {
            WolfWearLogger.log(System.currentTimeMillis() + " checkForColorFromList method believes no colors were found \n");
            return null;
        }
        //else return the first match we found
        WolfWearLogger.log(System.currentTimeMillis() + " checkForColorFromList method found the color " + listMatches.get(0) + " \n");
        return listMatches.get(0);
    }

    //checks to see if any of the items from this list were found in the string the user said
    private String checkForItemFromList(String possSaid, List<String> listOfItems) {
        //to store the potential matches we come across in list
        List<String> listMatches = new ArrayList<String>();

        //ideally, only matches very first instance of clothing article it finds in this group
        for (String listOfItem : listOfItems) {
            String patternToMatch = "" + listOfItem + "";
            //need this in instances where there are multiple words in target string
            String patternToMatchReplaced = patternToMatch.replace(" ", "\\s");

            Log.d(TAG, "Attempting to check for pattern: " + patternToMatchReplaced);

            Pattern pattern = Pattern.compile(patternToMatchReplaced);
            Matcher matcher = pattern.matcher(possSaid);

            while (matcher.find()) {
                listMatches.add(patternToMatch);

                Log.d(TAG, "Found a match for pattern: " + patternToMatchReplaced);
            }
        }

        //if no matches were found, return null
        if (listMatches.size() == 0) {
            WolfWearLogger.log(System.currentTimeMillis() + " checkForItemFromList method believes no items were found \n");
            return null;
        }
        //else return the first match we found
        Log.d(TAG, "Returning: " + listMatches.get(0));
        WolfWearLogger.log(System.currentTimeMillis() + " checkForItemFromList method found the item " + listMatches.get(0) + " \n");

        return listMatches.get(0);
    }

    //verifies that the value grabbed actually is what the user intended to put in the frame
    public boolean verifyFrameSelection(Frame f, HashMap<Frame.SlotName, String> valsToVerify, List<UserSpeechPossibility> recognizedUtterances) {

        //Stores what the user possibly said
        String possSaid;
        boolean completedVerify = false;

        Log.d(TAG, "reached verifyFrameSelection method");

        //go through all recognized utterances, determine if worth parsing, then check for frame slot words
        for (UserSpeechPossibility possSpeech : recognizedUtterances) {
            //if confidence higher than threshold, we'll try looking through this utterance
            double confidence = possSpeech.getReportedConfidence();

            Log.d(TAG, "My verify selection confidence is " + confidence);

            //note that occasionally no confidence scores are returned, so values would default to 0
            if (confidence == 0.0 || confidence > minimumConfidenceThreshold) {
                possSaid = possSpeech.getUtterance();
                WolfWearLogger.log(System.currentTimeMillis() + " verifyFrameSelection method believes utterance was possibly " + possSaid + " \n");
            } else {
                //not enough confidence in speech to bother moving forward with this check
                WolfWearLogger.log(System.currentTimeMillis() + " verifyFrameSelection method was not confident enough to parse utterance \n");
                Log.d(TAG, "I was not confident in verify selection.  Sad day.");

                possSaid = null;
            }

            //if there is an utterance to check from this input, check to see if it's yes
            if (possSaid != null) {
                String patternToMatch = "^yes$";

                Log.d(TAG, "Attempting to check verification selection for pattern: " + patternToMatch);

                Pattern pattern = Pattern.compile(patternToMatch);
                Matcher matcher = pattern.matcher(possSaid);

                while (matcher.find()) {
                    //The actual important bit where stuff is updated
                    Log.d(TAG, "Found a match for verification selection and pattern: " + patternToMatch);
                    completedVerify = true;
                    WolfWearLogger.log(System.currentTimeMillis() + " verifyFrameSelection method heard user say yes \n");
                    for (Map.Entry<Frame.SlotName, String> entry : valsToVerify.entrySet()) {
                        Frame.SlotName key = entry.getKey();
                        String value = entry.getValue();

                        f.getSlot(key).setValue(value);
                    }
                }

                patternToMatch = "^no$";

                Log.d(TAG, "Attempting to check for pattern: " + patternToMatch);

                pattern = Pattern.compile(patternToMatch);
                matcher = pattern.matcher(possSaid);

                while (matcher.find()) {
                    Log.d(TAG, "Found a match for pattern: " + patternToMatch);
                    completedVerify = true;
                    WolfWearLogger.log(System.currentTimeMillis() + " verifyFrameSelection method heard user say no \n");

                }
            }
        }
        if(!completedVerify) {
            WolfWearLogger.log(System.currentTimeMillis() + " verifyFrameSelection method did not hear a valid option \n");
        }
        return completedVerify;
    }



    //makes a string of all possible options for given named field
    public String optionsForFieldToString(Frame f, boolean inVerifyState) {
        String returnString = "";
        if(!inVerifyState) {
            List<String> listOfItems = interviewTypes;

            if (slotToFill.equals("INTERVIEW_TYPE")) {
                listOfItems = interviewTypes;
            }
            if (slotToFill.equals("ALL_BUT_INTERVIEW")){
                listOfItems = generalExample;
            }
            if (slotToFill.equals("OUTERWEAR_ITEM")) {
                listOfItems = outerwearItems;
            }
            if (slotToFill.equals("OUTERWEAR_ITEM_COLOR")) {
                listOfItems = itemColors;
            }
            if (slotToFill.equals("TOP_ITEM")) {
                listOfItems = topItems;
            }
            if (slotToFill.equals("TOP_ITEM_COLOR")) {
                listOfItems = itemColors;
            }
            if (slotToFill.equals("BOTTOM_ITEM")) {
                listOfItems = bottomItems;
            }
            if (slotToFill.equals("BOTTOM_ITEM_COLOR")) {
                listOfItems = itemColors;
            }
            if (slotToFill.equals("SHOE_TYPE")) {
                listOfItems = shoeTypes;
            }
            if (slotToFill.equals("SHOE_COLOR")) {
                listOfItems = itemColors;
            }

            for (String item : listOfItems) {
                returnString += "'" + item + "'      ";
            }
        } else { //is in verify state
            returnString = "'yes'      'no'";
        }
        WolfWearLogger.log(System.currentTimeMillis() + " optionsForFieldToString has return string of " + returnString + " \n");

        return returnString;
    }

    //sets the slotToFill value to be the next part of the frame that needs filling
    //logging for this will take place below parent call, since it's ugly to put it in here...
    public String determineSlotToFill(Frame f) {
        if(!f.getSlot(Frame.SlotName.INTERVIEW_TYPE).isFilled()) {
            return "INTERVIEW_TYPE";
        }

        if (f.onlyInterviewTypeFilled()){
            return "ALL_BUT_INTERVIEW";
        }
        else {
            if (!f.getSlot(Frame.SlotName.OUTERWEAR_ITEM).isFilled()) {
                return "OUTERWEAR_ITEM";
            }
            if (!f.getSlot(Frame.SlotName.OUTERWEAR_ITEM_COLOR).isFilled()) {
                return "OUTERWEAR_ITEM_COLOR";
            }
            if (!f.getSlot(Frame.SlotName.TOP_ITEM).isFilled()) {
                return "TOP_ITEM";
            }
            if (!f.getSlot(Frame.SlotName.TOP_ITEM_COLOR).isFilled()) {
                return "TOP_ITEM_COLOR";
            }
            if (!f.getSlot(Frame.SlotName.BOTTOM_ITEM).isFilled()) {
                return "BOTTOM_ITEM";
            }
            if (!f.getSlot(Frame.SlotName.BOTTOM_ITEM_COLOR).isFilled()) {
                return "BOTTOM_ITEM_COLOR";
            }
            if (!f.getSlot(Frame.SlotName.SHOE_TYPE).isFilled()) {
                return "SHOE_TYPE";
            }
            if (!f.getSlot(Frame.SlotName.SHOE_COLOR).isFilled()) {
                return "SHOE_COLOR";
            }
        }
        return null;
    }

}
