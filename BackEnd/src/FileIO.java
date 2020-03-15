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

    private String userFilePath;
    private String itemFilePath;
    private String transactionFilePath;

    /* 
    Description: A simple constructor for the instance of FileIO
    input: userFilePath: the string path of where the users file to be read from is
           itemFilePath: the string path of where the items file to be read from is
           transactionFilePath: the string path of where the transactions file to be read from is
    output: none
    */
    public FileIO (String userFilePath, String itemFilePath, String transacitonFilePath){
        this.userFilePath = userFilePath;
        this.itemFilePath = itemFilePath;
        this.transactionFilePath = transacitonFilePath;
    }

    public static ArrayList<String> dailyTransactionFile;
    public static ArrayList<String> currentUserAccounts;
    public static ArrayList<String> availableItems;

    /*
    Description: This program takes the lists from the main program as reference then reads in all
    of the different files nessesary for the program, based on the file names given to the class
    on creation
    If this function detects a error in one of the descriptors within on of the files, it will pass
    upon that item, write an error, and proceed with the rest of the file
    Inputs: users: a refernced list of users, items: a referenced list of items, transactions: a referenced
    list of the transactions 
    Outputs: a return of true or false, based on whether or not the read was succsesful
    */
    public boolean readFiles(ArrayList<User> users, ArrayList<Item> items, ArrayList<String> transactions){

        return true;
    }

    /*
    Description: This program takes a list of users and items that has been updated with the daily
    transactions and writes them to the files instanciated at the classes creation
    Inputs: users: a list of type users that contains all of the info of every user
            items: a list of type items that contains all of the info on every item
    Outputs: true or false based on if all of the writes were succesful or not
    */
    public boolean writeFiles(ArrayList<User> users, ArrayList<Item> items){
        
        return true;
    }

}