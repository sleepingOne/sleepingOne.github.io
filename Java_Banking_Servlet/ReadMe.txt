Java Banking Servlet Version 2.0

untested 

Application taerget Requirements

1. frontend
   All pages:
      nav bar
   
   index.html: 
      about page/content
      account usage
      privacy statement   


2. backend
   user information saved to mysql database
   user registration
   user accounts
   secure login?
      3rd party?
      oauth compatibility?
   encryption?


3. Utility
   update error logs
      seperate access logs?
      success and fail?

   analytics
      crashing
      slow loading?
      wrong page 
   
4.


src
   html pages






<!-- 
----page directory---
main bank login 
log in user credentials 
    -take user login parameters username(do not need password)
    -transfer user to balance page 
or
link to create account(add new user)
    -take user login parameters 
    -check if username is already taken?
    -create new login link to add account page

loginServlet/register(balance)
   -read/display accounts and view amounts 
   -link to transaction page
addaccount.html
   -take user login, account name, and amount (max 10k add?)
   -check if account exists, if not create account  
   -update/write amount to file object
   -link back to balance page
transaction.html
-deposit
-withdraw
deleteaccount
   -take user login, account id
   -check if account exists, if not error msg  
   -delete object from file
   -link back to balance page

history
   -open log 
   -read/print log file 
   -exit log, link to login page


------
   <link href="style.css" rel="stylesheet" type="text/css">
	<script src="script.js">
	</script>


-->








Java Banking Servlet Version 1.0

tested on Apache Tomcat 8.5.39

This banking application is used to demonstrate working knowledge of hosting a Java Web application on a local Apache server. 

Application Target Requirements:
1. Login screen – to identify the user (or add new). User can be identified and added to session to create state of user data.

2. Transfer screen – have the ability to utilize a transfer method in one of your objects between accounts. Does not matter which accounts, but you will need to present the user with their accounts, choose which one to transfer from and which one to transfer to. After the transfer the user should be able to see their accounts updated to reflect the change. NOTE: must only allow sufficient funds from one account to another

3. Add / Delete accounts – per user, be able to add and delete accounts
	-Add an account per user that will be used with a name, type and $ amount
	-Delete account and remove it from the user object

4. Balance screen – show the user a listing of all of their accounts and balances in the accounts	
	-View data around account's (name, $ amount, and optional ID/key)

5. View history – ability to show what was done when with the account
 If you add new, you can hard code accounts
	-Log data around transactions
	-Show history of transactions to screen

6. Session Data:	
	-Hold user objects in Java file for offline state
	-<META> tags for BACK button Pragma, Cache-control, and Expires META tags are added to response to client


Application html and java code descriptions:

index.html:
-log in user credentials 
    -take user login parameters username(do not need password)
    -transfer user to balance page 

-link to create account(add new user):
    -take user login parameters 
    -check if username is already taken?
    -create new login link to add account page

loginServlet/register(balance):
   -read/display accounts and view amounts 
   -link to transaction page

addaccount.html
   -take user login, account name, and amount (max 10k add?)
   -check if account exists, if not create account  
   -update/write amount to file object
   -link back to balance page

transaction.html:
-deposit
-withdraw

deleteaccount:
   -take user login, account id
   -check if account exists, if not error msg  
   -delete object from file
   -link back to balance page

history:
   -open log 
   -read/print log file 
   -exit log, link to login page
