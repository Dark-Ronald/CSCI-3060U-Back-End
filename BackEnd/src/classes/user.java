package classes;

/*
class to represent a user of the auction system
 */
public class user {
    //userTypes userType;
    private String userType;
    private String username;
    private double credit;
    /*
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
    */

    /*
    user constructor
    input: username: the username of the user
           userType: the type of the user
           credit: the credit of the user
    output: this
     */
    public user(String username, String userType, String credit) {
        this.username = username;
        /*
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
        */
        this.userType = userType;
        
        try {
        	this.credit = Double.valueOf(credit);
        } catch (NumberFormatException e) {
        	System.out.printf("The credit passed by user \"%s\" was not a number\n", username);
        	throw new NumberFormatException();
        }
    }

    /*
    get the credit of the user
    input: None
    output: credit: the credit of the user
     */
    public double getCredit() {
        return credit;
    }

    /*
    set the credit of the user
    input: credit: the credit amount to set the users credit to
    output: None
     */
    public void setCredit(double credit) {
        this.credit = credit;
    }

    /*
    get the username of the user
    input: None
    output: username: the username of the user
     */
    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }
    
    @Override
    public String toString() {
    	return String.format("%s %s %09.2f",
    						  this.username,
    						  this.userType,
    						  this.credit);
    }
    
    @Override
    public boolean equals(Object o) {
    	
    	if(o == this) return true;
    	
    	if(!(o instanceof user)) return false;
    	
    	user other = (user) o;
    	
    	if(!(this.username.equals(other.getUsername()))) return false;
    	if(!(this.userType.equals(other.getUserType()))) return false;
    	if(this.credit != other.getCredit()) return false;
    	
    	return true;
    }
}
