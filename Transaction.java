
public class Transaction {
	
	//variables
	private String sender;
	private String receiver;
	private int amount;
	
	//constructor 
	public Transaction(String sender, String receiver, int amount) {
		this.sender = sender; 
		this.receiver = receiver; 
		this.amount = amount;
	}
	
	//getters 
	public String getSender() {
		return this.sender;
	}
	
	public String getReceiver() {
		return this.receiver;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	public String toString() {
		 return sender + ":" + receiver + "=" + amount;
	}

}
