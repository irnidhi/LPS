package main.java;

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
			//root.fillLevelWithDefaultValues(currentRound+1, this.pId);
			
			currentRound++;
			System.out.println("Next round N " + this.pId + " R " + currentRound);
			
			if (currentRound <= noOfTraitors)
				broadcast();
		}
		
		root.majority(root);
		
		System.out.println("RESULT: " + root.getOutput() + " Finish N " + this.pId + " R " + currentRound);
	}
	
	
	public void processMessagesReceivedInCurrentRound() {
		List<Message> messagesReceivedCurrentRound = messagesReceived.get(currentRound);
		for (Message message : messagesReceivedCurrentRound) {
			root.saveMessage(message, 0);
		}
	}
	
	public synchronized String receive(Message msg) throws RemoteException {	
		System.out.println("Process " + this.pId + " received " + msg);
		int messageRound = msg.getPath().size() - 1;
		List<Message> messagesReceivedCurrentRound = messagesReceived.get(messageRound);
		messagesReceivedCurrentRound.add(msg);
		messagesReceived.set(messageRound, messagesReceivedCurrentRound);
		return null;
	}
	
	/**
	 * This function sends messages to all the running remote peer processes.
	 * @param neighbour remote destination process 
	 * @param msg message to be sent to neighbour
	 */
	public void sendMsg(String lieutenant, Message msg) {
		try {
			System.out.println("lookup");
			Remote robj = Naming.lookup(lieutenant);
			
			DA_BYZ_RMI processserver = (DA_BYZ_RMI) robj;
			
			System.out.println("Order msg sent from "+currProcess+" to "+lieutenant);
			//put the message in sending queue, removed only when ack is received

			processserver.receive(msg);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
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
				Message msg = new Message("0", this.currProcess, destProcess, path);
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
						
						if (this.pId < 5) {
							msgToSend.setVal("1");
						}
						messagesToSend.add(msgToSend);
					}
					
				}
//				String newPath = message.getStringPath() + Integer.toString(pId);
//				Message msgToSend = new Message(message.getVal(), this.currProcess, message.getReceiver(), newPath);
//				messagesToSend.add(msgToSend);
			}
		}
		
		return messagesToSend;
		
	}
	
	@Override
	public void run() {
		startAlgo();
	}	
}