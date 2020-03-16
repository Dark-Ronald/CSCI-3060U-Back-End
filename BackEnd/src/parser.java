import java.util.ArrayList;

public class parser {
    static ArrayList<user> currentUserAccounts = new ArrayList<user>();
    static ArrayList<Item> availableItems = new ArrayList<Item>();

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

    }
    static void refund() {

    }
}
