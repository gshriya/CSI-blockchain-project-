import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Random;

public class Block {
	
	 private int index; // the index of the block in the list
	 private java.sql.Timestamp timestamp; // time at which transaction has been processed
	 private Transaction transaction; // the transaction object
	 private String nonce; // random string (for proof of work)
	 private String previousHash; // previous hash (in first block, set to string of zeroes of size of complexity "00000")
	 private String hash; // hash of the block (hash of string obtained from previous variables via toString() method)
	 
	 public Block(int index, Timestamp timestamp, Transaction transaction, String nonce, String previousHash, String hash) {
		 this.index = index;
		 this.transaction = transaction;
		 this.nonce=nonce;
		 this.previousHash=previousHash;
		 this.timestamp = timestamp;
		 this.hash = hash;
	 }

	Block(int index, Transaction transaction, String previousHash) {
		this.index = index;
		timestamp = new Timestamp(System.currentTimeMillis());
		this.transaction = transaction;
		this.nonce = "";
		this.previousHash = previousHash;
		try {
			this.hash = Sha1.hash(toString());
		} catch (Exception e) {
			System.out.println("Something is wrong with the generated hash.");
		}
	}
	 
	 public int getIndex() {
		 return this.index;
	 }
	 
	 public Timestamp getTimestamp() {
		 return this.timestamp;
	 }
	 
	 public Transaction getTransaction() {
		 return this.transaction;
	 }
	 
	 public String getNonce() {
		 return this.nonce;
	 }
	 
	 public String getPreviousHash() {
		 return this.previousHash;
	 }
	 
	 public String getHash() {
		 return this.hash;
	 }
	 
	 //creating a new nonce
	 public void generateNonce() {
		 this.nonce = "";
		 Random random = new Random();
		 try {
			 while (!Sha1.hash(toString()).startsWith("00000")) { //hash must start with 5 zeros 
				 this.nonce = "";
	             for (int i = 0; i < 25; i++) {
	            	//added 33 to offset the minimum value from 0 to 33
	            	 char ch = (char)(random.nextInt(126-33+1)+33); 
	            	 nonce += ch; //concatenating it to the nonce to create a new nonce 
	             }
			 }
		 }
		 catch (Exception e) {
			 System.out.println("Failed");
		 }
	 }
	 
	 //calculate how many hashes it takes to generate a valid hash  
	 public String generateHash() {
		 int num =0;
		 this.nonce="";
		 String previousHash = "00000";
	     String character= "";
	     for (int ch = 32; ch<123; ch++){
	    	 character += (char)ch;
	     }
	     try {
	    	 hash = Sha1.hash(toString());
	         while(!hash.substring(0,5).equals(previousHash)) {
	        	 Random rand = new Random();
	             StringBuilder builder = new StringBuilder(14);
	             for(int i = 0; i < 14; i++){
	            	 builder.append(character.charAt( rand.nextInt(character.length()) ) );
	            	 num++;
	             }
	             	nonce = builder.toString();
	                hash = Sha1.hash(toString());
	            }
	        }
	        catch (UnsupportedEncodingException e){
	            System.out.println("error");
	        }
	     	System.out.println("Number of nonce iterations: "+num);
	        return hash;
	 }

	 public String toString() {
		 return timestamp.toString() + ":" + transaction.toString() + "." + nonce+ previousHash;
	 }

}
