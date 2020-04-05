# Run the front end 3 times with separate transactions, Ie, advertise item 1, buying item 1, adding credit

tests="dailyScript/*"

for t in $tests
do
​
    # If its a directory, go inside of it, should add credits, advertise an item, then bid on said item
    if [ -d "$t" ]
    then​
        # Run the script
        ./CSCI3060-Front-End/FrontEnd/FrontEnd.exe CSCI3060-Front-End/test_documents/FrontEnd/current_user_accounts_file.txt CSCI3060-Front-End/test_documents/FrontEnd/available_items_file.txt CSCI3060-Front-End/test_documents/FrontEnd/daily_transaction_file.txt < $t/test.inp >​
    fi
    
    # Clear the terminal at the end of the run
    clear
​
done

# Merge all Daily transactions into a single file

paste output1.txt output2.txt output3.txt > MergedFile.txt

# Then run the backend with the merged daily script as input
