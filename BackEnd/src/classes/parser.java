package classes;

import java.util.ArrayList;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.Collections;

/*
utility class for parsing the lines of the daily_transaction file
 */
public class parser {
    protected static ArrayList<user> currentUserAccounts = new ArrayList<user>();
    protected static ArrayList<Item> availableItems = new ArrayList<Item>();
    protected static Date datePreviouslyRun;

    //used for testing purposes
    public static void addUsers(user... users) {
    	for(user newUser : users)
    		currentUserAccounts.add(newUser);
    }
    
    public static void clearUsers() {
    	currentUserAccounts.clear();
    }
    
    public static user searchUser(String name) {
    	for(user u: currentUserAccounts) {
    		if(u.getUsername().equals(name)) return u;
    	}
    	return null;
    }
    
    public static void addItems(Item... items) {
    	for(Item item : items)
    		availableItems.add(item);
    }
    
    public static void clearItems() {
    	availableItems.clear();
    }
    
    public static Item searchItem(String name) {
    	for(Item item: availableItems) {
    		if(item.getItemName().equals(name)) return item;
    	}
    	return null;
    }
    
    /*
    gets the user to add credit to, then sets their credit to the credit value in the
    transaction
    input: transaction: a line of the daily_transaction file with addcredit code
    output: None
     */
    public static void addCredit(String transaction) {
        String username = transaction.substring(3, 18);
        double credit = 0.0;
        
        try {
        	credit = Double.valueOf(transaction.substring(22, 31));
        } catch(NumberFormatException e) {
        	System.out.printf("Credit for addCredit transaction by user \"%s\" is not a number\n", username);
        	throw new NumberFormatException();
        }
        
        for (user userAccount : currentUserAccounts) {
            if (username.compareTo(userAccount.getUsername()) == 0) {
                userAccount.setCredit(credit + userAccount.getCredit());
                return;
            }
        }
        
        System.out.printf("User \"%s\" does not exist!\n", username);
        throw new NoSuchElementException();
    }

    /*
    adds an item to the availableItems list
    input: transaction: a line of the daily_transaction file with advertise code
    output: None
     */
    public static void advertise(String transaction) {
        String itemName = transaction.substring(3, 22);
        String sellerName = transaction.substring(23, 38);
        String daysToAuction = transaction.substring(39, 42);
        String minBid = transaction.substring(43, 50);
        String buyerName = "               ";
        
        availableItems.add(new Item(itemName, sellerName, buyerName, daysToAuction, minBid));
    }

    /*
    handles bid transaction
    input: transaction: a line of the daily_transaction file with bid code
    output: None
     */
    public static void bid(String transaction) {

    }

    /*
    adds user to currentUserAccounts list
    input: transaction: a line of the daily_transaction file with create code
    output: None
     */
    public static void create(String transaction) {
        String username = transaction.substring(3, 18);
        for (user user : currentUserAccounts) {
            if (user.getUsername().compareTo(username) == 0) {
                System.out.println("ERROR: Creation Of New User With Existing Name.  Transaction: " + transaction);
                throw new IllegalArgumentException();
            }
        }
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
    public static void deleteUser(String transaction) {
        /*
        if user being deleted is the highest bidder on an item, then when they are deleted
        the second highest bidder needs to be found and replace the deleted user in the item
        object.  This may involve reading previous dailyTransaction files
        */

        String username = transaction.substring(3, 18);
        ArrayList<Item> itemsBidOn = null;

        for (int i = 0; i < availableItems.size(); i++) {
            Item item = availableItems.get(i);
            if (item.getSellerName().compareTo(username) == 0) {
                availableItems.remove(i);
                i--;
            }
            else if (item.getBidderName().compareTo(username) == 0) {
                itemsBidOn.add(item);
            }
        }

        //find second highest bidder for each item in itemsBidOn
        ArrayList<String> secondBidder = new ArrayList<>(Collections.nCopies(itemsBidOn.size(), null));
        ArrayList<Double> secondBid = new ArrayList<>(Collections.nCopies(itemsBidOn.size(), null));
        /*
        due to data races the first bid (by a different user) found may not be the second
        highest bid, however any bid (by a different user) found is guaranteed to be higher
        than any bids 2 (executed) days prior
         */
        ArrayList<Integer> dayTracker = new ArrayList<>(Collections.nCopies(itemsBidOn.size(), null));

        boolean reset = true;
        ArrayList<String> oldTransactionFile;
        while (itemsBidOn.size() > 0) {
            oldTransactionFile = FileIO.getPreviousDTFs(reset);
            reset = false;
            for (int i = 0; i < dayTracker.size(); i++) {
                if (dayTracker.get(i) != null) {
                    dayTracker.set(i, dayTracker.get(i) + 1);
                    if (dayTracker.get(i) > 2) {
                        Item item = itemsBidOn.get(i);
                        item.setBidderName(secondBidder.get(i));
                        item.setBidPrice(secondBid.get(i));
                        itemsBidOn.remove(i);
                        secondBidder.remove(i);
                        secondBid.remove(i);
                        dayTracker.remove(i);
                        i--;
                    }
                }
            }
            for (int i = oldTransactionFile.size() - 1; i >= 0; i--) {
                String oldTransaction = oldTransactionFile.get(i);
                if (oldTransaction.substring(0, 2).compareTo("04") == 0) {
                    String itemName = oldTransaction.substring(3, 22);
                    String sellerName = oldTransaction.substring(23, 38);
                    String buyerName = oldTransaction.substring(39, 54);
                    Double bid = Double.valueOf(oldTransaction.substring(55, 61));

                    for (int j = 0; j < itemsBidOn.size(); j++) {
                        Item item = itemsBidOn.get(j);
                        if ((item.getItemName().compareTo(itemName) == 0) &&
                                (item.getSellerName().compareTo(sellerName) == 0) &&
                                (username.compareTo(buyerName) != 0)) {
                            if (secondBidder.get(j) == null) {
                                secondBidder.set(j, buyerName);
                                secondBid.set(j, bid);
                                dayTracker.set(j, 1);
                            }
                            else if (secondBid.get(j) <= bid) {
                                secondBidder.set(j, buyerName);
                                secondBid.set(j, bid);
                            }
                        }
                    }
                }
                else if (oldTransaction.substring(0, 2).compareTo("03") == 0) {
                    String itemName = oldTransaction.substring(3, 22);
                    String sellerName = oldTransaction.substring(23, 38);
                    double startingBid = Double.valueOf(oldTransaction.substring(43, 49));

                    for (int j = 0; j < itemsBidOn.size(); j++) {
                        Item item = itemsBidOn.get(j);
                        if ((item.getItemName().compareTo(itemName) == 0) &&
                                (item.getSellerName().compareTo(sellerName) == 0)) {
                            if (secondBidder.get(j) == null) {
                                item.setBidPrice(startingBid);
                                item.setBidderName("               ");
                            }
                            else {
                                item.setBidderName(secondBidder.get(j));
                                item.setBidPrice(secondBid.get(j));
                            }
                            itemsBidOn.remove(j);
                            secondBidder.remove(j);
                            secondBid.remove(j);
                            dayTracker.remove(j);
                            j--;
                        }
                    }
                }
            }
        }
    }

    /*
    refunds user
    input: transaction: a line of the daily_transaction file with create code
    output: None
     */
    public static void refund(String transaction) {
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
                            throw new IllegalArgumentException(); 
                        }
                        else if (buyerCredit + credit > 999999) {
                            System.out.println("ERROR: Refund Causes Buyers Credit To Exceed Maximum User Credit.  Transaction: " + transaction);
                            throw new IllegalArgumentException();
                        }
                        else {
                            sellerAccount.setCredit(sellerCredit - credit);
                            buyerAccount.setCredit(buyerCredit + credit);
                            return;
                        }
                    }
                }
                System.out.println("The Sellers username does not exist");
                throw new NoSuchElementException();
            }
        }
        System.out.println("The Buyers username does not exist");
        throw new NoSuchElementException();
    }

    public static void clean() {
        currentUserAccounts.clear();
        availableItems.clear();
    }
}
