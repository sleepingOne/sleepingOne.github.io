/*

retrieve accountid param/ vector position
select choice param 

transaction.html
update/write object

return to choice to print new page

*/

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;

public class Choice extends HttpServlet
{
private final static Logger slogger = Logger.getLogger(Choice.class.getName());//log
public static FileHandler fh;
 
 public static void initSlog(String servletPath){//int for session log
 		try {
 			fh=new FileHandler(servletPath,true);//session logfile name
			SimpleFormatter simple = new SimpleFormatter();
			fh.setFormatter(simple);
			slogger.addHandler(fh);//
			slogger.setLevel(Level.ALL);
 		} catch (SecurityException | IOException e) {
 			e.printStackTrace();
		}
}
public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {		


		response.setContentType("text/html");//
		PrintWriter out=response.getWriter();//
		HttpSession session = request.getSession(false);//request session set as session; no create
//if null no session
		String sId =  request.getSession(false).getId();//slog
		ServletContext context = getServletContext();//webroot file path
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");	
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");		  
		initSlog(logPath);//session log initializer
		BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

		String name = (String) session.getAttribute("user");//login param 
		int tId = Integer.valueOf(request.getParameter("tId"));//dep/withdraw param
		String choice = request.getParameter("choice");//dep/withdraw param 
		double amount = Double.valueOf(request.getParameter("amount"));//dep/withdraw param 
		double nBalance = 0;		

//search vector for matching names 
	
        ObjectInputStream objIs = null;
	String nameMatch = null;
	int idMatch;

//get session vector to modify
	    @SuppressWarnings("unchecked")
	    Vector<Account> vecRead = (Vector<Account>) session.getAttribute("vecRead");
	     	
	    out.println("<HTML>");
	    out.println("<HEAD>");
	    out.println("<TITLE> Choice </TITLE>");///links
	    out.println("<a href=\"index.html\"> Back to Login</a> |");
	    out.println("<a href=\"addAccount.html\"> add account</a> |");
	    out.println("<a href=\"delAccount.html\"> delete account</a>|");
	    out.println("<a href=\"transaction.html\"> transaction</a> |");	  	    	  
	    out.println("<a href=\"accHistory\">account history</a>|");
	    out.println("<a href=\"balance\"> accounts and balance</a>|");
	    out.println("<BODY>");    
	    for(Account account: vecRead){
		if(account instanceof Account)
			nameMatch = account.getAccountUserId();
			idMatch = account.getAccountId();

			if(nameMatch.compareTo(name)==0 && idMatch==tId){//look for account with matching name,type*, Id
				//deposit param  tId, amount, choice
				//get account/ element pos
				if(choice.compareTo("deposit")==0 && amount > 0){//transaction conditions
				       tlog.write(name+" deposited "+amount+" to account#"+tId);
		
					nBalance = account.getBalance();
					nBalance = amount+nBalance;			
					account.setBalance(nBalance);
					tlog.write(" New Balance: $ "+nBalance+"\n");
					out.println("<h1>deposit success<h1>");
					out.println("<BR>id:"+ tId +" <BR> new balance: $"+nBalance);
					out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
					out.println("</BODY>");
    					out.println("</HTML>");
    					out.close();	
	
				}else if(choice.compareTo("withdraw")==0 && amount > 0){
					
//debug here				
					nBalance = account.getBalance();//iterate balances
					if(amount > nBalance){//check sufficient balance
						tlog.write(name+" has insufficient funds to withdraw\n");										
						out.println("<h1>Insufficient funds<h1>");
						out.println("<BR> current balance:"+ nBalance);	
						out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
						out.println("</BODY>");
	    					out.println("</HTML>");
	    					out.close();
					}else{
						nBalance = nBalance - amount;//withdraw
						account.setBalance(nBalance);//update balance
						tlog.write(name+" withdrawing $"+amount+" to account#"+tId);				
						tlog.write(" New Balance: $"+nBalance+"\n");
						out.println("<h1>withdraw success<h1>");
						out.println("<BR>id:"+ tId +" <BR> new balance: $"+nBalance);	
						out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
						out.println("</BODY>");
	    					out.println("</HTML>");
	    					out.close();
					}
				}else{ 
					slogger.info("Invalid form input");
					out.println("<h1>Try again<h1>");
					out.println("<BR> wrong account Id: "+tId+" or amount: "+ amount);	
					out.println("<h1><a href=\"index.html\"> back to login</a></h1>");
					out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
					out.println("</BODY>");
	    				out.println("</HTML>");
	    				out.close();
					continue;
				}//choice 
			}else{ continue;}//only matching names and accountid
	     }//for loop
			out.println("</BODY>");
			out.println("</HTML>");
			out.close();
	     session.setAttribute("vecRead", vecRead);//set attr for session
	     //check if vector read updating
	     storeObject(vecRead);	

	fh.close();
	tlog.close();
        }

public void storeObject(Vector accs){// store specific object
        OutputStream ops = null;
        ObjectOutputStream objOps = null;
        try {
	    String path = getServletContext().getRealPath("/WEB-INF/accountsDB.ser");//WEBINF path
	    File file = new File(path);
	    if(!file.exists()){//create file if not found
		file.createNewFile();
	    }
	    String fullPath = file.getCanonicalPath();
            ops = new FileOutputStream(fullPath);//open file to output stream to write
            objOps = new ObjectOutputStream(ops);// open objects in file to write
            objOps.writeObject(accs);//write object
            objOps.flush();
        } catch (FileNotFoundException e) {// catch errors
            e.printStackTrace();
	    slogger.info("accounts database File not found");//session log	
         } catch (IOException e) {
            e.printStackTrace();
	    slogger.info("file read IOException");//session log
        } finally{
            try{
                if(objOps != null) objOps.close();// if stream is null close stream 
            } catch (Exception ex){
		slogger.info("object outputstream exception error");
            }
        }
         
    }
	
}

