

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.sun.xml.internal.ws.api.message.Messages;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class DA_BYZ extends UnicastRemoteObject implements DA_BYZ_RMI, Runnable{
//	private static Log log = LogFactory.getLog(DA_BYZ.class);
	public int pId;
	private String currProcess;
	private List<String> processList;

	private int noOfProcesses;
	private int noOfTraitors;

	private Node root;
	private int currentRound;
	
	private static final int WAIT_TIME = 1000;
	
	private List<List<Message>> messagesToSend;
	private List<List<Message>> messagesReceived;
	
	
	protected DA_BYZ(int pId, String processName, List<String> processList, int noOfProcesses, int noOfTraitors) throws RemoteException {
		super();
		this.pId = pId;
		this.currProcess = processName;
		this.processList = processList;
		
		this.noOfProcesses = noOfProcesses;
		this.noOfTraitors = noOfTraitors;
		
		this.root = new Node(this.noOfProcesses);
		currentRound = 0;
		
		
		messagesToSend = new ArrayList<List<Message>>();
		for (int i = 0; i < noOfTraitors + 1; i++) {
			List<Message> sendRound = new ArrayList<Message>();
			messagesToSend.add(sendRound);
		}
		
		messagesReceived = new ArrayList<List<Message>>();
		for (int i = 0; i < noOfTraitors + 1; i++) {
			List<Message> receiveRound = new ArrayList<Message>();
			messagesReceived.add(receiveRound);
		}
	}
	
	public void startAlgo(){
		System.out.println("Start of node " + this.pId);
		if (pId == 1){
			broadcast();
		}
		
		while (currentRound < noOfTraitors + 1)
		{
			try {
				Thread.sleep(WAIT_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			processMessagesReceivedInCurrentRound();
			if (this.pId != 1)
			{
				//root.fillLevelWithDefaultValues(currentRound+1, this.pId);
			}
			
			
			List<Message> messagesReceivedCurrentRound = messagesReceived.get(currentRound);
			//System.out.println("Proc " + pId + " msg rec " + messagesReceivedCurrentRound.size());
			currentRound++;
			//System.out.println("Next round N " + this.pId + " R " + currentRound);
			//pauseProg();
			if (currentRound <= noOfTraitors)
				broadcast();
		}
		
		root.majority(root);
		
		//root.majority2(noOfTraitors+1);
		
		
		System.out.println("RESULT: " + root.getOutput() + " Finish N " + this.pId + " R " + currentRound);
	}
	
	
	public void processMessagesReceivedInCurrentRound() {
		synchronized(messagesReceived) {
			List<Message> messagesReceivedCurrentRound = messagesReceived.get(currentRound);
		for (Message message : messagesReceivedCurrentRound) {
			root.saveMessage(message, 0);
		}
		}
		
	}
	
	public synchronized String receive(Message msg) throws RemoteException {	
		//System.out.println("Process " + this.pId + " received " + msg);
		int messageRound = msg.getPath().size() - 1;
		if (messageRound != this.currentRound) {
			//System.out.println("Message " + msg + " coming in round " + this.currentRound);
		}
		List<Message> messagesReceivedCurrentRound = messagesReceived.get(messageRound);
		messagesReceivedCurrentRound.add(msg);
		synchronized(messagesReceived) {
			messagesReceived.set(messageRound, messagesReceivedCurrentRound);
		}
		
		return null;
	}
	
	/**
	 * This function sends messages to all the running remote peer processes.
	 * @param neighbour remote destination process 
	 * @param msg message to be sent to neighbour
	 */
	public void sendMsg(String lieutenant, Message msg) {
		try {
			//System.out.println("lookup");
			Remote robj = Naming.lookup(lieutenant);
			
			DA_BYZ_RMI processserver = (DA_BYZ_RMI) robj;
			
			//System.out.println("Order msg " + msg);
			//put the message in sending queue, removed only when ack is received

			processserver.receive(msg);
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	 /** 
	  * This function broadcasts messages to all the other running lieutenant processes in case of General or broadcasts to other
	  * lieutenants in case of a sender lieutenant
     */	
	public void broadcast() {

		List<Message> messagesToSendInCurrentRound = generateMessagesToSend();
		for (Message message : messagesToSendInCurrentRound) {
			sendMsg(message.getReceiver(), message);
		}
		this.messagesToSend.set(currentRound, messagesToSendInCurrentRound);		
	}

	public List<Message> generateMessagesToSend() {
		List<Message> messagesToSend = new ArrayList<Message>();
		
		if (currentRound == 0) {
			for (String destProcess : processList){
				List<Integer> path = new LinkedList<Integer>();
				path.add(pId);
				Message msg = new Message("1", this.currProcess, destProcess, path);
				double random = Math.random();
				if (random < 0.5)
				{
					msg.setVal("1");
				}
				else
				{
					msg.setVal("0");
				}
				messagesToSend.add(msg);
			}
		}
		else {
			List<Message> messagesReceivedPreviousRound = messagesReceived.get(currentRound-1);
			for (Message message : messagesReceivedPreviousRound) {
				for (String destProcess : processList){
					int destProcessNumber = Integer.parseInt(destProcess.split("Process")[1]);
					List<Integer> oldPath = message.getPath();
					if (!oldPath.contains(destProcessNumber))
					{
						List<Integer> newPath = new LinkedList<Integer>(oldPath);
						newPath.add(pId);
						
						Message msgToSend = new Message(message.getVal(), this.currProcess, destProcess, newPath);
						
						if (this.pId > noOfProcesses-OutputSettings.noOfFaultyProcesses+1) 
						{
							//System.out.println("Faulty");
							if (OutputSettings.randomValue)
							{
								double random = Math.random();
								if (random < 0.5)
								{
									msgToSend.setVal("1");
								}
								else
								{
									msgToSend.setVal("0");
								}
							}
							else
							{
								msgToSend.setVal("0");
							}
							
							if (OutputSettings.failedSendMsgs)
								if (OutputSettings.randomSending)
								{
									
									
									double random = Math.random();
									if (random < 0.5)
									{
										messagesToSend.add(msgToSend);
									}
								}
								else
								{
									messagesToSend.add(msgToSend);
								}
							
							
							
						}
						else
						{
							messagesToSend.add(msgToSend);
						}
						
					}
					
				}
//				String newPath = message.getStringPath() + Integer.toString(pId);
//				Message msgToSend = new Message(message.getVal(), this.currProcess, message.getReceiver(), newPath);
//				messagesToSend.add(msgToSend);
			}
		}
		
		return messagesToSend;
		
	}
	
	public void pauseProg() {
		System.out.printf(">>NODE %d ROUND: %d\n", this.pId, this.currentRound);
		List<Message> msgSendInPrvRound = messagesToSend
				.get(this.currentRound - 1);
		System.out.printf("Messages sent in previous round:\n"
				+ msgSendInPrvRound.toString() +"\n");
		List<Message> msgRcvdInPrvRound = messagesReceived
				.get(this.currentRound - 1);
		System.out.printf("Messages received in previous round:\n"
				+ msgRcvdInPrvRound.toString() +"\n");

		if (OutputSettings.stepExecution) {
			System.out.println("Press enter to continue...");
			Scanner keyboard = new Scanner(System.in);
			keyboard.nextLine();
		}

	}

	@Override
	public void run() {
		startAlgo();
	}	
}