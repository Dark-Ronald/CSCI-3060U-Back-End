import java.util.ArrayList;
import java.util.Date;

public class parser {
    static ArrayList<user> currentUserAccounts = new ArrayList<user>();
    static ArrayList<Item> availableItems = new ArrayList<Item>();
    static Date datePreviouslyRun;

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
    static void advertise() {

    }
    static void bid() {

    }
    static void deleteUser() {
        /*
        if user being deleted is the highest bidder on an item, then when they are deleted
        the second highest bidder needs to be found and replace the deleted user in the item
        object.  This may involve reading previous dailyTransaction files
        */
    }
    static void refund() {

    }
}
