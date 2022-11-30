import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import javax.servlet.*;
import javax.servlet.http.*;
import java.lang.*;

public class register extends HttpServlet {

private final static Logger slogger = Logger.getLogger(register.class.getName());//log
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

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");//
		PrintWriter out=response.getWriter();
		
		HttpSession session = request.getSession();//get session
		String sId =  request.getSession().getId();//slog
		ObjectInputStream objIs = null;
		ServletContext context = getServletContext();//webroot file path
		String logPath = getServletContext().getRealPath("/WEB-INF/logs/session.log");		  
		String tLogPath = getServletContext().getRealPath("/WEB-INF/transactions.txt");		  
		initSlog(logPath);//session log initializer
		
		File tfile = new File(tLogPath);//file check
		File lfile = new File(logPath);//file check		
		if(lfile.exists()){
			slogger.info("log file exists");			
	 	}else{
			lfile.createNewFile();
			slogger.info("log file created");
		}
		
		if(tfile.exists()){//init transaction log
			slogger.info("transaction file exists");
			BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));			
	 	}else{
			tfile.createNewFile();
			slogger.info("transaction file created");
		}

		slogger.info("Session Created:"+ sId);//session log

		Account currentAccount = new Account();//set param to currentAccount
	
		String name = request.getParameter("RaccountUserId");
		currentAccount.setAccountUserId(name);
		String RaccountType = (String) request.getParameter("RaccountType");
		currentAccount.setAccountType(request.getParameter("RaccountType"));
		double balance = Double.valueOf(request.getParameter("Rbalance"));
		String summary = null;//print accounts var
		int vecSize = 0;//vector position	
		if(name == null){//invalidate/logoff
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
		}else if(balance<0){
			summary = currentAccount.toString();			
			out.println("<HTML>");
			out.println("<HEAD>");
			out.println("<TITLE> Account error </TITLE>");///print the account selected
			out.println("<BODY>");
			out.println("<RIGHT>");
			out.println("<h1>Options</h1>"); 
			out.println("<a href=\"index.html\"> Back to Login</a> |");
			out.println("<a href=\"register.html\"> Back to register</a>");  
  			out.println("</RIGHT>");
			out.println("<CENTER>");
			out.println("<H1>Invalid balance amount:"+balance+" </H1>");// print account amount
			out.println("<CENTER>");
			out.println("</HEAD>");
			out.println("<BR><BR><BR>");			
		}
		
	BufferedWriter tlog = new BufferedWriter(new FileWriter(tLogPath,true));//tlog

		session.setAttribute("user", request.getParameter("RaccountUserId"));//keep account as instance
 		slogger.info(name+" User object created ");//session log

//read file to get vec size	    
	try {
            InputStream fileIs = context.getResourceAsStream("/WEB-INF/accountsDB.ser");// open for reading
            objIs = new ObjectInputStream(fileIs);// open objects in file for reading

	    @SuppressWarnings("unchecked")//vecRead...readObject();
            Vector<Account> vecRead = (Vector<Account>) objIs.readObject();//read object 
	    vecSize = vecRead.size();// set vec size
//log
	    String path = getServletContext().getRealPath("/WEB-INF/accountsDB.ser");//
            slogger.info("vector size from read: "+vecSize);//session log
            slogger.info("from string path: "+path);//session log	    
            slogger.info(" vecRead: "+vecRead);//session log	    

	    currentAccount.setAccountId(vecSize+1);//v position
	    int rId = vecSize+1;

//check if balance is 0
		if(balance >= 0){//check initial balance
			currentAccount.setBalance(Double.valueOf(request.getParameter("Rbalance")));
			summary = currentAccount.toString();			
			out.println("<HTML>");
			out.println("<HEAD>");
			out.println("<TITLE> Account Created </TITLE>");///print the account selected
			out.println("<BODY>");
			out.println("<RIGHT>");
			out.println("<h1>Options</h1>"); 
			out.println("<a href=\"index.html\"> Back to Login</a> |");
			out.println("<a href=\"transaction.html\"> Transaction</a>");  
  			out.println("</RIGHT>");
			out.println("<CENTER>");
			out.println("<H1>Account"+ rId +" Created successfully</H1>");// print account amount
			out.println("<BR>"+summary+"<BR>");
			out.println("<CENTER>");
			out.println("</HEAD>");
			out.println("<BR><BR><BR>");

		}	    
	    
    	    //log
	    slogger.info(" vecRead: "+vecRead);//session log	    
	    slogger.info("Account registered: "+summary);
	    tlog.write(name +" registered an account :"+summary+"\n");
	    //write new vector file 
	    vecRead.addElement(currentAccount);//add account object to vector to write
	    session.setAttribute("vecRead", vecRead);//set attr for session
	    storeObject(vecRead);		
//set vec Write as vec read
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
