import java.util.ArrayList;
import java.util.Date;

/*
utility class for parsing the lines of the daily_transaction file
 */
public class parser {
    static ArrayList<user> currentUserAccounts = new ArrayList<user>();
    static ArrayList<Item> availableItems = new ArrayList<Item>();
    static Date datePreviouslyRun;

    /*
    gets the user to add credit to, then sets their credit to the credit value in the
    transaction
    input: transaction: a line of the daily_transaction file with addcredit code
    output: None
     */
    static void addCredit(String transaction) {
        String username = transaction.substring(3, 18);
        double credit = Double.valueOf(transaction.substring(23, 31));
        for (user userAccount : currentUserAccounts) {
            if (username.compareTo(userAccount.getUsername()) == 0) {
                userAccount.setCredit(credit);
                return;
            }
        }
    }

    /*
    adds an item to the availableItems list
    input: transaction: a line of the daily_transaction file with advertise code
    output: None
     */
    static void advertise(String transaction) {

    }

    /*
    handles bid transaction
    input: transaction: a line of the daily_transaction file with bid code
    output: None
     */
    static void bid(String transaction) {

    }

    /*
    adds user to currentUserAccounts list
    input: transaction: a line of the daily_transaction file with create code
    output: None
     */
    static void create(String transaction) {

    }

    /*
    removes user from currentUserAcoounts list, removes outstanding items user has up for
    auction (if any), and removes them from any item bids (if any) and replaces them with next
    highest bidder (if any)
    input: transaction: a line of the daily_transaction file with delete code
    output: None
     */
    static void deleteUser(String transaction) {
        /*
        if user being deleted is the highest bidder on an item, then when they are deleted
        the second highest bidder needs to be found and replace the deleted user in the item
        object.  This may involve reading previous dailyTransaction files
        */
    }

    /*
    refunds user
    input: transaction: a line of the daily_transaction file with create code
    output: None
     */
    static void refund(String transaction) {

    }
}
