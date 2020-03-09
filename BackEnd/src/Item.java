public class Item {

    private String itemName;
    private String sellerName;
    private String buyerName;
    private double bidPrice;
    private short remaningDays; 

    public Item(String itemName, String sellerName, String buyerName, double bidPrice, short remainingDays){
        this.itemName = itemName;
        this.sellerName = sellerName;
        this.buyerName = buyerName;
        this.bidPrice = bidPrice;
        this.remaningDays = remainingDays;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @return the sellerName
     */
    public String getSellerName() {
        return sellerName;
    }

    /**
     * @return the buyerName
     */
    public String getBuyerName() {
        return buyerName;
    }

    /**
     * @return the bidPrice
     */
    public double getBidPrice() {
        return bidPrice;
    }

    /**
     * @return the remaningDays
     */
    public short getRemaningDays() {
        return remaningDays;
    }

    /**
     * @param itemName the itemName to set
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @param sellerName the sellerName to set
     */
    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    /**
     * @param buyerName the buyerName to set
     */
    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    /**
     * @param bidPrice the bidPrice to set
     */
    public void setBidPrice(double bidPrice) {
        this.bidPrice = bidPrice;
    }

    /**
     * 
     */
    public void decrementRemaningDays() {
        this.remaningDays--;
    }

    @Override
    public String toString() {
        return "item description";
    }
}
