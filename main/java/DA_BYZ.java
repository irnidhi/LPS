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

	private int rounds;

	private static final int WAIT_TIME = 1000;
	
	protected DA_BYZ(int pId, String processName, int noOfProcesses, int noOfTraitors, List<String> tList) throws RemoteException {
		super();
		this.pId = pId;
		this.currProcess = processName;

	}
	
	public void startAlgo(){
		
		String order = "0";			//order value hard-coded
		System.out.println("order value of "+pId+"is"+order);
		//log.debug(pState);
		System.out.println("No. of rounds:"+rounds);
		for (int r=0; r<rounds; r++){
			if (r == 0 && pId == 1){
				broadcast(order);
				break;
			}
			//System.out.println(pState);
			//log.debug(pState);
			//if (pState.name() == "WAITING"){
				try {
				    //thread to sleep for the specified number of milliseconds
				    Thread.sleep(WAIT_TIME);
				    
				} catch ( java.lang.InterruptedException ie) {
				    System.out.println(ie);
			}
			//if (pState.name() == "SAFE"){
				System.out.println("process is safe, calling broadcast for process:" +pId);
				broadcast(order);
			}
	}
	
	

	
	public synchronized String receive(Message m) throws RemoteException {
		
		
	
		return null;
	}
	
	/**
	 * This function sends messages to all the running remote peer processes.
	 * @param neighbour remote destination process 
	 * @param msg message to be sent to neighbour
	 */
	public void sendMsg(String lieutenant, Message msg) {
		try {
			Remote robj = Naming.lookup(lieutenant);
			
			DA_BYZ_RMI processserver = (DA_BYZ_RMI) robj;
			
			System.out.println("Order msg sent from "+currProcess+"to"+lieutenant);
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
	public void broadcast(String order) {
		
		System.out.print("Inside broadcast");
		

		for (String destProcess : processList){

			
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}	
}