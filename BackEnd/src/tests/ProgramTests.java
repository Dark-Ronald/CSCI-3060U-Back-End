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
		parser.addUser(temp);
		
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
		parser.addUser(temp);
		
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
		parser.addUser(temp);
		
		String transaction = "01 TestUser        FS 000100.00";
		Assertions.assertThrows(IllegalArgumentException.class, () -> parser.create(transaction));
		parser.clearUsers();
	}
}
