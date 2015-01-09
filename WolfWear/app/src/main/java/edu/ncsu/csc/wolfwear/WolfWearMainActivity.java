package edu.ncsu.csc.wolfwear;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;

import edu.ncsu.csc.wolfwear.dialogue_management.DialogueManager;
import edu.ncsu.csc.wolfwear.dialogue_management.UserInterfaceManager;
import edu.ncsu.csc.wolfwear.dialogue_management.UserSpeechPossibility;
import edu.ncsu.csc.wolfwear.logging.WolfWearLogger;

import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class WolfWearMainActivity extends Activity implements View.OnClickListener,  OnInitListener, OnUtteranceCompletedListener {

    /**
     * This is the text to speech engine (I think)
     */
    private TextToSpeech tts;

    //ideally, this works for debugging via logs.
    private static final String TAG = "WolfWearMainActivity";

    //so we can say 'can you repete' instead of read same long sentence again
    private String preWords;

    /**
     * This is the first method that is called when the activity is
     * created. (Sort of like it's constructor)
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wolf_wear_main);
        findViewById(R.id.speechButton).setOnClickListener(this);
        tts = new TextToSpeech(this,this);

//        WolfWearLogger.init(getFilesDir());
//        Toast.makeText(this, getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG).show();
        WolfWearLogger.init(Environment.getExternalStorageDirectory());
//        Toast.makeText(this, Environment.getExternalStorageDirectory().getAbsolutePath(), Toast.LENGTH_LONG).show();
        WolfWearLogger.log(System.currentTimeMillis() + " Application Started\n");

        //Initializes our app and lets it know about this activity
        DialogueManager.init(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.wolf_wear_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Updates the main text box in the app
     * @param text The text to display
     */
    public void updateMainTextField(String text) {

        //Find the text view we are interested in
        TextView tv = (TextView) findViewById(R.id.mainTextDisplayTextView);

        //Update the text
        tv.setText(text);
    }

    /**
     * Updates the options text box in the app
     * @param text The text to display
     */
    public void updateOptionsTextField(String text) {

        //Find the text view we are interested in
        TextView tv = (TextView) findViewById(R.id.optionsTextDisplayTextView);

        //Update the text
        tv.setText(text);
    }

    /**
     * Updates the "app heard" text box in the app
     * @param text The text to display
     */
    public void updateAppHeardTextField(String text) {

        //Find the text view we are interested in
        TextView tv = (TextView) findViewById(R.id.optionsAppHeardTextView);

        //Update the text
        tv.setText(text);
    }

    /**
     * Updates the frame state text box in the app
     * @param text The text to display
     */
    public void updateFrameStateTextField(String text) {

        //Find the text view we are interested in
        TextView tv = (TextView) findViewById(R.id.optionsFrameStateTextView);

        //Update the text
        tv.setText(text);
    }

    /**
     * Changes the text of the button the user uses.
     * @param text  The text for the button label.
     */
    public void setButtonText(String text) {

        Button b = (Button) findViewById(R.id.speechButton);
        b.setText(text);

    }

    /**
     * Uses the text to speech engine to speak the given
     * string to the user
     * @param words The words or phrase(s) to speak.
     */
    public void speakWords(String words) {
        words = words.replaceAll("_"," ");
        HashMap<String, String> myHash = new HashMap<String, String>();

        if (words.equals(preWords)){
            WolfWearLogger.log(System.currentTimeMillis() + " System started Speaking the utterance: "+"Please repeat" + "\n");
            tts.speak("Please repeat", TextToSpeech.QUEUE_FLUSH, myHash);
        }
        else {
            changeBtnTextOnCondition(words);

            //user onUtterance complete to log end of sentence
            if (words.contains("Thank you for using WolfWear")) {
                myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "completeConversation");
            } else {
                myHash.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "completeUtterance");
            }

            //log start time and speak
            WolfWearLogger.log(System.currentTimeMillis() + " System started Speaking the utterance: " + words + "\n");
            tts.speak(words, TextToSpeech.QUEUE_FLUSH, myHash);
            preWords = words;
        }
    }

    /**
     * change button text to give user better instruction in the start/end of conversation
     */
    public void changeBtnTextOnCondition(String words){
        Button btn = (Button)findViewById(R.id.speechButton);
        if (words.contains("tell me what type of interview you are going to")){
            btn.setText("Tell me");
        }
        else if(words.contains("Uhm, let me see")){
            btn.setText("Finish");
        }
    }


    /**
     * Handles when the user clicks something
     * @param view
     */
    @Override
    public void onClick(View view) {

        //Check that the view is the button we want.  We do nothing otherwise
        if (view.getId() == R.id.speechButton) {

            WolfWearLogger.log(System.currentTimeMillis() + " User pressed button\n");

            Button btn = (Button)findViewById(R.id.speechButton);
            if (btn.getText().equals("Finish")){
                updateMainTextField("Thank you for using WolfWear. Hope you have a successful interview! \n" +"Goodbye!");
                updateOptionsTextField("");
                speakWords("Thank you for using WolfWear. Hope you have a successful interview! \nGoodbye!");
            }
            else {
                //Not entirely sure how all the rest of this works, but here it is
                Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                i.putExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES, (float[]) null);
                try {
                    startActivityForResult(i, 1);  //TODO Get rid of this magiv number
                } catch (Exception e) {
                    Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
                    WolfWearLogger.log(System.currentTimeMillis() + " Error initializing speech to text engine. \n");

                }
            }

        }

    }

    /**
     * Handles when the speech api returns what it thinks has been said
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected  void onActivityResult(int requestCode, int resultCode, Intent data) {

        float[] myConfidences = {0};
        //Also, not sure what all of this does, but here it is
        try {
            if (data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES) != null) {
                myConfidences = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);

                ArrayList<String> possibleUtterances = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                for (String utterance : possibleUtterances) {
                    Log.d(TAG, "potential utterance was:" + utterance);
                    updateAppHeardTextField("I heard you say: " + utterance);
                    WolfWearLogger.log(System.currentTimeMillis() + " system heard the user say: " + utterance + " ");

                }

                ArrayList<UserSpeechPossibility> userSpeechPossibilities = new ArrayList<UserSpeechPossibility>(myConfidences.length);
                for (int i = 0; i < myConfidences.length; i++) {
                    userSpeechPossibilities.add(new UserSpeechPossibility(possibleUtterances.get(i), myConfidences[i]));
                }

                //Now that we have collected the results, let the dialogue manager know about it
                String recommendation = DialogueManager.getInstance().reportUserSpeech(userSpeechPossibilities);
                String frameFieldsString = DialogueManager.getInstance().getFrame().frameFieldsToString();
                String optionsFieldString = DialogueManager.getInstance().getFrameFiller().optionsForFieldToString(DialogueManager.getInstance().getFrame(), DialogueManager.getInstance().getInVerifyState());

                updateMainTextField(recommendation);
                speakWords(recommendation);
                updateFrameStateTextField(frameFieldsString);
                updateOptionsTextField(optionsFieldString);

                //Toast.makeText(this, recommendation, Toast.LENGTH_LONG).show();
                }
        } catch (Exception e) {
            WolfWearLogger.log(System.currentTimeMillis() + "Error in processing previous utterance.  Please try again.\n ");
            Log.d(TAG, "Error in processing previous utterance.  Please try again.");
        }
    }

    /**
     * Checks to make sure that the text to speech engine loaded properly
     * @param code
     */
    @Override
    public void onInit(int code) {
        if (code == TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            tts.setOnUtteranceCompletedListener(this);
        } else {
            tts = null;
            Toast.makeText(this, "Failed to initialize TTS engine.", Toast.LENGTH_SHORT).show();
            WolfWearLogger.log(System.currentTimeMillis() + "Failed to initialize TTS engine. \n");

        }
        //utter a greeting
        speakWords("Welcome to WolfWear!  My name is Fashionastoria, and I am your personal stylist to help you dress right " +
                "for your interview. \nFirst, I'd like to get to know you.  Can you click the start button and " +
                "tell me a single word that describes you?");

    }

    /**
     * handle when an utterance's speaking is complete
     */
    @Override
    public void onUtteranceCompleted(String uttId) {
        if (uttId.equalsIgnoreCase("completeUtterance")) {
            WolfWearLogger.log(System.currentTimeMillis() + " System Completed Speaking the Utterance ");
        }
        else if(uttId.equalsIgnoreCase("completeConversation")) {
            WolfWearLogger.log(System.currentTimeMillis() + " System Completed Speaking the Last Sentense ");
            this.finish();
        }
    }

    /**
     * This needs to be here to make sure that everything closes properly
     */
    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }

        WolfWearLogger.log(System.currentTimeMillis() + " Application closed\n");
        WolfWearLogger.finish();

        super.onDestroy();
    }

}
