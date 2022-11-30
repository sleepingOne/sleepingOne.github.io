/*

delete account using id

*/

import java.io.*;
import java.util.*;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;

public class delAccount extends HttpServlet
{

private final static Logger slogger = Logger.getLogger(delAccount.class.getName());//log
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
		String sId =  request.getSession(false).getId();//slog		
		String name = (String) session.getAttribute("user");//login param 
		ServletContext context = getServletContext();//webroot file path
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");	
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");		  
		initSlog(logPath);//session log initializer
		
		BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

		int delId = Integer.valueOf(request.getParameter("delId"));//dep/withdraw param
//check form param		
		if(delId<=0){//check delete id param
			out.println("<TITLE> Account delete error </TITLE>");///print the account selected
			out.println("<BODY>");
			out.println("<RIGHT>");
			out.println("<h1>Login Options</h1>"); 
			out.println("<a href=\"index.html\"> Login</a> |");
			out.println("<a href=\"addAccount.html\">add account</a>");
			out.println("<a href=\"delAccount.html\"> Back to delete account</a>");
			out.println("<a href=\"transaction.html\">Transaction</a>");  
  			out.println("</RIGHT>");
			out.println("<CENTER>");
			out.println("<H1> Invalid amount</H1>");// print account amount
			out.println("<CENTER>");
			out.println("</HEAD>");
			out.println("<BR><BR><BR>");			
		}



//get session vector to modify
	    @SuppressWarnings("unchecked")
	    Vector<Account> vecWrite = (Vector<Account>) session.getAttribute("vecRead");
	     					
	    out.println("<HTML>");
	    out.println("<HEAD>");
	    out.println("<TITLE> Delete Account </TITLE>");///print the account selected
	    out.println("<a href=\"index.html\"> Back to Login</a> |");
	    out.println("<a href=\"addAccount.html\"> add account</a> |");
	    out.println("<a href=\"transaction.html\"> transaction</a> |");
	    out.println("<a href=\"transaction.html\"> transaction</a> ");
	    out.println("<BR><BR><BR>");	  
	    out.println("<BODY>");


	    String nameMatch = null;
	    int idMatch,echeck = 0;
	    for(Iterator<Account> i = vecWrite.iterator();i.hasNext();){//iterate remove element with matching id
			Account account = (Account) i.next();
			idMatch = account.getAccountId();
			nameMatch = account.getAccountUserId();			
			out.println("<BR>");
			if(idMatch==delId){
				echeck++;
				i.remove();
			}
	    }//for
//

	    slogger.info("vecWrite after delete"+ vecWrite);//


	     if(echeck<0){//
			//slog
			slogger.info("No accounts matched to delete");//session log	    
	    		
			out.println("<h1>No matching accounts</h1>");
			out.println("<h1>Invalid Id<BR>");
			out.println("<h1><a href=\"index.html\"> back to login</a></h1>");
			out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
			out.println("<h1><a href=\"delAccount.html\"> back to delete account</a></h1>");
	     }else{
		tlog.write("user "+name+" deleted account "+delId+"\n");
		out.println("<BR> <h1>account "+delId+" deleted </h1>");//
		out.println("<h1><a href=\"transaction.html\"> back to transaction</a></h1>");
		out.println("<h1><a href=\"delAccount.html\"> back to delete account</a></h1>");
	     }


	    storeObject(vecWrite);
	    session.setAttribute("vecRead",vecWrite);//reset vector
      	
	    out.println("</BODY>");					
	    out.println("</HTML>");
    	    out.close();
	    fh.close();
	    tlog.close();
        }

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
        
    }
}

