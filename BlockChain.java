import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.sql.Timestamp;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.File;

public class BlockChain {
	//class instance 
	private ArrayList<Block> blockChain;

	//constructor that creates a Block type array list
	public BlockChain(ArrayList<Block> blockChain) {
		this.blockChain=blockChain;
	}
	
	public static BlockChain fromFile(String fileName) {
		ArrayList<Block> blockChain = new ArrayList<Block>();
		try {
			File file = new File(fileName);
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(file);
            String previousHash="00000";
            // variable that stores the block value 
            int index = 0;
            //reads the 6 other blocks after the index from the blockchain.txt file
            while (sc.hasNextLine()) { //scan it if the file has a next line, then read it 
                String [] blocks = new String [7];
                for (int i=0; i < 7; i++) {
                    blocks[i] = sc.nextLine();
                }
                Timestamp timestamp = new Timestamp(Long.valueOf(blocks[1]));
                Transaction transaction = getTransaction(blocks[2], blocks[3], blocks[4]); //this is the sender, receiver and amount
                String nonce = blocks[5];
                String hash = blocks[6];
                blockChain.add(index, new Block(index, timestamp, transaction, nonce, previousHash, hash)); //adding each of the blocks to the blockchain
                previousHash = hash; 
                index++;
            }
        } 
		catch (FileNotFoundException e) {
            System.out.println("File doesn't exist");
        }
        return new BlockChain(blockChain); //return of type BlockChain
	}
	
	private static Transaction getTransaction(String sender, String receiver, String amount) {
		
		//not valid cases. If there is no sender, receiver or amount being exchanged there will be no transaction 
        if(sender == null || receiver == null || sender.isEmpty() || receiver.isEmpty() || amount == null) { 
            return null;
        }
        try {
            int amount1 = Integer.parseInt(amount); //converting string to an int value as the original value of amount is an int
            return new Transaction(sender, receiver, amount1);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
	
	public void toFile(String fileName) {

        List<String> blockList = new ArrayList<>(); //creating a new list to store blocks  
        for (int i = 0; i < blockChain.size(); i++) {
            Block blocks = blockChain.get(i);
            String index = String.valueOf(blocks.getIndex());
            String timeStamp = String.valueOf(blocks.getTimestamp().getTime());
            String sender = blocks.getTransaction().getSender();
            String receiver = blocks.getTransaction().getReceiver();
            String amount = String.valueOf(blocks.getTransaction().getAmount());
            String nonce = blocks.getNonce();
            String hash = blocks.getHash();
            
            //adding each of the elements of a block into the list 
            blockList.add(index);
            blockList.add(timeStamp);
            blockList.add(sender);
            blockList.add(receiver);
            blockList.add(amount);
            blockList.add(nonce);
            blockList.add(hash);
        }
        try {
            Path file = Paths.get(fileName);
            Files.write(file, blockList, Charset.forName("UTF-8"));
        } catch (IOException e) {
            System.out.println("Unable to write");
        }
	}
	
	public boolean validateBlockchain() {
        ArrayList<Block> tmpblockChain1 = new ArrayList<Block>();
        BlockChain tmpblockChain = new BlockChain(tmpblockChain1);
        String previousHash = "00000";
        for (int i = 0; i < blockChain.size(); i++) {
            System.out.println(i);
            Block blocks = blockChain.get(i);
            String sender = blocks.getTransaction().getSender();
            tmpblockChain.add(blocks);
            int amnt = tmpblockChain.getBalance(sender);
            System.out.println(sender+ " has balance "+ amnt);
            try {
                if (!blocks.getHash().equals(Sha1.hash(blocks.toString()))) { //comparing the hash values calculated 
                    System.out.println("Invalid hash");
                    return false;
                }
            } 
            catch (Exception e) {
                System.out.println("Error found for hash");
            }
            if (amnt < 0 && !sender.equals("bitcoin")) { //verifies if the sender has enough money
                System.out.println("Sender does not have enough balance");
                return false;
            }
            if (blocks.getIndex() != i) { //comparing the previous index 
                System.out.println("Not the right position");
                return false;
            }
            if (!blocks.getPreviousHash().equals(previousHash)) { //comparing hash values 
                System.out.println("Invalid previous hash");
                return false;
            }
            previousHash = blocks.getHash(); //update the previous hash value
        }

        return true;
	}
	
	public int getBalance(String username) {
		int chargedAmount= 0;
		Iterator<Block> iterator = blockChain.iterator();
        while (iterator.hasNext()) {
            Block block = iterator.next();
            String receiver = block.getTransaction().getReceiver();
            String sender = block.getTransaction().getSender();
            int amount = block.getTransaction().getAmount();
            if (sender.equals(username) && !sender.equals("bitcoin")) { //if the sender is equal to the username, then decrease the charged amount
                chargedAmount -= amount;
            }
            if (receiver.equals(username)) { //if the receiver is equal to the username, then increase the charged amount  
                chargedAmount += amount;
            }
            if (chargedAmount < 0 && !sender.equals("bitcoin")) { //verifies if the sender has enough money
                System.out.println("Sender does not have enough money to proceed witht the transaction");
            }
        }
        return chargedAmount;
	}

    public int size() {

            return blockChain.size();

    }


	public void add(Block block) {
		//adds the blocks to the blockchain 
		blockChain.add(block);
	}
	
	public static void main(String[] args) {
	    String file = "OmarSiage.txt";
        BlockChain BlockChain = fromFile(file);
        BlockChain.validateBlockchain();
        int input = 1;
        while (input==1){
            Scanner scan = new Scanner(System.in);
            System.out.println("Press 1 for Transaction, 2 for Exit");
            input = scan.nextInt();
            if (input==1){
                System.out.println("Enter sender: ");
                String sender = scan.next();
                System.out.println("Enter receiver: ");
                String receiver = scan.next();
                System.out.println("Enter amount: ");
                int amount = scan.nextInt();
                if (BlockChain.getBalance(sender) < amount && !sender.equals("bitcoin")){
                    System.out.println("Invalid Transaction");
                }
                else{
                    Transaction transaction = new Transaction(sender, receiver, amount);
                    String prevHash = (BlockChain.blockChain.get(BlockChain.blockChain.size()-1)).getHash();
                    Block block = new Block(BlockChain.size(),transaction, prevHash);
                    block.generateNonce();
                    System.out.println(block.generateHash());
                    BlockChain.add(block);
                }
            }
        }
        BlockChain.toFile(file);

	}
	
}
