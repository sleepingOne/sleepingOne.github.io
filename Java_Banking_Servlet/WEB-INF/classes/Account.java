import java.io.Serializable;

public class Account implements Serializable {
	
	private String accountUserId;
	private String accountType;
	private int accountId;		
	private double balance;
	
        public Account() {
        
        }

	public Account(String accountUserId, String accountType, int accountId, double balance) {
        this.accountUserId = accountUserId;
        this.accountType = accountType;
	this.accountId = accountId;
        this.balance = balance;
        } 

	public void setAccountUserId(String accountUserId) {
		this.accountUserId = accountUserId; 
	}

	public String getAccountUserId() {
		return accountUserId;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType; 
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public double getBalance() {
		return balance;
	}

	public String toString() {
		return "accountUserID: "+ accountUserId + " accountType: " + accountType + " accountId: " + accountId + " balance: $" + balance;
	}

}


	
