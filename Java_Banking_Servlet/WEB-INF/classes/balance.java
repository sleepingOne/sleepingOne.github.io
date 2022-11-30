/*

get balance

*/

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;

public class balance extends HttpServlet {

private final static Logger slogger = Logger.getLogger(LoginServlet.class.getName());//log
public static FileHandler fh;
 
 public static void initSlog(String servletPath){//int for session log
 		try {
 			fh=new FileHandler(servletPath,true);//session logfile name
			SimpleFormatter simple = new SimpleFormatter();
			fh.setFormatter(simple);
			slogger.addHandler(fh);//
			slogger.setLevel(Level.CONFIG);
 		} catch (SecurityException | IOException e) {
 			e.printStackTrace();
		}
}

public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException
    {		

		response.setContentType("text/html");//print html
		PrintWriter out=response.getWriter();//set print to out

		HttpSession session = request.getSession(false);//request session set as session			
		String sId =  request.getSession(false).getId();//slog
		String name = (String) session.getAttribute("user");//login param 		
		ServletContext context = getServletContext();//webroot file path  
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");		  

		initSlog(logPath);//session log initializer

		if(name==null){//invalidate/logoff
			slogger.info("No user name Param Session invalid:"+ sId);//session log
			session.invalidate();
			out.println("<HTML>");
	    		out.println("<HEAD>");
	    		out.println("<TITLE> Invalid </TITLE>");///print the account selected
			out.println("</HEAD>");
			out.println("<BODY>");
	    		out.println("Invalid session");
	    		out.println("<h1><a href=\"index.html\"> back to login</a></h1>");
	    		out.println("</BODY>");
	    		out.println("</HTML>");
			out.close();		
		}


//search vector for matching names 
	
        ObjectInputStream objIs = null;
	String nameMatch = null;
		  
        try {
            InputStream fileIs = context.getResourceAsStream("/WEB-INF/accountsDB.ser");// open for reading
            objIs = new ObjectInputStream(fileIs);// open objects in file for reading
	    @SuppressWarnings("unchecked")//vecRead...readObject();
            Vector<Account> vecRead = (Vector<Account>) objIs.readObject();//read object 
            session.setAttribute("vecRead",vecRead);//allow other servlets to access
	    int vecSize = vecRead.size();
				
	    out.println("<HTML>");//html start
	    out.println("<HEAD>");
	    out.println("<TITLE> Accounts </TITLE>");///print the account selected
	  	    	  
	    out.println("<BODY>");
	    out.println("<h1> Accounts</h1>");	
///log
	    slogger.info("reading in accounts from file"+ sId);//session log	
            slogger.info("vecSize: "+vecSize);//session log
	    slogger.info("vecRead"+vecRead);
 
//count accounts in vecRead or return 0	    
	    int acc = 0;
	    for(Account account: vecRead){//***loops through all accounts prints matching account name
		if(account instanceof Account)
			nameMatch = account.getAccountUserId();
			//out.println("looking through accounts:");//log
			if(nameMatch.compareTo(name)==0){
				acc++;
				out.println("<BR><h2>");
				out.println(account.toString());
				out.println("</h2><BR>"); 				
			}
	     }//for

	    if(acc==0){//no matches
		slogger.info("No matching user Id in database");
		session.invalidate();
	   	sendPage(response,name);
	    }else{
		out.println("<BR><BR><BR>");
		out.println("<a href=\"index.html\"> Back to Login</a> |");
	    	out.println("<a href=\"addAccount.html\"> add account</a> |");
	    	out.println("<a href=\"delAccount.html\"> delete account</a>|");
	    	out.println("<a href=\"transaction.html\"> transaction</a> |");	  	    	  
	    	out.println("<a href=\"accHistory\">account history</a>|");
		out.println("<a href=\"balance\"> accounts and balance</a>|");
		out.println("</BODY>");
	    	out.println("</HTML>");	
		out.close();
	    }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
	    slogger.info("accounts database File not found");//session log	
        } catch (IOException e) {
            e.printStackTrace();
	    slogger.info("file read IOException");//session log
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
	    slogger.info("class not found");//session log
        } finally {
            try {
                if(objIs != null) objIs.close();// close object input
            } catch (Exception ex){
                 slogger.info("Object input stream error");//session log
            }
        }
	fh.close();//close filehandler slog

    }



	private void sendPage(HttpServletResponse reply,String name) throws IOException
	{
		reply.setContentType("text/HTML");

		PrintWriter out = reply.getWriter();
		out.println("<HTML>");
	    	out.println("<HEAD>");
	    	out.println("<TITLE> Invalid </TITLE>");///print the account selected
		out.println("</HEAD>");
		out.println("<BODY>");
	    	out.println("<BR>"+name+" is an Invalid user name or not registered");
	    	out.println("<h1><a href=\"index.html\"> back to login</a></h1>");
		out.println("<h1><a href=\"register.html\"> register</a></h1>");
	    	out.println("</BODY>");
	    	out.println("</HTML>");	
		out.flush();
		out.close();
	}

}
