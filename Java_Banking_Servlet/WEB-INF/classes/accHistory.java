/*

get login: user

transactions from file
get size, check if it contains user
get accounts 
 

*/

import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.String.*;

public class accHistory extends HttpServlet {

private final static Logger slogger = Logger.getLogger(accHistory.class.getName());//log
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

		response.setContentType("text/html");//print html
		PrintWriter out=response.getWriter();//set print to out

		HttpSession session = request.getSession(false);//request session set as session			
		String sId =  request.getSession().getId();//slog
		String name = (String) session.getAttribute("user");// 		
		ServletContext context = getServletContext();//webroot file path
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");	
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");		  
		initSlog(logPath);//session log initializer
		BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

		
//search vector for matching names 
	
	String nameMatch = null;
			
///
	    out.println("<HTML>");
	    out.println("<HEAD>");
	    out.println("<TITLE> Accounts History </TITLE>");///print the account selected	    	  
	    out.println("<BODY>");
	    out.println("<h1> Account History</h1>");	
	    out.println("<h1><a href=\"transaction.html\"> back to transactions</a></h1>");
//read txt file
//parse user names 
//print out account actions
	File file = new File(tLogPath);
	Scanner in = null;
	String start, nameStart;

	if(name.length() > 3){
		nameStart = name.substring(0,3);
	}else{
		nameStart = name;					
	}
	

        try {
            in = new Scanner(file);
            while(in.hasNext()){
                String line=in.nextLine();
		out.println("<BR>");

		if(line.length() > 3){
			start = line.substring(0,3);
			if(start.compareTo(nameStart)==0){
				out.println(line);
			}	
		}else{
			start = line;					
		}

          }//while
        } catch (FileNotFoundException e) {
	    slogger.info("transactions.txt does not exist");
            e.printStackTrace();
        }

	out.println("</BODY>");
	out.println("</HTML>");
	out.close();
	fh.close();
	tlog.close();
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
