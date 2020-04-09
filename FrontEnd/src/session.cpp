#include <iostream>
#include <string>
#include <vector>
#include "getInput.h"
#include "user.h"
#include "FileReader.h"
#include "transactionFileWriter.h"
#include "session.h"
#include <sstream>
#include <iomanip>
#if(_DEBUG)
#include "main.h"
#include <csetjmp>
//eofbit should never be set when a user is using the program, but 
//since the tests send a file through cin, they set the eofbit.
//So this makes it so that if they set it, the program exits
#define checkTestEnd if (cin.peek() == EOF) { longjmp(testExit, 1); }
#endif

using namespace std;

//logs out of session
void session::logout() {


	string transaction;
	transaction += "00 ";
	transaction += userObject->getUsername();
	transaction += " ";
	transaction += userObject->getUserTypeAsString();
	transaction += " ";
	transaction += pad(userObject->getCreditAsString(), 9, '0', 'r');
	transactionFileWriter::add(transaction);
	transactionFileWriter::writeOut();
}

void session::advertise() {
	if ((userObject->getUserType() & (user::ADMIN | user::FULL_STANDARD | user::SELL_STANDARD)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}
	string itemName = getInputWithSpaces("Enter Item Name: ", "Error: Invalid Name", 19, true);
	string minBid = getMonetaryInputAsString("Enter Minimum Bid: ", [](string input) {
		double val = stod(input);
		if (val < 0) {
			cout << "Error: Minimum Bid Cannot Be Negative" << endl;
			return false;
		}
		else if (val > 999.99) {
			cout << "Error: Maximum Price For Item Is 999.99" << endl;
			return false;
		}
		return true;
	});
	string period;
	bool validPeriod = false;
	while (!validPeriod) {
		period = getInputWithSpaces("Enter Number Of Days Until Auction Ends: ", "Error: Invalid Input", 3, false);
		try {
			stoi(period);
		}
		catch (exception e) {
			cout << "Error: Invalid Input" << endl;
			continue;
		}
		/*
		cout << "Enter Number Of Days Until Auction Ends: ";
#if(_DEBUG)
		checkTestEnd
#endif
		cin >> period;
		if (cin.peek() != '\n') {
			cout << "Error: Invalid Input" << endl;
		}
		*/
		if (stoi(period) < 0) { //assume days until auction starts when item becomes available
			cout << "Error: Number Of Days Until Auction Ends Cannot Be Negative" << endl;
		}
		else if (stoi(period) > 100) {
			cout << "Error: Maximum Number Of Days Until Auction Ends Is 100" << endl;
		}
		else {
			validPeriod = true;
		}
	}

	string transaction;
	transaction += "03 ";
	transaction += itemName;
	transaction += " ";
	transaction += userObject->getUsername();
	transaction += " ";
	transaction += pad(period, 3, '0', 'r');
	transaction += " ";
	transaction += pad(minBid, 6, '0', 'r');
	transactionFileWriter::add(transaction);
}

void session::bid() {
	
	// bid item
	// check user types
	if ((userObject->getUserType() & (user::ADMIN | user::FULL_STANDARD | user::BUY_STANDARD)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}

	string itemName;
	string sellerName;
	double minBid;

	vector<string> availableItems = FileReader::getAvailableItems();
	bool itemFound = false;
	while (!itemFound) {
		itemName = getInputWithSpaces("Enter Item: ", "Error: Invalid Name", 19, true);
		for (string line : availableItems) {
			if (line.substr(0, 19).compare(itemName) == 0) {
				itemFound = true;
				break;
			}
		}
		if (!itemFound) {
			cout << "Error: No Item With That Name Exists" << endl;
		}
	}
	bool sellerFound = false;
	while (!sellerFound) {
		sellerName = getInputWithSpaces("Enter Seller Username: ", "Error: Invalid Name", 15, true);
		for (string line : availableItems) {
			if (line.substr(20, 15).compare(sellerName) == 0) {
				sellerFound = true;
				minBid = stod(line.substr(56, 6));
				break;
			}
		}
		if (!sellerFound) {
			cout << "Error: Seller Does Not Have That Item Up For Auction" << endl;
		}
	}

	stringstream ss;
	ss << fixed << setprecision(2) << minBid;
	cout << "Current Highest Bid: " << ss.str() << endl;

	if ((userObject->getUserType() & (user::ADMIN)) != userObject->getUserType()) {
		minBid *= 1.05;
	}

	string bid = getMonetaryInputAsString("Enter Bid: ", [minBid](string input) {
		double val = stod(input);
		if (val < 0) {
			cout << "Error: Minimum Bid Cannot Be Negative" << endl;
			return false;
		}
		else if (val < minBid) {
			stringstream ss;
			ss << fixed << setprecision(2) << minBid;
			cout << "Error: Minimum Bid Is " + ss.str() << endl;
			return false;
		}
		return true;
	});

	string transaction;
	transaction += "04 ";
	transaction += itemName;
	transaction += " ";
	transaction += sellerName;
	transaction += " ";
	transaction += userObject->getUsername();
	transaction += " ";
	transaction += pad(bid, 6, '0', 'r');
	transactionFileWriter::add(transaction);
}

void session::create() {
	if ((userObject->getUserType() & (user::ADMIN)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}
	string newUsername;
	bool validName = false;
	while (!validName) {
		newUsername = getInputWithSpaces("Enter Username For New User: ", "Error: Invalid Name", 15, true);
		vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();
		bool cont = false;
		for (int i = 0; i < currentUserAccounts.size() - 1; i++) {
			string& line = currentUserAccounts[i];
			if (line.substr(0, 15).compare(newUsername) == 0) {
				cout << "Error: User With That Name Already Exists" << endl;
				cont = true;
				break;
			}
		}
		if (cont) continue;
		validName = true;
		break;
	}

	string newUserType;
	while (true) {
		newUserType = getInputWithSpaces("Enter Type For New User: ", "Error: New User Type Must Be One Of(admin, full - standard, buy - standard, sell - standard)", 20, false);
		/*
#if(_DEBUG)
		checkTestEnd
#endif
		cin >> newUserType;
		if (cin.peek() != '\n') {
			cout << "Error: Invalid Input" << endl;
		}
		*/
		if (newUserType.compare("admin") == 0) {
			newUserType = "AA";
			break;
		}
		else if (newUserType.compare("full-standard") == 0) {
			newUserType = "FS";
			break;
		}
		else if (newUserType.compare("buy-standard") == 0) {
			newUserType = "BS";
			break;
		}
		else if (newUserType.compare("sell-standard") == 0) {
			newUserType = "SS";
			break;
		}
		else {
			cout << "Error: New User Type Must Be One Of (admin, full-standard, buy-standard, sell-standard)" << endl;
		}
	}

	string transaction;
	transaction += "01 ";
	transaction += newUsername;
	transaction += " ";
	transaction += newUserType;
	transaction += " ";
	transaction += pad("", 9, '0', 'r');
	transactionFileWriter::add(transaction);
}

void session::addCredit() {
	user* userToAddTo = userObject;
	double creditAmount;

	if ((userObject->getUserType() & (user::ADMIN)) == userObject->getUserType()) {
		bool validUser = false;
		while (!validUser) {
			string userName = getInputWithSpaces("Enter User Name To Add Credit To: ", "Error: Invalid User Name", 15, true);
			vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();

			for (string line : currentUserAccounts) {
				if (line.substr(0, 15).compare(userName) == 0) {
					userToAddTo = new user(line.substr(0, 15), line.substr(16, 2), line.substr(19, 9));
					validUser = true;
					break;
				}
			}
			if (!validUser) {
				cout << "Error: No User With That Name Exists" << endl;
			}
		}
	}

	while (true) {
		creditAmount = getMonetaryInput("Enter Amount To Add: ", [](string input) {
			double val = stod(input);
			if (val < 0) {
				cout << "Error: Cannot Add Negative Credit" << endl;
				return false;
			}
			else if (val > 1000.00) {
				cout << "Error: Cannot Add More Than 1000.00 Credit" << endl;
				return false;
			}
			return true;
		});
		if (creditAmount + userToAddTo->getCredit() > 999999.0) {
			if (userToAddTo == userObject) {
				cout << "Error: Cannot Have More Than 999,999.00 Credit In Your Account" << endl;
			}
			else {
				cout << "Error: Users Cannot Have More Than 999,999.00 Credit" << endl;
			}
			continue;
		}
		bool userTracked = false;
		for (user* trackedUser : userCreditTracker) {
			if (trackedUser->getUsername().compare(userToAddTo->getUsername()) == 0) {
				userTracked = true;
				if (trackedUser->getCredit() + creditAmount - trackedUser->getStartCredit() > 1000) {
					if (userToAddTo != userObject) {
						cout << "Error: Cannot Add More Than 1000.00 Credit To A User In A Single Session" << endl;
					}
					else {
						cout << "Error: Cannot Add More Than 1000.00 Credit To Your Account In A Single Session" << endl;
					}
					continue;
				}
			}
		}
		if (!userTracked) {
			userCreditTracker.push_back(userToAddTo);
		}
		break;
	}

	stringstream ss;
	ss << fixed << setprecision(2) << creditAmount;

	string transaction;
	transaction += "06 ";
	transaction += userToAddTo->getUsername();
	transaction += " ";
	transaction += userObject->getUserTypeAsString();
	transaction += " ";
	transaction += pad(ss.str(), 9, '0', 'r');
	transactionFileWriter::add(transaction);

	/*
	//only allows admin or full standard users to add credit
	if ((userObject->getUserType() & (user::ADMIN | user::FULL_STANDARD)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}


	//path for if user is admin type is different than if user is full standard
	//admin path first
	if ((userObject->getUserType() & (user::ADMIN)) == userObject->getUserType()) {
		user* userToAddTo = NULL;
		bool validUser = false;
		while (!validUser) {
			string userName = getInputWithSpaces("Enter User Name To Add Credit To: ", "Error: Invalid User Name", 15);
			vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();

			for (int i = 0; i < currentUserAccounts.size() - 1; i++) {
				string& line = currentUserAccounts[i];
				if (line.substr(0, 15).compare(userName) == 0) {
					userToAddTo = new user(line.substr(0, 15), line.substr(16, 2), line.substr(19, 9));
					validUser = true;
					break;
				}
			}
			if (!validUser) {
				cout << "Error: No User With That Name Exists" << endl;
			}
		}
		while (true) {
			double creditAmount = getMonetaryInput("Enter Amount To Add: ", [](string input) {
				double val = stod(input);
				if (val < 0) {
					cout << "Error: Cannot Add Negative Credit" << endl;
					return false;
				}
				else if (val > 1000.00) {
					cout << "Error: Cannot Add More Than 1000.00 Credit" << endl;
					return false;
				}
				return true;
			});
			if (creditAmount + userToAddTo->getCredit() > 999999.0) {
				cout << "Error: Cannot Have More Than 999,999.00 In Your Account." << endl;
				continue;
			}
			for (user* trackedUser : userCreditTracker) {
				if (trackedUser->getUsername().compare(userToAddTo->getUsername()) == 0) {
					if (trackedUser->getCredit() + creditAmount - trackedUser->getStartCredit() > 1000) {
						cout << "Error: Cannot Add More Than 1000.00 Credit To A User In A Single Session" << endl;
						continue;
					}
				}
			}
			userToAddTo->addCredit(creditAmount);
			userCreditTracker.push_back(userToAddTo);
			break;
			
		}

		//creates the transaction line and sends it to writer
		string transaction;
		transaction += "06 ";
		transaction += userToAddTo->getUsername();
		transaction += " ";
		transaction += "AA";
		transaction += " ";
		transaction += pad(userToAddTo->getCreditAsString(), 9, '0', 'r');
		transactionFileWriter::add(transaction);
		transactionFileWriter::writeOut();

		return;
	}


	//Full standard user path
	if ((userObject->getUserType() & (user::FULL_STANDARD)) == userObject->getUserType()) {
		while (true) {
			double creditAmount = getMonetaryInput("Enter Amount To Add To Account: ", [](string input) {
				double val = stod(input);
				if (val < 0) {
					cout << "Error: Cannot add negative credit" << endl;
					return false;
				}
				else if (val > 1000.00) {
					cout << "Error: Maximum Amount To Add Is 1000.00" << endl;
					return false;
				}
				return true;
			});
			if (creditAmount + userObject->getCredit() > 999999.0) {
				cout << "Error: Cannot Have More Than 999,999.00 In Your Account." << endl;
				continue;
			}
			else if (userObject->getCredit() + creditAmount - userObject->getStartCredit() > 1000) {
				cout << "Error: Cannot Add More Than 1000.00 Credit To Your Account In A Single Session" << endl;
				continue;
			}
			else {
				userObject->addCredit(creditAmount);
				break;
			}
		}

		//creates the transaction line and sends it to the writer
		string transaction;
		transaction += "06 ";
		transaction += userObject->getUsername();
		transaction += " ";
		transaction += "FS";
		transaction += " ";
		transaction += pad(userObject->getCreditAsString(), 9, '0', 'r');
		transactionFileWriter::add(transaction);
		transactionFileWriter::writeOut();
		return;
	}

	return;
	*/
}

void session::refund() {
	if ((userObject->getUserType() & (user::ADMIN)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}

	string buyerName;
	string sellerName;
	string amount;
	vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();

	bool validBuyerName = false;
	while (!validBuyerName) {
		buyerName = getInputWithSpaces("Enter Buyer User Name: ", "Error: Invalid User Name", 15, true);
		
		for (string line : currentUserAccounts) {
			if (line.substr(0, 15).compare(buyerName) == 0) {
				validBuyerName = true;
				break;
			}
		}
		if (!validBuyerName) {
			cout << "Error: No User With That Name Exists" << endl;
		}
	}

	bool validSellerName = false;
	while (!validSellerName) {
		sellerName = getInputWithSpaces("Enter Seller User Name: ", "Error: Invalid User Name", 15, true);

		for (string line : currentUserAccounts) {
			if (line.substr(0, 15).compare(sellerName) == 0) {
				validSellerName = true;
				break;
			}
		}
		if (!validSellerName) {
			cout << "Error: No User With That Name Exists" << endl;
		}
	}

	amount = getMonetaryInputAsString("Enter Credit Amount To Be Refunded: ", [](string input) {
		double val = stod(input);
		if (val < 0) {
			cout << "Error: Amount Cannot Be Negative" << endl;
			return false;
		}
		if (val > 999999) {
			cout << "Error: Max Refund Amount Is 999,999.00" << endl;
			return false;
		}
		return true;
	});

	string transaction;
	transaction += "05 ";
	transaction += buyerName;
	transaction += " ";
	transaction += sellerName;
	transaction += " ";
	transaction += pad(amount, 9, '0', 'r');
	transactionFileWriter::add(transaction);
}

//deletes user
void session::deleteUser() {
	//only allows admins to delete users
	if ((userObject->getUserType() & (user::ADMIN)) != userObject->getUserType()) {
		cout << "Error: You Do Not Have Privileges To Perform This Transaction" << endl;
		return;
	}

	//gets user to be deleted
	string userToDelete;
	bool validUser = false;
	while (!validUser) {
		userToDelete = getInputWithSpaces("Enter Username of User to Delete: ", "Error: Invalid Username", 15, true);
		vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();

		for (string line : currentUserAccounts) {
			if (line.substr(0, 15).compare(userToDelete) == 0) {
				validUser = true;
				break;
			}
		}
		if (!validUser) {
			cout << "Error: No User With That Name Exists" << endl;
		}
	}

	string transaction;
	transaction += "02 ";
	transaction += userToDelete;
	transaction += " ";
	transaction += "AA";
	transaction += " ";
	transaction += "000000000";
	transactionFileWriter::add(transaction);
}

/*
reads the userAccounts file and finds the user details with the
given account name
if name is found in userAccounts file, instantiates userObject
and returns a new session
if name is not found returns null
*/
session* session::login() {
	string username = getInputWithSpaces("Enter Username: ", "Error: Invalid Username", 15, true);
	username = pad(username, 15, ' ', 'l');
	vector<string> currentUserAccounts = FileReader::getCurrentUserAccounts();
	
	for (string line : currentUserAccounts) {
		if (line.substr(0, 15).compare(username) == 0) {
			return new session(new user(line.substr(0, 15), line.substr(16, 2), line.substr(19, 9)));
		}
	}
	return NULL;
}

void session::sessionLoop() {
	string command;
	while (true) {
#if(_DEBUG)
		checkTestEnd
#endif
		command = getInputWithSpaces("", "Error: Invalid Input", 20, false);

		if (command.compare("login") == 0) {
			cout << "Error: Already Logged In" << endl;
		}
		else if (command.compare("logout") == 0) {
			logout();
			break;
		}
		else if (command.compare("advertise") == 0) {
			advertise();
		}
		else if (command.compare("bid") == 0) {
			bid();
		}
		else if (command.compare("create") == 0) {
			create();
		}
		else if (command.compare("addcredit") == 0) {
			addCredit();
		}
		else if (command.compare("refund") == 0) {
			refund();
		}
		else if (command.compare("delete") == 0) {
			deleteUser();
		}
		else {
			cout << "Error: Invalid Command" << endl;
		}
	}
}

session::session(user* userObject) {
	this->userObject = userObject;
}

session::~session() {
	for (user* user : userCreditTracker) {
		if (user != userObject) {
			delete user;
		}
	}
	delete userObject;
}