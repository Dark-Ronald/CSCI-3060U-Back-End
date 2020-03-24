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
        double credit = Double.valueOf(transaction.substring(23, 32));
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
        String itemName = transaction.substring(3, 22);
        String sellerName = transaction.substring(23, 36);
        String daysToAuction = transaction.substring(37, 40);
        String minBid = transaction.substring(41, 47);
        String buyerName = "               ";

        availableItems.add(new Item(itemName, sellerName, buyerName, daysToAuction, minBid));
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
        String username = transaction.substring(3, 18);
        String userType = transaction.substring(19, 21);
        String credit = transaction.substring(22, 31);

        currentUserAccounts.add(new user(username, userType, credit));
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
        String buyerName = transaction.substring(3, 18);
        String sellerName = transaction.substring(19, 34);
        double credit = Double.valueOf(transaction.substring(35, 43));

        for (user buyerAccount : currentUserAccounts) {
            if (buyerName.compareTo(buyerAccount.getUsername()) == 0) {
                for (user sellerAccount : currentUserAccounts) {
                    if (sellerName.compareTo(sellerAccount.getUsername()) == 0) {
                        double buyerCredit = buyerAccount.getCredit();
                        double sellerCredit = sellerAccount.getCredit();
                        if (sellerCredit - credit < 0) {
                            System.out.println("ERROR: Seller Doesn't Have Enough Credit For Refund.  Transaction: " + transaction);
                            return;
                        }
                        else if (buyerCredit + credit > 999999) {
                            System.out.println("ERROR: Refund Causes Buyers Credit To Exceed Maximum User Credit.  Transaction: " + transaction);
                            return;
                        }
                        else {
                            sellerAccount.setCredit(sellerCredit - credit);
                            buyerAccount.setCredit(buyerCredit + credit);
                        }
                    }
                }
            }
        }
    }
}
