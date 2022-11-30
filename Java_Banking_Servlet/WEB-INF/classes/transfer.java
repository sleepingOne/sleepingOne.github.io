/*

retrieve accountids param
select choice param 



transaction 
update/write object

return to choice to print new page

*/

import java.io.*;
import java.util.*;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class transfer extends HttpServlet
{

private final static Logger slogger = Logger.getLogger(transfer.class.getName());//log
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
		String sId =  request.getSession().getId();//slog
		ServletContext context = getServletContext();//webroot file path
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");		  
		initSlog(logPath);//session log initializer
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");	
		
		BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

		String name = (String) session.getAttribute("user");//login param 
		int tId1 = Integer.valueOf(request.getParameter("fromAccount"));//dep/withdraw param
		int tId2 = Integer.valueOf(request.getParameter("toAccount"));//dep/withdraw param				
		double tAmount = Double.valueOf(request.getParameter("tAmount"));//dep/withdraw param 
	

///inside name search loop vecRead
//search vector for matching names 

	String nameMatch1, nameMatch2 = null;
	int idMatch1,idMatch2;		
	double balance1,balance2 = 0;	
	int vecSize,success = 0;

//get session vector to modify
	    @SuppressWarnings("unchecked")
	    Vector<Account> vecWrite = (Vector<Account>) session.getAttribute("vecRead");	     	
	    vecSize = vecWrite.size();
		
	if(tId1<=0 && tId2<=0 && tId1==tId2 && tAmount<0){//form param check slog
            sendPage(response,tId1);
	}else{
		//vector m1 iterator
	    for(Iterator<Account> m1 = vecWrite.iterator();m1.hasNext();){		
			Account account = (Account) m1.next();
			idMatch1 = account.getAccountId();
			nameMatch1 = account.getAccountUserId();
			if(idMatch1==tId1){
				balance1 = account.getBalance();
				if(balance1<tAmount){
					///tlog insufficient funds
					tlog.write(name+" has insufficient funds to transfer\n");					
					sendPage(response,tId1);				
				}else{
					balance1 = balance1-tAmount;
					account.setBalance(balance1);
					success++;				
				}
			}else{		
			continue;}
	    }//for
		//vector m2
	     for(Iterator<Account> m2 = vecWrite.iterator();m2.hasNext();){		
			Account account = (Account) m2.next();
			idMatch2 = account.getAccountId();
			nameMatch2 = account.getAccountUserId();
			if(idMatch2==tId2 && success>0){
				balance2 = account.getBalance();
				balance2 = balance2 + tAmount;
				account.setBalance(balance2);
				out.println("<HTML>");
	    			out.println("<HEAD>");
	    			out.println("<TITLE> Transfer </TITLE>");///links
	    			out.println("<h1><a href=\"transaction.html\"> Back to Transaction</a></h1>");
				out.println("<BR><h3><FONT COLOR=\"GREEN\">Success<BR></FONT> Account ID: "+tId2+" New Balance"+balance2+"<BR><h3>");
				out.println("</BODY>");
	     			out.println("</HTML>");
	     			out.close();
				//tlog tId1 transferred tAmount to tId2
				tlog.write(name+" transferred $"+tAmount+" to account#"+tId2);				
	   			tlog.write(" New Balance: $"+balance2+"\n");
						
			}
	     }//for 2
	}//checks ids and name in param

///reset vec

	     session.setAttribute("vecRead", vecWrite);//set attr for session
	     //check if vector read updating
	     storeObject(vecWrite);
//slog 
	     fh.close();
             out.close();
	     tlog.close();
   }//class

public void storeObject(Vector accs){// store specific object
        OutputStream ops = null;
        ObjectOutputStream objOps = null;
        try {
	    String path = getServletContext().getRealPath("/WEB-INF/accountsDB.ser");//WEBINF path
	    File file = new File(path);
	    if(file.exists()){//create file if not found
		file.createNewFile();
	    }
	    String fullPath = file.getCanonicalPath();
            ops = new FileOutputStream(fullPath);//open file to output stream to write
            objOps = new ObjectOutputStream(ops);// open objects in file to write
            objOps.writeObject(accs);//write object
            objOps.flush();
        } catch (FileNotFoundException e) {// catch errors
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(objOps != null) objOps.close();// if stream is null close stream 
            } catch (Exception ex){//?
            }
        }
         
    }//
	

	private void sendPage(HttpServletResponse reply,int tId) throws IOException
	{
		reply.setContentType("text/HTML");
		PrintWriter out=reply.getWriter();//
	     	out.println("<HTML>");
	    	out.println("<HEAD>");
	    	out.println("<TITLE> Invalid </TITLE>");///print the account selected
		out.println("</HEAD>");
		out.println("<BODY>");
		out.println("Transfer Id:"+ tId);		
	    	out.println("Invalid id or amount");
	    	out.println("<h1><a href=\"transaction.html\">transaction</a></h1>");
		out.println("<h1><a href=\"transfer.html\">transfer</a></h1>");
	    	out.println("</BODY>");
	    	out.println("</HTML>");	
		out.flush();
	}


}

