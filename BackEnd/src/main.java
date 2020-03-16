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

public class main {
    /*
    main loop of program
    input: String[] args: names of or paths to available_items, current_user_accounts, and
                          daily_transaction files
    output: None
     */
    public static void main(String[] args) {
        while(true) {
            //TODO
            //wake up

            //TODO
            //check for previous days file

            FileIO.readFiles(parser.currentUserAccounts, parser.availableItems);

            for (String line : FileIO.dailyTransactionFile) {
                //TODO
                //process line by calling parser function corresponding to transaction code

            }

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
    public void processDailyTransacionFile() {

    }

    /*
    creates and waits on sleep timer set for start of the next day
    input: None
    output: None
     */
    public void sleep() {

    }
}
