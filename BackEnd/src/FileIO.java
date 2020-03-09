/*
Description: This classes purpose is to handle all of the different File IO needs of our
program. This does things like read in all of the different flies, the users file, read items file

*/
import java.util.ArrayList;
import java.io.*;

public class FileIO{

    /*private String userFilePath;
    private String itemFilePath;
    private String transactionFilePath;

    public FileIO (String userFilePath, String itemFilePath, String transacitonFilePath){

    }*/

    public static ArrayList<String> dailyTransactionFile;
    public static ArrayList<String> currentUserAccounts;
    public static ArrayList<String> availableItems;

    public static boolean readFiles(ArrayList<user> users, ArrayList<Item> items, ArrayList<String> transactions){

        return true;
    }

    public static boolean writeFiles(ArrayList<user> users, ArrayList<Item> items){
        
        return true;
    }

    public static boolean readUsersFile(ArrayList<user> users){

        return true;
    }

    public static boolean readItemsFile(ArrayList<Item> items){

        return true;
    }

    public static boolean readTransactionFile(ArrayList<String> transacitons){

        return true;
    }


    public static boolean updateUsersFile(ArrayList<user> users){

        return true;
    }

    public static boolean updateItemsFile(ArrayList<Item> items){
        
        return true;
    }

}