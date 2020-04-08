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
import java.sql.Array;
import java.sql.Time;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class main {
    public static AtomicBoolean wakeup = new AtomicBoolean();
    public static AtomicBoolean shutdown = new AtomicBoolean();
    public static midnightTask task = new midnightTask();
    public static Timer midnightTimer = new Timer();
    public static AtomicBoolean newDay = new AtomicBoolean();
    /*
    main loop of program
    input: String[] args: names of or paths to available_items and current_user_accounts files.
                          Path to daily_transaction files.
    output: None
     */
    public static void main(String[] args) throws java.lang.InterruptedException {
        String transactionFilesPath = null;
        if (args.length == 3) {
            transactionFilesPath = args[2];
        }
        FileIO.setPaths(args[0], args[1], transactionFilesPath);

        shutdown.set(false);
        wakeup.set(false);

        Thread userInput = new Thread(new getUserInput());
        userInput.start();

        connectionHandler.init();
        setMidnightTimer();

        while(!shutdown.get()) {
            boolean sleep = true;
            boolean newDayFlag = false;
            if (FileIO.readFiles(parser.currentUserAccounts, parser.availableItems) && !newDay.get()) {

                processDailyTransactionFile();
                if (newDayFlag){
                    runAuctionDay(parser.currentUserAccounts, parser.availableItems);
                }
                FileIO.writeFiles(parser.currentUserAccounts, parser.availableItems);
                parser.clean();
            }
            else if (newDay.get() && !FileIO.fileComplete) {
                newDay.set(false);
                newDayFlag = true;
                sleep = false;
                synchronized (shutdown) {
                    while (!shutdown.get()) {
                        shutdown.wait(300000); //wait 5 minutes for front ends to write out file
                    }
                }
            }
            if (sleep) {
                synchronized (wakeup) {
                    while (!wakeup.get()) {
                        wakeup.wait();
                    }
                    wakeup.set(false);
                }
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

    private static void runAuctionDay(ArrayList<user> currentUserAccounts, ArrayList<Item> availableItems) {
        for (int i = 0; i < availableItems.size(); i++) {
            Item item = availableItems.get(i);
            if (item.getRemaningDays() == 0) {
                if (item.getBidderName().compareTo("               ") != 0) {
                    user seller = null;
                    user buyer = null;
                    for (user user : currentUserAccounts) {
                        if (user.getUsername().compareTo(item.getBidderName()) == 0) {
                            buyer = user;
                        }
                        if (user.getUsername().compareTo(item.getSellerName()) == 0) {
                            seller = user;
                        }

                        if ((seller != null) && (buyer != null)) {
                            break;
                        }
                    }
                    boolean transactionGood = true;
                    if (buyer.getCredit() < item.getBidPrice()) {
                        System.out.println("ERROR: Bidder Does Not Have Sufficient Funds.  Item Listing: " +
                                item.getItemName() + " " +
                                item.getSellerName() + " " +
                                item.getBidderName() + " " +
                                String.valueOf(item.getRemaningDays()) + " " +
                                String.format("%.2f", item.getBidPrice()));
                        transactionGood = false;
                    }
                    if (seller.getCredit() + item.getBidPrice() > 999999.00) {
                        System.out.println("ERROR: Item Purchase Causes Seller Credit To Exceed Maximum Credit Amount (999999.00).  Item Listing: " +
                                item.getItemName() + " " +
                                item.getSellerName() + " " +
                                item.getBidderName() + " " +
                                String.valueOf(item.getRemaningDays()) + " " +
                                String.format("%.2f", item.getBidPrice()));
                        transactionGood = false;
                    }

                    if (transactionGood) {
                        buyer.setCredit(buyer.getCredit() - item.getBidPrice());
                        seller.setCredit(seller.getCredit() + item.getBidPrice());
                    }
                }
                availableItems.remove(i);
                i--;
            }
            item.decrementRemaningDays();
        }
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
        synchronized (main.shutdown) {
            main.shutdown.set(true);
            main.shutdown.notify();
        }
        synchronized (main.wakeup) {
            main.wakeup.set(true);
            main.wakeup.notify();
        }
    }
}

class midnightTask extends TimerTask implements Runnable {
    public void run() {
        synchronized (main.wakeup) {
            main.wakeup.set(true);
            main.wakeup.notify();
        }
        main.setMidnightTimer();
    }
}
