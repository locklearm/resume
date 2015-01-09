package edu.ncsu.csc.wolfwear.natural_language_generation;

import edu.ncsu.csc.wolfwear.dialogue_management.Frame;
import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Zhongxiu Liu (zliu24@ncsu.edu)
 * rules and generate recommendation based on whether items match the rules
 */
public class RecommendationGenerator {

    //collection of inappropriate items
    Hashtable <String, ArrayList<String>> badColors = new Hashtable<String, ArrayList<String>>();
    Hashtable <String, ArrayList<String>> appropriateItems = new Hashtable<String, ArrayList<String>>();
    ArrayList<String> controversyColors = new ArrayList<String>();
    Hashtable <String, String> humor = new Hashtable<String, String>();
    Hashtable<String, String> colorCategory = new Hashtable<String, String>();

    //ArrayList of all of the rules that have been violated
    ArrayList<String> ruleViolated = new ArrayList<String>();

    //ArrayLists of all of the color rules that have been violated
    ArrayList<String> badColorViolations = new ArrayList<String>();
    ArrayList<String> colorClashViolations = new ArrayList<String>();

    //ArrayLists of all humor
    ArrayList<String> humorList = new ArrayList<String>();

    //constructor, adds the rules to the above hashtables
    public RecommendationGenerator(){
        fillItemRules();
        fillColorRules();
        fillColorCategory();
        fillControversyColors();
        fillHumors();
    }

    public String makeRecommendationUtterance(Frame frame){

        String recommendation = "Uhm, let me see... ";
        //quick fix for whole body item
        ArrayList<String> itemColors = new ArrayList<String>(Arrays.asList(frame.getSlot(Frame.SlotName.TOP_ITEM_COLOR).getValue(),frame.getSlot(Frame.SlotName.BOTTOM_ITEM_COLOR).getValue(),
                frame.getSlot(Frame.SlotName.SHOE_COLOR).getValue(),frame.getSlot(Frame.SlotName.OUTERWEAR_ITEM_COLOR).getValue()));
        String interviewType = frame.getSlot(Frame.SlotName.INTERVIEW_TYPE).getValue();
        ArrayList<String> userItems = new ArrayList<String>(Arrays.asList(frame.getSlot(Frame.SlotName.OUTERWEAR_ITEM).getValue(),frame.getSlot(Frame.SlotName.TOP_ITEM).getValue(),
                frame.getSlot(Frame.SlotName.BOTTOM_ITEM).getValue(),frame.getSlot(Frame.SlotName.SHOE_TYPE).getValue() ));

        //check items to see if they are appropriate for the given interview
        if (!appropriateItems(interviewType, userItems)){ //if items were not appropriate, this if statement is triggered
            String inappropriateItems = "";

            //if more than one item violates a rule, format "X, Y, and Z are "
            if (ruleViolated.size() > 1) {
                //for violations 1 through n-1, make a comma separated list
                for (int i = 0; i < ruleViolated.size() - 1; i++){
                    inappropriateItems = inappropriateItems + ruleViolated.get(i) + ", ";
                }
                inappropriateItems = inappropriateItems + "and " + ruleViolated.get(ruleViolated.size() - 1) + " are ";
            } else { //only one item was inappropriate, format "X is "
                inappropriateItems = ruleViolated.get(0) + " is ";
            }

            //construct the recommendation
            recommendation = recommendation + "Your " + inappropriateItems  + "not appropriate for " + interviewType
                     + ". Consider dressing up a little bit. \n" ;

        }

        //check debatable colors - should be able to check this at the same time as rule violations, and parse both together.
        if (hasControversyColor(itemColors)){
            String badColorsString = "";

            //if more than one color violates a rule, format "X, Y, and Z"
            if (badColorViolations.size() > 1) {
                //for violations 1 through n-1, make a comma separated list
                for (int i = 0; i < badColorViolations.size() - 1; i++){
                    badColorsString = badColorsString + badColorViolations.get(i) + ", ";
                }
                badColorsString = badColorsString + "and " + badColorViolations.get(badColorViolations.size() - 1);
            } else { //only one item was inappropriate, format "X"
                badColorsString = badColorsString + badColorViolations.get(0);
            }

            if(!recommendation.equals("Uhm, let me see... ")) {
                recommendation = recommendation + " Moreover, there";
            } else {
                recommendation = recommendation + " There"; //without this, system actually says "dot dot dot" for ...
            }

             recommendation = recommendation + " are some colors you need to wear carefully, such as: " + badColorsString
                    + ". You want to look professional, not too bold. \n" ;

        }

        //check color matching
        if (!colorsMatch(itemColors)){
            String clashingColors = "";

            if (colorClashViolations.size()>1) {
                for (int i = 0; i < colorClashViolations.size() - 1; i++) {
                    clashingColors = clashingColors + colorClashViolations.get(i) + ", ";
                }
                clashingColors = clashingColors + " and " + colorClashViolations.get(colorClashViolations.size() - 1);
            }else{
                clashingColors = clashingColors + colorClashViolations.get(0);
            }

            if(!recommendation.equals("Uhm, let me see... ")) {
                recommendation = recommendation + " Lastly, ";
            }

            recommendation = recommendation + "be careful about colors that do not normally match well with each other. In your case: " + clashingColors
                    +". You should avoid the combination of these colors\n";
        }

        //if any combination of the above rules (bad items, bad colors, clashing colors) were triggered, return that now
        if(!recommendation.equals("Uhm, let me see... ")) {
            return recommendation;
        }

        //if nothing was triggered, check humor
        if (thereIsHumor(itemColors)){
            return recommendation + "I don't see any problems with what you are wearing. Just to make a quirky suggestion: " + humorList.get(0);
        }
        else {
            return recommendation + "I don't see any problems with what you are wearing. Wear a big smile, be confident, and have a successful interview!";
        }

    }

    //////////////////////////////////////
    /////////helper functions/////////////
    //////////////////////////////////////
    /**
     * checks whether items in the frame are appropriate for the types of interview
     * record inappropriate items into ruleViolated
     * @param interviewType
     * @param userItems
     * @return whether appropriate
     */
    private boolean appropriateItems(String interviewType, ArrayList<String> userItems){
        boolean itemsAppropriate = true;
        for (int i=0; i<userItems.size(); i++){
            if (!appropriateItems.get(interviewType).contains(userItems.get(i)) || appropriateItems.get("never").contains(userItems.get(i))) {
                WolfWearLogger.log(System.currentTimeMillis() + " appropriateItems method believes item " + userItems.get(i) + " was not appropriate. \n");
                if (!ruleViolated.contains(userItems.get(i))) {
                    ruleViolated.add(userItems.get(i));
                }
                itemsAppropriate = false;
            }
        }
        return itemsAppropriate;
    }

    /**
     * check if any item colors are controversy color -- non-traditional interview suit color that need to be wear carefully
     * add controversy colors into badColorViolations
     * @param itemColors
     * @return whether there's controversy color
     */
    private boolean hasControversyColor(ArrayList<String> itemColors){
        boolean hasControversyColor = false;
        for (int i=0; i<itemColors.size(); i++){
            if (controversyColors.contains(colorCategory.get(itemColors.get(i)))){
                if (!badColorViolations.contains(itemColors.get(i))){
                    badColorViolations.add(itemColors.get(i));
                    WolfWearLogger.log(System.currentTimeMillis() + " hasControversyColor method believes color " + itemColors.get(i) + " was controversial. \n");

                    hasControversyColor = true;
                }
            }
        }
        return hasControversyColor;
    }

    /**
     * check whether colors in the frame match
     * record pairs of colors don't match into ruleViolated
     * @param itemColors
     * @return whether colors match
     */
    private boolean colorsMatch(ArrayList<String> itemColors){
        boolean colorsMatch = true;
        for (int i=0; i<itemColors.size()-1; i++){
            for (int j=i+1; j<itemColors.size(); j++){
                String a = colorCategory.get(itemColors.get(i));
                String b = colorCategory.get(itemColors.get(j));

                if (badColors.get(colorCategory.get(itemColors.get(i))).contains(colorCategory.get(itemColors.get(j)))){
                    String rv1 = itemColors.get(i) + " and " + itemColors.get(j);
                    String rv2 = itemColors.get(j) + " and " + itemColors.get(i);
                    if (!colorClashViolations.contains(rv1) && !colorClashViolations.contains(rv2)){
                        WolfWearLogger.log(System.currentTimeMillis() + " colorsMatch method believes colors " + itemColors.get(i) + " and " + itemColors.get(j) + " clashed. \n");

                        colorClashViolations.add(itemColors.get(i) + " and " + itemColors.get(j));
                    }
                    colorsMatch = false;
                }
            }
        }
        return colorsMatch;
    }


    /**
     * need to decide on what are the humors we want to include before filling this function
     * @return
     */
    private boolean thereIsHumor(ArrayList<String> itemColors){
        boolean thereIsHumor = false;
        for (int i=0; i<itemColors.size(); i++){
            if (humor.get(itemColors.get(i))!=null){
                humorList.add(humor.get(itemColors.get(i)));
                WolfWearLogger.log(System.currentTimeMillis() + " thereIsHumor method believed itemColor " + itemColors.get(i) + " was humorous. \n");
                thereIsHumor = true;
            }
        }
        return thereIsHumor;
    }



    //////////////////////////////////////
    //////functions to fill in rules//////
    //////////////////////////////////////
    /**
     * rules for humor
     */
    private void fillHumors(){
        humor.put("white", "remember no white after Labor Day!");
    }

    /**
     * rules for appropriate items
     */
    private void fillItemRules(){
        ArrayList<String> businessAttire = new ArrayList<String>(Arrays.asList("suit", "blouse", "vest", "blazer", "slacks", "skirt","pants","tuxedo","dress", "flats", "heels", "shoes", "oxfords", "tie", "belt", "over coat",
                "trench coat", "over coat", "coat"));
        appropriateItems.put("business attire", businessAttire);

        ArrayList<String> businessCasual = new ArrayList<String>(Arrays.asList("suit", "blouse", "vest", "blazer", "slacks", "tuxedo","dress", "heels", "shoes", "oxford", "tie", "belt","oxfords",
                "sweater", "polo", "long sleeve shirt", "cardigan", "over coat","pants", "skirt", "trousers", "uniform", "flats", "boots", "wedges", "scarf", "over coat", "peacoat",
                "trench coat", "loafers","coat"));
        appropriateItems.put("business casual", businessCasual);

        ArrayList<String> casual = new ArrayList<String>(Arrays.asList("suit", "blouse", "vest", "blazer", "slacks", "tuxedo","dress", "flats", "heels", "shoes", "oxfords", "tie", "belt",
                "sweater", "polo", "long sleeve shirt", "tee","cardigan", "pants", "skirt", "trousers", "uniform", "flats", "boots", "sandals", "wedges", "scarf", "over coat", "peacoat", "trench coat", "loafers",
                "shirt", "t-shirt", "knit", "cover up", "tunic", "jacket", "over coat", "hoodie", "coat", "jeans", "denim", "tennis shoes",
                "sneakers", "rain boots", "clogs", "comfortable shoes", "hat", "sports jacket"));
        appropriateItems.put("casual", casual);

        ArrayList<String> never = new ArrayList<String>(Arrays.asList("tank top", "tank", "sleeveless", "sport","sport shirt", "sport pants","sweatshirt", "cami", "camisole", "shorts", "yoga pants", "leggings", "mini skirt",
                "sweatpants", "tights", "pajama", "jump suit", "robe", "flip flops", "platform", "galoshes", "slippers"));
        appropriateItems.put("never", never);
    }

    /**
     * colors that are controversy; we need to warn user about wearing these colors wisely
     */
    private void fillControversyColors(){
        controversyColors = new ArrayList<String>(Arrays.asList("orange", "pink", "purple", "red", "yellow"));
    }

    /**
     * this function use each color name as key, and associate color names with a color category as value
     */
    private void fillColorCategory() {

        ArrayList<String> red = new ArrayList<String>(Arrays.asList("red","crimson", "scarlet", "cardinal", "ruby", "lava", "flamingo", "carmine", "fire"));
        for (int i=0; i<red.size(); i++){
            colorCategory.put(red.get(i), "red");
        }

        ArrayList<String> blue = new ArrayList<String>(Arrays.asList("blue","azure", "sky", "navy", "sapphire", "indigo", "celeste", "midnight","denim"));
        for (int i=0; i<blue.size(); i++){
            colorCategory.put(blue.get(i), "blue");
        }

        ArrayList<String> orange = new ArrayList<String>(Arrays.asList("orange","carrot", "apricot", "tangerine", "coral", "pumpkin", "sunset", "ginger","sunset"));
        for (int i=0; i<orange.size(); i++){
            colorCategory.put(orange.get(i), "orange");
        }
        ArrayList<String> yellow = new ArrayList<String>(Arrays.asList("yellow","gold", "amber", "maize", "buff", "lemon", "citron", "buff", "wheat","mustard"));
        for (int i=0; i<yellow.size(); i++){
            colorCategory.put(yellow.get(i), "yellow");
        }
        ArrayList<String> white = new ArrayList<String>(Arrays.asList("white","snow", "ivory",  "pale", "pearl", "milk"));
        for (int i=0; i<white.size(); i++){
            colorCategory.put(white.get(i), "white");
        }
        ArrayList<String> beige = new ArrayList<String>(Arrays.asList("beige","tan", "vanilla"," sand", "cream", "seashell"));
        for (int i=0; i<beige.size(); i++){
            colorCategory.put(beige.get(i), "beige");
        }
        ArrayList<String> black = new ArrayList<String>(Arrays.asList("black","charcoal", "licorice" ));
        for (int i=0; i<black.size(); i++){
            colorCategory.put(black.get(i), "black");
        }
        ArrayList<String> green = new ArrayList<String>(Arrays.asList("green","mint", "spring", "olive", "forest", "emerald", "tea", "jade", "grass"));
        for (int i=0; i<green.size(); i++){
            colorCategory.put(green.get(i), "green");
        }
        ArrayList<String> grey = new ArrayList<String>(Arrays.asList("grey","gray","silver", "ash", "smoky", "metallic", "taupe", "steel", "misty"));
        for (int i=0; i<grey.size(); i++){
            colorCategory.put(grey.get(i), "grey");
        }
        ArrayList<String> brown = new ArrayList<String>(Arrays.asList("brown","bronze", "earth", "caramel", "chestnut", "chocolate", "cafe", "coffee", "maroon", "wood"));
        for (int i=0; i<brown.size(); i++){
            colorCategory.put(brown.get(i), "brown");
        }
        ArrayList<String> purple = new ArrayList<String>(Arrays.asList("purple","violet", "orchid", "lavender", "plum", "wine"));
        for (int i=0; i<purple.size(); i++){
            colorCategory.put(purple.get(i), "purple");
        }
        ArrayList<String> pink = new ArrayList<String>(Arrays.asList("pink","raspberry", "rose", "magenta", "coral", "salmon"));
        for (int i=0; i<pink.size(); i++){
            colorCategory.put(pink.get(i), "pink");
        }

    }


    /**
     * this function associate each color with a link list contains
     * - colors it doesn't match with
     */
    private void fillColorRules(){

        ArrayList<String> red = new ArrayList<String>(Arrays.asList("pink", "green", "blue", "orange", "purple", "yellow"));
        badColors.put("red", red);

        ArrayList<String> blue = new ArrayList<String>(Arrays.asList("pink", "green", "orange", "purple", "red", "yellow", "brown"));
        badColors.put("blue", blue);

        ArrayList<String> yellow = new ArrayList<String>(Arrays.asList("black", "blue", "green", "pink", "purple", "red", "brown"));
        badColors.put("yellow", yellow);

        ArrayList<String> orange = new ArrayList<String>(Arrays.asList("black", "green", "pink", "purple", "red", "yellow"));
        badColors.put("orange", orange);

        ArrayList<String> white = new ArrayList<String>(Arrays.asList(""));
        badColors.put("white", white);

        ArrayList<String> black = new ArrayList<String>(Arrays.asList("yellow", "orange"));
        badColors.put("black", black);

        ArrayList<String> green = new ArrayList<String>(Arrays.asList("red", "orange", "pink", "purple", "red"));
        badColors.put("green", green);

        ArrayList<String> gray = new ArrayList<String>(Arrays.asList(""));
        badColors.put("grey", gray);

        ArrayList<String> navy = new ArrayList<String>(Arrays.asList(""));
        badColors.put("navy", navy);

        ArrayList<String> brown = new ArrayList<String>(Arrays.asList("blue", "yellow", "pink", "purple"));
        badColors.put("brown", brown);

        ArrayList<String> purple = new ArrayList<String>(Arrays.asList("brown", "red", "blue", "yellow", "orange", "pink"));
        badColors.put("purple", purple);

        ArrayList<String> tan = new ArrayList<String>(Arrays.asList(""));
        badColors.put("tan", tan);

        ArrayList<String> beige = new ArrayList<String>(Arrays.asList(""));
        badColors.put("beige", beige);

        ArrayList<String> pink = new ArrayList<String>(Arrays.asList("brown", "green", "yellow", "orange", "red", "blue"));
        badColors.put("pink", pink);

    }


}


