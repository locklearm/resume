package edu.ncsu.csc.wolfwear.logging;

import android.os.Environment;
import android.os.SystemClock;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Martin Locklear (melockle@ncsu.edu) on 10/28/2014.
 */
public class WolfWearLogger extends Thread {

    private static WolfWearLogger instance = null;
    private List<String> messagesToWrite;
    private boolean finish;
    private PrintWriter logPrinter;
    private static File logFolder;

    private WolfWearLogger() throws IOException {
        this.messagesToWrite = Collections.synchronizedList(new ArrayList<String>());
        this.finish = false;
        this.logPrinter = new PrintWriter(
//                new FileWriter(Environment.getExternalStorageDirectory() + "/wolfwearlog" + System.currentTimeMillis() + ".log", true)
//                new FileWriter(Environment.getDataDirectory() + "/wolfwearlog" + System.currentTimeMillis() + ".log", true);
                new FileWriter(new File(WolfWearLogger.logFolder, "wolfwearlog" + System.currentTimeMillis() + ".log"), true)
        );
        System.out.println("log file is " + WolfWearLogger.logFolder + "wolfwearlog" + System.currentTimeMillis() + ".log ...ish \n" );

    }

    public static void init(File logFolder) {
        WolfWearLogger.logFolder = logFolder;
    }

    private static WolfWearLogger getInstance() {
        if (WolfWearLogger.instance == null) {
            try {
                WolfWearLogger.instance = new WolfWearLogger();
            } catch (IOException e) {
                WolfWearLogger.instance = null;
                return null;
            }
            WolfWearLogger.instance.start();
        }
        return WolfWearLogger.instance;
    }

    public void run() {

        do {
            //try {
                //wait();
            SystemClock.sleep(4000);

                //Look for anything in the list.  If it's there, we save it
                while (!this.messagesToWrite.isEmpty()) {

                    String logLine = "";
                    logLine += "[" + System.currentTimeMillis() + "]\t";
                    logLine += this.messagesToWrite.remove(0);
                    this.logPrinter.println(logLine);

                }

//            } catch (InterruptedException e) {
//                //We don't need to do anything here, since this will just loop through
//                //an exception without losing data
//            }
        }
        while(!this.finish);

        this.logPrinter.flush();
        this.logPrinter.close();

    }

    public static void finish() {
        WolfWearLogger.getInstance().finish = true;
        //WolfWearLogger.getInstance().notify();
    }

    public static void log(String contents) {
        WolfWearLogger.getInstance().messagesToWrite.add(contents);
        //WolfWearLogger.getInstance().notify();

    }

}
