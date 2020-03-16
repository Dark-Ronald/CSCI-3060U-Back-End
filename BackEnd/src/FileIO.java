/*
Description: This classes purpose is to handle all of the different File IO needs of our
program. This does things like read in all of the different flies, the users file, read items file

This also handles printing out fatal errors like thrying to read in an unrecognized file

input: on a creation of an instance of FileIO, it requires being passed the file paths
that the program will read and write from
*/
import java.util.ArrayList;
import java.io.*;

public class FileIO{

    private static String userFilePath;
    private static String itemFilePath;
    private static String transactionFilePath;

    /* 
    Description: A simple constructor for the instance of FileIO
    input: userFilePath: the string path of where the users file to be read from is
           itemFilePath: the string path of where the items file to be read from is
           transactionFilePath: the string path of where the transactions file to be read from is
    output: none
    */
    public static void setPaths (String userFilePath, String itemFilePath, String transactionFilePath){
        FileIO.userFilePath = userFilePath;
        FileIO.itemFilePath = itemFilePath;
        FileIO.transactionFilePath = transactionFilePath;
    }

    public static ArrayList<String> dailyTransactionFile;
    public static ArrayList<String> currentUserAccounts;
    public static ArrayList<String> availableItems;

    /*
    Description: This function reads the mergedDailyTransaction file, currentUserAccounts file,
    and the availableItems file.
    The currentUserAccounts file is read into a list of user objects, and the availableItems
    file is read into a list of item objects
    input: users: a pointer to a user ArrayList
           items: a pointer to an Item ArrayList
    Outputs: a return of true or false, based on whether or not the read was succsesful
    */
    public static boolean readFiles(ArrayList<user> users, ArrayList<Item> items){

        return true;
    }

    /*
    Description: This program takes a list of users and items that has been updated with the daily
    transactions and writes them to the files instanciated at the classes creation
    Inputs: users: a list of type users that contains all of the info of every user
            items: a list of type items that contains all of the info on every item
    Outputs: true or false based on if all of the writes were succesful or not
    */
    public static boolean writeFiles(ArrayList<user> users, ArrayList<Item> items){
        
        return true;
    }

}