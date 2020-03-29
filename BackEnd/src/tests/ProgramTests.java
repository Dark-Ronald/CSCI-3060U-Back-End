package tests;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;

import classes.*;

@TestMethodOrder(OrderAnnotation.class)
public class ProgramTests{
	
	/*
	 * ---------------------------------
	 * STATEMENT COVERAGE FOR ADVERTISE
	 * ---------------------------------
	 */
	
	/*
	 Test to see if advertise works properly
	 */
	@Test
	@Order(1)
	public void testAdvertise() {
		Item item = new Item("Desk Lamp          ", "UserOne        ", "               ", "004", "0099.99");
		
		String transaction = "03 Desk Lamp           UserOne         004 0099.99";
		parser.advertise(transaction);
		
		Assertions.assertTrue(item.equals(parser.searchItem(item.getItemName())));
		parser.clearItems();
	}
	
	/*
	 Test for if wrong type is sent for price of item
	 */
	@Test
	@Order(2)
	public void wrongPriceValueForAdvertise() {
		String transaction = "03 Desk Lamp           UserOne         004 OO99.99";
		Assertions.assertThrows(NumberFormatException.class, () -> parser.advertise(transaction));
	}
	
	/*
	 Test for if wrong type is sent for price of item
	 */
	@Test
	@Order(3)
	public void wrongDayValueForAdvertise() {
		String transaction = "03 Desk Lamp           UserOne         OO4 0099.99";
		Assertions.assertThrows(NumberFormatException.class, () -> parser.advertise(transaction));
	}
	
	
	/*
	 * ---------------------------------
	 * STATEMENT COVERAGE FOR ADDCREDIT
	 * ---------------------------------
	 */
	
	/*
	  Test to see if addCredit works properly
	*/
	@Test
	@Order(4)
	public void testAddCredit() {
		user temp = new user("TestUser       ", "FS", "100.00");
		parser.addUsers(temp);
		
		String transaction = "06 TestUser        FS 000200.00";
		parser.addCredit(transaction);
		Assertions.assertEquals(300.00, parser.searchUser("TestUser       ").getCredit());
		
		parser.clearUsers();
	}
	
	//Test to see if addCredit properly handles invalid input
	@Test 
	@Order(5)
	public void wrongCreditValueForAddCredit() {
		user temp = new user("TestUser       ", "FS", "100.00");
		parser.addUsers(temp);
		
		String transaction = "06 TestUser        FS OOO200.OO";
		Assertions.assertThrows(NumberFormatException.class, () -> parser.addCredit(transaction));
		
		parser.clearUsers();
	}
	
	//Test to see if the program correctly handles the user not being found within the
	//user array
	@Test
	@Order(6)
	public void addCreditToNonExistingUser() {
		String transaction = "06 TestUser        FS 000200.00";
		Assertions.assertThrows(NoSuchElementException.class, () -> parser.addCredit(transaction));
	}
	
	/*
	 * -----------------------------
	 * STATEMENT COVERAGE FOR CREATE
	 * -----------------------------
	 */

	//test to see if create function is working properly
	@Test
	@Order(7)
	public void testCreate() {
		user temp = new user("TestUser       ", "FS", "100.00");
		
		String transaction = "01 TestUser        FS 000100.00";
		parser.create(transaction);
		
		Assertions.assertTrue(temp.equals(parser.searchUser(temp.getUsername())));
		
		parser.clearUsers();
	}
	
	//test to check whether or not function properly handles bing passed a wrong value
	@Test
	@Order(8)
	public void wrongCreditValuesForCreate() {
		String transaction = "01 TestUser        FS OOO1OO.OO";
		Assertions.assertThrows(NumberFormatException.class, () -> parser.create(transaction));
	}
	
	//test to check what happens when user being created with a username already in use
	@Test
	@Order(9)
	public void createUserWithExistingUsername() {
		user temp = new user("TestUser       ", "FS", "100.00");
		parser.addUsers(temp);
		
		String transaction = "01 TestUser        FS 000100.00";
		Assertions.assertThrows(IllegalArgumentException.class, () -> parser.create(transaction));
		parser.clearUsers();
	}
	
	/*
	 * -----------------------------
	 * STATEMENT COVERAGE FOR DELETE
	 * -----------------------------
	 */
	
	//test to see if delete works properly
	@Test
	@Order(19)
	public void testDeleteUser() {
		user temp = new user("TestUser       ", "FS", "000100.00");
		parser.addUsers(temp);
	}	
	
	
	
	/*
	 * -------------------------------------
	 * DECISION AND LOOP COVERAGE FOR REFUND
	 * -------------------------------------
	 */
	
	//DESCISION COVERAGE SECTION
	
	//first test in decision coverage: buyer = null, seller = null
	@Test
	@Order(10)
	public void decisionCoverageOne() {
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		Assertions.assertThrows(NoSuchElementException.class, () -> parser.refund(transaction));
	}
	
	//second test in decision coverage: buyer = 50.00, seller = null
	@Test
	@Order(11)
	public void decisionCoverageTwo() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		parser.addUsers(buyer);
		
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		Assertions.assertThrows(NoSuchElementException.class, () -> parser.refund(transaction));
		
		parser.clearUsers();
	}
	
	//third test in decision coverage: buyer = 50.00, seller = 25.00
	@Test
	@Order(12)
	public void decisionCoverageThree() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000025.00");
		parser.addUsers(buyer, seller);
			
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		Assertions.assertThrows(IllegalArgumentException.class, () -> parser.refund(transaction));
			
		parser.clearUsers();
	}
	
	//fourth test in decision coverage: buyer = 999999.00, seller = 150.00
	@Test
	@Order(13)
	public void decisionCoverageFour() {
		user buyer = new user("BuyingUser     ", "BS", "999999.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		parser.addUsers(buyer, seller);
				
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		Assertions.assertThrows(IllegalArgumentException.class, () -> parser.refund(transaction));
				
		parser.clearUsers();
	}
	
	//fifth test in decision coverage: buyer = 000050.00, seller = 150.00
	@Test
	@Order(14)
	public void decisionCoverageFive() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		parser.addUsers(buyer, seller);
					
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
		
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
					
		parser.clearUsers();
	}
	
	
	//LOOP COVERAGE SECTION
	
	//first test in loop coverage: parser user array contains one user
	//and no sellers
	//this would be the same as decision coverage two
	
	//second test in loop coverage: parser user array contains two users
	//with the seller being the first user in the array
	@Test
	@Order(15)
	public void LoopCoverageTwo() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		parser.addUsers(seller, buyer);
					
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
		
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
					
		parser.clearUsers();
	}
	
	//third test in loop coverage: parser user array contains two users
	//with the selling user being at the back
	//this would be the same as decision coverage five
	
	//fourth test in loop coverage: parser user array contains many users
	//with the selling user being near the end
	@Test
	@Order(16)
	public void loopCoverageFour() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		
		user otherUserOne = new user("otherUserOne   ", "FS", "000001.00");
		user otherUserTwo = new user("otherUserTwo   ", "FS", "000002.00");
		user otherUserThree = new user("otherUserThree ", "FS", "000003.00");
		user otherUserFour = new user("otherUserFour  ", "FS", "000004.00");
		user otherUserFive = new user("otherUserFive  ", "FS", "000005.00");
		user otherUserSix = new user("otherUserSix   ", "FS", "000006.00");
		user otherUserSeven = new user("otherUserSeven ", "FS", "000007.00");
		user otherUserEight = new user("otherUserEight ", "FS", "000008.00");
		
		parser.addUsers(buyer, otherUserOne, otherUserTwo, otherUserThree, 
						otherUserFour, otherUserFive, otherUserSix, otherUserSeven, 
						otherUserEight, seller);
					
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
		
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
					
		parser.clearUsers();
	}
	
	//fifth test in loop coverage: parser user array contains no users
	//this would be the same a decision coverage one
	
	//sixth test in loop coverage: parser user array contains many users
	//with buying user being at the front 
	@Test
	@Order(17)
	public void loopCoverageSix() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		user otherUserOne = new user("otherUserOne   ", "FS", "000001.00");
		user otherUserTwo = new user("otherUserTwo   ", "FS", "000002.00");
		user otherUserThree = new user("otherUserThree ", "FS", "000003.00");
		
		parser.addUsers(buyer, otherUserOne, otherUserTwo, otherUserThree, seller);
		
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
		
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
					
		parser.clearUsers();
	}
	
	//seventh test in loop coverage: parser user array contains many users
	//with buying user being in the second spot 
	@Test
	@Order(17)
	public void loopCoverageSeven() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		user otherUserOne = new user("otherUserOne   ", "FS", "000001.00");
		user otherUserTwo = new user("otherUserTwo   ", "FS", "000002.00");
		user otherUserThree = new user("otherUserThree ", "FS", "000003.00");
			
		parser.addUsers(otherUserOne, buyer, otherUserTwo, otherUserThree, seller);
			
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
			
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
						
		parser.clearUsers();
	}
	
	//eighth and final test in loop coverage: parser user array contains many users
	//with buying user being near the end
	@Test
	@Order(18)
	public void loopCoverageEight() {
		user buyer = new user("BuyingUser     ", "BS", "000050.00");
		user seller = new user("SellingUser    ", "SS", "000150.00");
		
		user otherUserOne = new user("otherUserOne   ", "FS", "000001.00");
		user otherUserTwo = new user("otherUserTwo   ", "FS", "000002.00");
		user otherUserThree = new user("otherUserThree ", "FS", "000003.00");
		user otherUserFour = new user("otherUserFour  ", "FS", "000004.00");
		user otherUserFive = new user("otherUserFive  ", "FS", "000005.00");
		user otherUserSix = new user("otherUserSix   ", "FS", "000006.00");
		user otherUserSeven = new user("otherUserSeven ", "FS", "000007.00");
		user otherUserEight = new user("otherUserEight ", "FS", "000008.00");
		
		parser.addUsers(otherUserOne, otherUserTwo, otherUserThree, otherUserFour, 
						seller, otherUserFive, otherUserSix, otherUserSeven, 
						otherUserEight, buyer);
					
		String transaction = "05 BuyingUser      SellingUser     000050.00";
		parser.refund(transaction);
		
		Assertions.assertEquals(100.00, parser.searchUser("SellingUser    ").getCredit());
		Assertions.assertEquals(100.00, parser.searchUser("BuyingUser     ").getCredit());
					
		parser.clearUsers();
	}
	
}
