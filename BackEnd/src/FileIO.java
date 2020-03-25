/*
Description: This classes purpose is to handle all of the different File IO needs of our
program. This does things like read in all of the different flies, the users file, read items file

This also handles printing out fatal errors like thrying to read in an unrecognized file
*/
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Comparator;

public class FileIO{

    private static String usersFilePath;
    private static String itemsFilePath;
    private static String transactionFilePath;
    public static ArrayList<String> dailyTransactionFile;
    private static ArrayList<Path> dTFList = new ArrayList<>();

    /* 
    Description: Function to set the paths to each of the files
    input: userFilePath: the string path of where the users file to be read from is
           itemFilePath: the string path of where the items file to be read from is
           transactionFilePath: the string path of where the transactions file to be read from is
    output: none
    */
    public static void setPaths (String userFilePath, String itemFilePath, String transactionFilePath){
        FileIO.usersFilePath = userFilePath;
        FileIO.itemsFilePath = itemFilePath;
        FileIO.transactionFilePath = transactionFilePath;
    }

    /*
    Description: This function reads the mergedDailyTransaction file, currentUserAccounts file,
    and the availableItems file.
    The currentUserAccounts file is read into a list of user objects, and the availableItems
    file is read into a list of item objects
    input: users: a pointer to a user ArrayList
           items: a pointer to an Item ArrayList
    Outputs: a return of true or false, based on whether or not the read was succsesful
    */
    public static void readFiles(ArrayList<user> users, ArrayList<Item> items){
        BufferedReader dailyTransactionFileReader = null;
        BufferedReader currentUserAccountsReader = null;
        String line;

        try {
            dailyTransactionFileReader = new BufferedReader(new FileReader(transactionFilePath));
        }
        catch(java.io.FileNotFoundException e) {
            System.out.println("ERROR: FileNotFoundException. File: " + transactionFilePath + " Not Found");
            System.exit(-1);
        }

        try {
            currentUserAccountsReader = new BufferedReader(new FileReader(usersFilePath));
        }
        catch(java.io.FileNotFoundException e) {
            System.out.println("ERROR: FileNotFoundException. File: " + usersFilePath + " Not Found");
            System.exit(-1);
        }

        try {
            BufferedReader availableItemsReader = new BufferedReader(new FileReader(itemsFilePath));

            while ((line = availableItemsReader.readLine()) != null) {
                if (line.compareTo("END                                                           ") == 0) {
                    break;
                }
                items.add(new Item(line.substring(0, 18), line.substring(20, 34), line.substring(36, 50), line.substring(52, 54), line.substring(56, 61)));
            }
            availableItemsReader.close();
        }
        catch(java.io.FileNotFoundException e) {
            //assume that lack of an items file means that this is the first time the backend is running
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Reading From File: " + itemsFilePath);
            System.exit(-1);
        }

        try {
            while ((line = dailyTransactionFileReader.readLine()) != null) {
                if (line.compareTo("00") == 0) {
                    break;
                }
                dailyTransactionFile.add(line);
            }
            dailyTransactionFileReader.close();
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Reading From File: " + dailyTransactionFile);
            System.exit(-1);
        }

        try {
            while ((line = currentUserAccountsReader.readLine()) != null) {
                if (line.compareTo("END                         ") == 0) {
                    break;
                }
                users.add(new user(line.substring(0, 14), line.substring(16, 17), line.substring(19, 27)));
            }
            currentUserAccountsReader.close();
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Reading From File: " + usersFilePath);
            System.exit(-1);
        }
    }

    /*
    Description: This program takes a list of users and items that has been updated with the daily
    transactions and writes them to the files instanciated at the classes creation
    Inputs: users: a list of type users that contains all of the info of every user
            items: a list of type items that contains all of the info on every item
    Outputs: true or false based on if all of the writes were succesful or not
    */
    public static void writeFiles(ArrayList<user> users, ArrayList<Item> items){
        String filePath = usersFilePath;
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(usersFilePath));
            for (user user : users) {
                writer.write(user.getUsername() + " " + user.getUserType() + " " + String.format("%.2f", user.getCredit()));
                writer.newLine();
            }
            writer.write("END                         ");
            writer.close();

            filePath = itemsFilePath;
            writer = new BufferedWriter(new FileWriter(itemsFilePath));
            for (Item item : items) {
                writer.write(item.getItemName() + " " + item.getSellerName() + " " + item.getBidderName() + " " + String.valueOf(item.getRemaningDays()) + " " + String.format("%.2f", item.getBidPrice()));
                writer.newLine();
            }
            writer.write("END                                                           ");
            writer.close();
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Writing To File: " + filePath);
            System.exit(-1);
        }
    }

    private static int dTFListI = 0;

    public static ArrayList<String> getPreviousDTFs(boolean reset) {
        if (reset) {
            dTFListI = 0;
            dTFList = new ArrayList<>();
            getDTFList();
        }
        if (dTFListI >= dTFList.size()) {
            return null;
        }
        ArrayList<String> file = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(dTFList.get(dTFListI).toFile()));

            String line;

            while ((line = reader.readLine()) != null) {
                file.add(line);
            }
            reader.close();
        }
        catch (java.io.FileNotFoundException e) {
            System.out.println("ERROR: FileNotFoundException. File: " + dTFList.get(dTFListI).getFileName().toString() + " Not Found");
            System.exit(-1);
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Reading From File: " + dTFList.get(dTFListI).getFileName().toString());
            System.exit(-1);
        }
        dTFListI ++;
        return file;
    }

    static class pathComparator implements Comparator<Path> {
        public int compare(Path p1, Path p2) {
            return - p1.getFileName().toString().compareTo(p2.getFileName().toString());
        }
    }

    private static void getDTFList() {
        Path currentTransactionFilePath = Paths.get(transactionFilePath).toAbsolutePath();
        Path transactionFilesDir = currentTransactionFilePath.getParent();
        for (Path p : transactionFilesDir) {
            if (p.compareTo(currentTransactionFilePath) == 0) {
                continue;
            }
            String fileName = p.getFileName().toString();
            int lI = fileName.lastIndexOf('.');
            if ((lI != -1) && (fileName.length() - 1 != lI)) {
                String extension = fileName.substring(lI);
                if (extension.compareTo("atf") == 0) {
                    dTFList.add(p);
                }
            }
        }

        dTFList.sort(new pathComparator());
    }
}