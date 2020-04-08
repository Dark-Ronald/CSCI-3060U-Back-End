package classes;

/*
Description: This is a simple class used for the storage and handling of an instance of
each item class, user the simple getter and setter methods, as well as the toString 
functionallity
Input: All of the different information nessesary for a single item
*/

public class Item {

    private String itemName;
    private String sellerName;
    private String highestBidderName;
    private double bidPrice;
    private short remaningDays;
    /*
    Description: A simple constructor for the class, storing all of the nessesary information
    Input: itemName: What the name of the item is, sellerName: the user putting the item up for auction
           buyerName: the current user with the highest bid, bidPrice: what the highest bid price
           currently is, remainingDays: the amount of days before the bidding period ends 
    Output: none
    */
    public Item(String itemName, String sellerName, String highestBidderName, String remainingDays, String bidPrice){
    	
    	this.itemName = itemName;
        this.sellerName = sellerName;
        this.highestBidderName = highestBidderName;
        
        try {
        	this.bidPrice = Double.valueOf(bidPrice);
        } catch (NumberFormatException e) {
        	//replace with print to error file
        	System.out.println("ERROR: Item Bid Price Is Not A Number.  Item Listing: " +
                    itemName + " " +
                    sellerName + " " +
                    highestBidderName + " " +
                    remainingDays + " " +
                    bidPrice);
        	throw new NumberFormatException();
        }
        
        try {
        	this.remaningDays = Short.valueOf(remainingDays);
        }catch (NumberFormatException e) {
        	//replace with print to error file
        	System.out.printf("ERROR: Item Days To Auction Is Not A Number.  Item Listing: " +
                    itemName + " " +
                    sellerName + " " +
                    highestBidderName + " " +
                    remainingDays + " " +
                    bidPrice);
        	throw new NumberFormatException();
        }
    }

    /*
    Description: returns the item name for the item
    input: none
    output: the current item name 
     */
    public String getItemName() {
        return itemName;
    }

    /*
    Description: returns the name of the user putting the item up for auction
    input: none
    output: the sellers userName
     */
    public String getSellerName() {
        return sellerName;
    }

    /*
    Description: returns the name of the current highest bidder
    input: none
    output: the current item name 
     */
    public String getBidderName() {
        return highestBidderName;
    }

    /*
    Description: returns the current highest bid
    input: none
    output: the highest bid
     */
    public double getBidPrice() {
        return bidPrice;
    }

    /*
    Description: gets the number of days left to bid on the item
    input: none
    output: the number of remaning days
     */
    public short getRemaningDays() {
        return remaningDays;
    }

    /*
    Description: sets a new item name to the item (should be a privlaged transaction)
    input: the new item name
    output: none 
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /*
    Description: changes the user who is selling the item(should be a privlaged transaction)
    input: the new sellers name
    output: none 
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /*
    Description: sets a new highest buyer for the item (should be a privlaged transaction)
    input: the new highest bidder
    output: none 
     */
    public void setBidderName(String bidderName) {
        this.highestBidderName = bidderName;
    }

    /*
    Description: sets a new bid price for the current item(should be a privlaged transaction)
    input: the highest bid price
    output: none 
     */
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    /*
    Description: subtracts one from the current amount of days remaining on the 
    bidding period for the item(should be a privlaged transaction)
    input: none
    output: none 
     */
    public void decrementRemaningDays() {
        this.remaningDays--;
    }

    /* 
    Description: returns a string representation of the item
    input: none
    output: the string description of the item
    */
    @Override
    public String toString() {
        return String.format("%s %s %s %09.2f %03d",
        					 this.itemName, 
        					 this.sellerName, 
        					 this.highestBidderName,
        					 this.bidPrice,
        					 this.remaningDays);
    }
    
    @Override
    public boolean equals(Object o) {
    	
    	if(o == this) {
    		return true;
    	}
    	
    	if(!(o instanceof Item)) {
    		return false;
    	}
    	Item other = (Item) o;
    	
    	if(!(this.itemName.equals(other.getItemName()))) return false;
    	if(!(this.sellerName.equals(other.getSellerName()))) return false;
    	if(!(this.highestBidderName.equals(other.getBidderName()))) return false;
    	if(this.bidPrice != other.getBidPrice()) return false;
    	if(this.remaningDays != other.getRemaningDays()) return false;
    	
    	return true;
    }
}
