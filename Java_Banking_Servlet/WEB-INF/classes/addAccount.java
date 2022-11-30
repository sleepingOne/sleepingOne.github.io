import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.*;

public class addAccount extends HttpServlet {

private final static Logger slogger = Logger.getLogger(addAccount.class.getName());//log
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
		HttpSession session = request.getSession(false);//get session
		  String sId = request.getSession().getId();//slog
		ServletContext context = getServletContext();//webroot file path  
		  String name = (String) session.getAttribute("user");//get user from session		
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");	
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");		  
		initSlog(logPath);//session log initializer
		BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

//set param to new account	
		Account currentAccount = new Account();///
		currentAccount.setAccountUserId(name);//user object converted to string
		currentAccount.setAccountType(request.getParameter("RaccountType"));
		double balance = Double.valueOf(request.getParameter("Rbalance"));//param check
		int vecSize = 0;

//get vecRead attribute from current session		
	    @SuppressWarnings("unchecked")//vecRead...readObject();  
	    Vector<Account> vecWrite = (Vector<Account>) session.getAttribute("vecRead");
	    vecSize = vecWrite.size();
            currentAccount.setAccountId(vecSize+1);//set v position/unique id
	        	    	
			out.println("<HTML>");
			out.println("<HEAD>");
//debug
			out.println("<BR> sId: " + sId);//slog
//

		if(balance >= 0){//check initial balance
			currentAccount.setBalance(balance);
			String summary = currentAccount.toString();			
			//tlog	    
			tlog.write(name+" added  new account "+ summary+"\n");
			out.println("<TITLE> New Account Created </TITLE>");///print the account selected
			out.println("<BODY>");
			out.println("<RIGHT>");
			out.println("<h1>Login Options</h1>"); 
			out.println("<a href=\"index.html\"> Back to Login</a> |");
			out.println("<a href=\"addAccount.html\"> Back to add account</a>|");
			out.println("<a href=\"transaction.html\"> transaction</a>");  
  			out.println("</RIGHT>");
			out.println("<CENTER>");
			out.println("<BR>");
			out.println("<h1>Account created</h1><BR>");
			out.println("<h2>"+ summary+"<h2/>");// print account amount
			out.println("<CENTER>");
			out.println("</HEAD>");
			out.println("<BR><BR><BR>");

		}else{
			//slog
			slogger.info("Account add error");
		
			out.println("<TITLE> Account add error </TITLE>");///print the account selected
			out.println("<BODY>");
			out.println("<RIGHT>");
			out.println("<h1>Login Options</h1>"); 
			out.println("<a href=\"index.html\"> Back to Login</a> |");
			out.println("<a href=\"addAccount.html\"> Back to add account</a>");
			out.println("<a href=\"transaction.html\">Transaction</a>");  
  			out.println("</RIGHT>");
			out.println("<CENTER>");
			out.println("<H1> Invalid amount</H1>");// print account amount
			out.println("<CENTER>");
			out.println("</HEAD>");
			out.println("<BR><BR><BR>");			
		}

		

	    vecWrite.addElement(currentAccount);//add account object to vector to write
	    storeObject(vecWrite);
	    session.setAttribute("vecRead",vecWrite);//reset vector
//
	    slogger.info("accounts after delete" + vecWrite);//slog
//
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
                slogger.info("Object input stream error");//session log

            }
        }
        
    }
}
