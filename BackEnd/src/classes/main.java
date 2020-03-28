/*
This program handles the back end functionality of the auction system.
input files: available_items.txt
             current_user_accounts.txt
             %Date formated name%.txt
output files: available_items.txt
              current_user_accounts.txt
Program is intended to be left running indefinitely, performing all functions at the start
of everyday, or when it is signaled to wake, then returning to sleep
 */
package classes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class main {
    public static AtomicBoolean wakeup;
    public static AtomicBoolean shutdown;
    public static midnightTask task = new midnightTask();
    public static Timer midnightTimer = new Timer();
    public static AtomicBoolean newDay;
    /*
    main loop of program
    input: String[] args: names of or paths to available_items and current_user_accounts files.
                          Path to daily_transaction files.
    output: None
     */
    public static void main(String[] args) throws java.lang.InterruptedException {
        String[] paths = new String[3];
        for (int i = 0; i < args.length - 1; i++) {
            paths[i] = args[i + 1];
        }
        FileIO.setPaths(paths[1], paths[2], paths[3]);

        shutdown.set(false);
        wakeup.set(false);

        Thread userInput = new Thread(new getUserInput());
        userInput.start();

        connectionHandler.init();
        setMidnightTimer();

        while(!shutdown.get()) {
            boolean sleep = true;
            if (FileIO.readFiles(parser.currentUserAccounts, parser.availableItems) && !newDay.get()) {

                processDailyTransactionFile();

                FileIO.writeFiles(parser.currentUserAccounts, parser.availableItems);
            }
            else if (newDay.get() && !FileIO.fileComplete) {
                sleep = false;
                shutdown.wait(300000); //wait 5 minutes for front ends to write out file
            }
            if (sleep) {
                wakeup.wait();
            }
        }
        midnightTimer.cancel();
        userInput.join();
        connectionHandler.shutdown();
    }

    /*
    checks if the daily_transaction file of the previous day has been created, and if not then
    waits until it is created
    input: None
    output: None

    public void checkPreviousDaysFile() {

    }
    */

    /*
    Processes the daily_transaction file by calling respective parser function for each
    transaction type
    input: None
    output: None
     */
    public static void processDailyTransactionFile() {
        for (String line : FileIO.dailyTransactionFile) {
            //process line by calling parser function corresponding to transaction code
            String code = line.substring(0, 2);
            if (code.compareTo("01") == 0) {
                parser.create(line);
            }
            else if (code.compareTo("02") == 0) {
                parser.deleteUser(line);
            }
            else if (code.compareTo("03") == 0) {
                parser.advertise(line);
            }
            else if (code.compareTo("04") == 0) {
                parser.bid(line);
            }
            else if (code.compareTo("05") == 0) {
                parser.refund(line);
            }
            else if (code.compareTo("06") == 0) {
                parser.addCredit(line);
            }
            else if (code.compareTo("00") != 0) {
                System.out.println("ERROR: Invalid Transaction Code.  Transaction: " + line);
            }
        }
    }

    public static void setMidnightTimer() {
        GregorianCalendar midnightTime = new GregorianCalendar();
        midnightTime.set(
                midnightTime.get(YEAR),
                midnightTime.get(MONTH),
                midnightTime.get(DAY_OF_MONTH) + 1,
                0,
                0,
                0
        );
        midnightTimer.schedule(task, midnightTime.getTime());
    }
}

class getUserInput implements Runnable{
    public void run() {
        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        String line;
        try {
            while ((line = stdin.readLine()).compareTo("shutdown") != 0) {
                //just keep reading until shutdown command
            }
        }
        catch (java.io.IOException e) {
            //do nothing
        }

        //shutdown whether shutdown command received or an exception occurred
        main.shutdown.set(true);
        main.shutdown.notify();
    }
}

class midnightTask extends TimerTask implements Runnable {
    public void run() {
        main.wakeup.notify();
        main.setMidnightTimer();
    }
}
