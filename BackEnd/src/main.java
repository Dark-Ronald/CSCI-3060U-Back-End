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

            FileIO.writeFiles(parser.currentUserAccounts, parser.availableItems);

            //TODO
            //sleep until midnight of current day
        }

    }

    public void checkPreviousDaysFile() {

    }

    public void processDailyTransacionFile() {

    }

    public void sleep() {

    }
}
