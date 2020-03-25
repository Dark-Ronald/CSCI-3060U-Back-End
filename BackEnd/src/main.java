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

import java.util.ArrayList;

public class main {

    /*
    main loop of program
    input: String[] args: names of or paths to available_items and current_user_accounts files.
                          Path to daily_transaction files.
    output: None
     */
    public static void main(String[] args) {
        FileIO.setPaths(args[1], args[2], args[3]);



        while(true) {
            //TODO
            //wake up

            //TODO
            //check for previous days file

            FileIO.readFiles(parser.currentUserAccounts, parser.availableItems);

            processDailyTransactionFile();

            FileIO.writeFiles(parser.currentUserAccounts, parser.availableItems);

            //TODO
            //sleep until midnight of current day
        }

    }

    /*
    checks if the daily_transaction file of the previous day has been created, and if not then
    waits until it is created
    input: None
    output: None
     */
    public void checkPreviousDaysFile() {

    }

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

    /*
    creates and waits on sleep timer set for start of the next day
    input: None
    output: None
     */
    public void sleep() {

    }
}
