public class main {
    public static void main(String[] args) {
        while(true) {
            //TODO
            //wake up

            //TODO
            //check for previous days file

            FileIO.readFiles(parser.currentUserAccounts, parser.availableItems);

            for (String line : FileIO.dailyTransactionFile) {
                //TODO
                //process line by calling parser function corresponding to transaction code


            }

            //TODO
            //sleep until midnight of current day
        }

    }
}
