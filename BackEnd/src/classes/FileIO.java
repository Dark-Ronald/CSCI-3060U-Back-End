/*
Description: This classes purpose is to handle all of the different File IO needs of our
program. This does things like read in all of the different flies, the users file, read items file

This also handles printing out fatal errors like thrying to read in an unrecognized file
*/

package classes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.io.*;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Comparator;

public class FileIO{

    private static String usersFilePath;
    private static String itemsFilePath;
    private static String transactionFilePath;
    private static Path currentTransactionFilePath = null;
    public static ArrayList<String> dailyTransactionFile;
    private static ArrayList<Path> dTFList = new ArrayList<>();
    private static int linesRead;

    /* 
    Description: Function to set the paths to each of the files
    input: userFilePath: the string path of where the users file to be read from is
           itemFilePath: the string path of where the items file to be read from is
           transactionFilePath: the string path of where the transactions files to be read from are
    output: none
    */
    public static void setPaths (String userFilePath, String itemFilePath, String transactionFilePath){
        FileIO.usersFilePath = userFilePath;
        FileIO.itemsFilePath = itemFilePath;
        if (transactionFilePath == null) {
            //TODO
            //set the correct default path?
            FileIO.transactionFilePath = "";
        }
        else {
            FileIO.transactionFilePath = transactionFilePath;
        }
    }

    /*
    Description: This function reads the mergedDailyTransaction file, currentUserAccounts file,
    and the availableItems file.
    The currentUserAccounts file is read into a list of user objects, and the availableItems
    file is read into a list of item objects
    input: users: a pointer to a user ArrayList
           items: a pointer to an Item ArrayList
    Outputs: true if there exists new data to process, false otherwise
    */
    public static boolean readFiles(ArrayList<user> users, ArrayList<Item> items){
        readMeta();
        if (!getDTFList()) {
            return false;
        }
        BufferedReader dailyTransactionFileReader = null;
        BufferedReader currentUserAccountsReader = null;

        dailyTransactionFile = new ArrayList<>();
        String line;

        try {
            dailyTransactionFileReader = new BufferedReader(new FileReader(currentTransactionFilePath.toFile()));
        }
        catch(java.io.FileNotFoundException e) {
            System.out.println("ERROR: FileNotFoundException. File: " + currentTransactionFilePath.getFileName().toString() + " Not Found");
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
                items.add(new Item(line.substring(0, 19), line.substring(20, 35), line.substring(36, 51), line.substring(52, 55), line.substring(56, 62)));
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
            linesRead = 0;
            while ((line = dailyTransactionFileReader.readLine()) != null) {
                if (line.compareTo("00") == 0) {
                    break;
                }
                if (linesRead >= lineLeftAt) {
                    dailyTransactionFile.add(line);
                }
                linesRead++;
            }
            dailyTransactionFileReader.close();
            if (linesRead == lineLeftAt) {
                currentUserAccountsReader.close();
                if (fileComplete) {
                    updateMeta();
                }
                return false; //stop processing of current file if it hasnt changed since it was last processed
            }
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
                users.add(new user(line.substring(0, 15), line.substring(16, 18), line.substring(19, 28)));
            }
            currentUserAccountsReader.close();
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Reading From File: " + usersFilePath);
            System.exit(-1);
        }
        return true;
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
                writer.write(user.getUsername() + " " + user.getUserType() + " " + pad(String.format("%.2f", user.getCredit()), '0', 9, true));
                writer.newLine();
            }
            writer.write("END                         ");
            writer.close();

            filePath = itemsFilePath;
            writer = new BufferedWriter(new FileWriter(itemsFilePath));
            for (Item item : items) {
                writer.write(item.getItemName() + " " + item.getSellerName() + " " + item.getBidderName() + " " + pad(String.valueOf(item.getRemaningDays()), '0', 3, true) + " " + pad(String.format("%.2f", item.getBidPrice()), '0', 6, true));
                writer.newLine();
            }
            writer.write("END                                                           ");
            writer.close();
        }
        catch (java.io.IOException e) {
            System.out.println("ERROR: IOException When Writing To File: " + filePath);
            System.exit(-1);
        }

        updateMeta();
    }

    //pads strings to conform to file formats.  side currently unused
    private static String pad(String s, char c, int len, boolean side) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len - s.length(); i++) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }

    private static int dTFListI = 0;
    private static int dTFListIDefault = 0; //Index of currentTransactionFilePath in dTFList

    public static ArrayList<String> getPreviousDTFs(boolean reset) {
        if (reset) {
            dTFList = new ArrayList<>();
            getDTFList();
            dTFListI = dTFListIDefault;
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

    /*
    returns false if there are no daily transaction files in transactionFilePath
     */
    private static boolean getDTFList() {
        Path transactionFilesDir = Paths.get(transactionFilePath).toAbsolutePath();
        int i = 0;
        dTFListIDefault = 0;
        for (File f : transactionFilesDir.toFile().listFiles()) {
            Path p = f.toPath();
            if ((currentTransactionFilePath != null) && (p.compareTo(currentTransactionFilePath) == 0)) {
                dTFListIDefault = i;
            }
            String fileName = p.getFileName().toString();
            int lI = fileName.lastIndexOf('.');
            if ((lI != -1) && (fileName.length() - 1 != lI)) {
                String extension = fileName.substring(lI + 1);
                if (extension.compareTo("atf") == 0) {
                    dTFList.add(p);
                    i++;
                }
            }
        }

        if (dTFList.size() == 0) {
            return false;
        }

        dTFList.sort(new pathComparator());
        if (currentTransactionFilePath == null) {
            currentTransactionFilePath = dTFList.get(0);
        }
        return true;
    }

    /*
    by default the transaction file to be processed is the most recent one, but at midnight the
    previous days transaction file must be processed

    public static void setTransactionFileToPreviousDays() {
        String date = main.today.toString() + ".atf";
        int i = 0;
        for (Path p : dTFList) {
            if (p.getFileName().toString().compareTo(date) < 0) {
                dTFListIDefault = i;
                dTFListI = i;
                currentTransactionFilePath = p;
                return;
            }
            i++;
        }
    }
    */
    private static int lineLeftAt;
    public static boolean fileComplete = false;

    private static void updateMeta() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("meta.inf"));
            if (!fileComplete) {
                writer.write(currentTransactionFilePath.getFileName().toString());
                writer.newLine();
                writer.write(String.valueOf(linesRead + lineLeftAt));
            }
            else {
                writer.write(dTFList.get(dTFListIDefault - 1).getFileName().toString());
                writer.newLine();
                writer.write("0");
            }
            writer.close();
        }
        catch (java.io.IOException e) {
            System.exit(-1);
        }
    }

    private static void readMeta() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("meta.inf"));
            currentTransactionFilePath = Paths.get(reader.readLine()).toAbsolutePath();
            lineLeftAt = Integer.valueOf(reader.readLine());
            reader.close();
            getDTFList();
            /*
            if there is a more recent file, then this will be the last time that the
            transaction file marked by the meta data will need to be processed
             */
            if (dTFListIDefault != 0) {
                fileComplete = true;
            }

        }
        catch (java.io.FileNotFoundException e) {
            currentTransactionFilePath = null;
            lineLeftAt = 0;
        }
        catch (java.io.IOException e) {
            System.exit(-1);
        }
    }
}