public class User {
    userTypes userType;
    String username;
    double credit;

    enum userTypes {
        ADMIN (0x0000),
        FULL_STANDARD (0x0001),
        BUY_STANDARD (0x0002),
        SELL_STANDARD (0x003);
        private final int type;
        userTypes(int type) {
            this.type = type;
        }
    }

    User(String username, String userType, String credit) {
        this.username = username;
        if (userType.compareTo("AA") == 0) {
            this.userType = userTypes.ADMIN;
        }
        else if (userType.compareTo("FS") == 0) {
            this.userType = userTypes.FULL_STANDARD;
        }
        else if (userType.compareTo("BS") == 0) {
            this.userType = userTypes.BUY_STANDARD;
        }
        else {
            this.userType = userTypes.SELL_STANDARD;
        }
        this.credit = Double.valueOf(credit);
    }
}
