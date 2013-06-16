package main.java;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

public class DA_BYZ extends UnicastRemoteObject implements DA_BYZ_RMI, Runnable{
//	private static Log log = LogFactory.getLog(DA_BYZ.class);
	private int pId;
	private String currProcess;
	private List<String> processList;
	private int noOfProcesses;
	private List<String> faultyProcessList;
	private int noOfFaulty;
	private int rounds;
	private boolean isFaulty = false;
	private boolean isGeneral = false;
	// Received messages from other lieutenants
	private Map<List<Integer>, Message> recvdMsgs = new HashMap<List<Integer>,Message>();
	//After sending the message remove from this
	private Map<List<Integer>, Message> sentMsgs = new HashMap<List<Integer>,Message>();
	// Add received messages to evaluation list for later processing
	private Map<List<Integer>, Message> evalMsgs = new HashMap<List<Integer>,Message>();
	private int msgSentCounter=0;
	
	private ProcessState pState;
	private static final int WAIT_TIME = 1000;
	
	protected DA_BYZ(int pId, String processName, int noOfProcesses, int noOfTraitors, List<String> tList) throws RemoteException {
		super();
		this.pId = pId;
		this.currProcess = processName;
		//this.processList = processList;
		this.noOfProcesses = noOfProcesses;
		this.faultyProcessList = tList;
		this.noOfFaulty = faultyProcessList.size();
		this.rounds = faultyProcessList.size() + 1;
		//this.isFaulty = faulty;
		// By default General is the first process.
		if (pId == 1){
			this.isGeneral = true;
		}
		this.pState = ProcessState.SAFE;
	}
	
	public void startAlgo(){
		// initialize state of process and then broadcast based on it
		// after every round check for state of process, if SAFE broadcast, if WAITING, wait for acknowledgments
		// if RETIRED do not broadcast.
		// Set order value to be broadcasted based on fault pattern, if the process is Faulty
		
		String order = "0";			//order value hard-coded
		System.out.println("order value of "+pId+"is"+order);
		//log.debug(pState);
		System.out.println("No. of rounds:"+rounds);
		for (int r=0; r<rounds; r++){
			if (r == 0 && pId == 1){
				broadcast(order);
				break;
			}
			System.out.println(pState);
			//log.debug(pState);
			if (pState.name() == "WAITING"){
				try {
				    //thread to sleep for the specified number of milliseconds
				    Thread.sleep(WAIT_TIME);
				    
				} catch ( java.lang.InterruptedException ie) {
				    System.out.println(ie);
			}
			if (pState.name() == "SAFE"){
				System.out.println("process is safe, calling broadcast for process:" +pId);
				broadcast(order);
			}
	
			}
		}
		

	}
	
	public void sendAckMsg(Message m) throws MalformedURLException, RemoteException, NotBoundException{
		AckMessage aMsg = new AckMessage();
		aMsg.setCount(m.getCount());
		aMsg.setReceiver(m.getSender());
		aMsg.setSender(currProcess);
		
		Remote rObj = Naming.lookup(m.getSender());
		
		DA_BYZ_RMI receiver = (DA_BYZ_RMI) rObj;
		receiver.recvAckMsg(aMsg);
		
	}
	
	public synchronized void recvAckMsg(AckMessage m){
		// if received ack message has the same count as the sent order message then delete it from the sending queue.
		// when sending queue is empty set the state of process as SAFE
		// if process is commander and the queue is empty set the state as RETIRED
		for (Message sentMsg : sentMsgs.values()) {
			if (sentMsg.getCount() == m.getCount() ){
				sentMsgs.remove(sentMsg.getPath());
			}	
		}
		if (sentMsgs.isEmpty()){
			if (isGeneral)
				pState = ProcessState.RETIRED;
			else
				pState = ProcessState.SAFE;
		}
		
	}
	
	public synchronized String receive(Message m) throws RemoteException {
		
		recvdMsgs.put(m.getPath(), m);
		try {
			sendAckMsg(m);
		} catch (MalformedURLException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * This function sends messages to all the running remote peer processes.
	 * @param neighbour remote destination process 
	 * @param msg message to be sent to neighbour
	 */
	@Override
	public void sendMsg(String lieutenant, Message msg) {
		try {
			Remote robj = Naming.lookup(lieutenant);
			
			DA_BYZ_RMI processserver = (DA_BYZ_RMI) robj;
			
			msg.setCount(msgSentCounter++);
			System.out.println("Order msg sent with counter:" + msgSentCounter+"from "+currProcess+"to"+lieutenant);
			//put the message in sending queue, removed only when ack is received
			sentMsgs.put(msg.getPath(), msg);
			
			processserver.receive(msg);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	 /** 
	  * This function broadcasts messages to all the other running lieutenant processes in case of General or broadcasts to other
	  * lieutenants in case of a sender lieutenant
     */	
	public void broadcast(String order) {
		
		System.out.print("Inside broadcast");
		
		// after broadcasting set the state of process as WAITING
		pState = ProcessState.WAITING;
		//Message msg;
		for (String destProcess : processList){

			if (recvdMsgs.isEmpty()){
				Message	msg = new Message(msgSentCounter, order, currProcess, destProcess);
				List<Integer> path = new LinkedList<Integer>();
				path.add(pId);
				msg.setPath(path);
				sendMsg(destProcess, msg);
			}
			else{
				
				for (Message m : recvdMsgs.values()) {
					List<Integer> path = m.getPath();
					Message	msg = new Message(msgSentCounter, order, currProcess, destProcess);
					// ? what about the count
					
					// append process Id to path where allowed
					if (!path.contains(pId)){
						path.add(pId);
						msg.setPath(path);
						sendMsg(destProcess, msg);
					}
					
				}
			}
			
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}	
}