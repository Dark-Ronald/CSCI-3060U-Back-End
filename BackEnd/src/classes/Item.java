/*
Description: This is a simple class used for the storage and handling of an instance of
each item class, user the simple getter and setter methods, as well as the toString 
functionallity
Input: All of the different information nessesary for a single item
*/
package classes;

import java.util.ArrayList;
import java.util.Arrays;

public class Item {

    private String itemName;
    private String sellerName;
    private String highestBidderName;
    private double bidPrice;
    private short remaningDays;
    static ArrayList<Item> uniqueBidList = new ArrayList<Item>();

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
        this.bidPrice = Double.valueOf(bidPrice);
        this.remaningDays = Short.valueOf(remainingDays);
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
        return "item description";
    }


    /* 
    Description: Handles the bidding and modifies the available items array
    input: bidList array, available item array
    output: none
    */
    public void bid(ArrayList<Item> bidList, ArrayList<Item> availableItems){
        // Create an array of unique bids to compare bids

        //Loop through every bid from daily transaction file
        for (int i = 0; i < bidList.size(); i++){
            //Loop through the uniqueBidList
            for (int j = 0; j < uniqueBidList.size(); j++){
                // If both compareTo's are equal to zero then that means there has been a match
                // and bid is not unique so we have to compare bids and take the highest bid
                Item[] bidArray = bidList.toArray(new Item[bidList.size()]);
                Item[] uniqueArray = uniqueBidList.toArray(new Item[uniqueBidList.size()]);
                if (bidArray[i].getItemName().compareTo(uniqueArray[j].getItemName()) == 0 && bidArray[i].getSellerName().compareTo(uniqueArray[j].getSellerName()) == 0){
                    //compare the bids, need to convert both bids to double to compare
                    double compareBidList = 0.0;
                    double compareUniqueBidList = 0.0;

                    compareBidList = ((bidArray[i].getBidPrice());
                    compareUniqueBidList = (uniqueArray[i].getBidPrice());
                    if (compareBidList > compareUniqueBidList){
                        uniqueArray[j].setBidPrice(bidArray[i].getBidPrice());
                    }
                // otherwise it's unique so we add it to uniqueBidList array
                } else {
                    String strBidPrice = Double.toString(bidArray[i].getBidPrice());
                    uniqueBidList.add(new Item(bidArray[i].getItemName(), bidArray[i].getSellerName(), bidArray[i].getBidderName(), "0", strBidPrice));
                }
            }

        }
        //Merge the uniqueBidList array with the available items array
        Item[] completeUniqueArray = uniqueBidList.toArray(new Item[uniqueBidList.size()]);
        Item[] availableItemsArray = availableItems.toArray(new Item[availableItems.size()]);
        for (int z = 0; z < uniqueBidList.size(); z++){
            for (int k = 0; k < availableItems.size(); k++){
                if (completeUniqueArray[z].getItemName().compareTo(availableItemsArray[k].getItemName()) == 0 && completeUniqueArray[z].getSellerName().compareTo(availableItemsArray[k].getSellerName()) == 0){
                    availableItemsArray[k].setBidderName(completeUniqueArray[z].getBidderName());
                    availableItemsArray[k].setBidPrice(completeUniqueArray[z].getBidPrice());
                }
            }
        }
        //Convert availableItemsArray back to a list
        availableItems = Arrays.asList(availableItemsArray));
    }
}
