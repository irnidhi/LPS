package main.java;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DA_BYZ_Main{
	public static int noOftraitors;
	public static int processes;
	public static List<String> traitorList;
	
	public static int getTraitors(){
		return noOftraitors;
	}
	
	public static ArrayList<DA_BYZ> createByzantines(int noOfProcesses, int noOfTraitors, List<String> tList) throws RemoteException {
		String namePrefix = "//127.0.0.1/Process";		
		ArrayList<DA_BYZ> byzantineList = new ArrayList<DA_BYZ>();
		
		for (int i = 1; i <= noOfProcesses; i++) {
			
			String name = namePrefix + Integer.toString(i);	
			DA_BYZ byzantineServer = new DA_BYZ(i, name, noOfProcesses, noOfTraitors, tList);
			bindComponentToTheName(name, byzantineServer);
			
			byzantineList.add(byzantineServer);
			

		}

		return byzantineList;
	}

	public static void bindComponentToTheName(String bindName, DA_BYZ process) {
	
		try {
			Naming.rebind(bindName, process);
			} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("bind to the name %s \n", bindName);
		
	
	}

	
	public static void runAll(ArrayList<DA_BYZ> byzantines) {
		for (DA_BYZ byz : byzantines) {
			Thread thread = new Thread(byz);
			thread.start();
		}
	}


	public static void main(String args[]){
		
		messageTest();
//		processes = Integer.parseInt(args[0]);
//		noOftraitors = Integer.parseInt(args[1]);
//		String traitors = args[2];
//		traitorList = Arrays.asList(traitors.split(","));
//		
//		try {
//			LocateRegistry.createRegistry(1099);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		try {
//			runAll(createByzantines(processes, noOftraitors, traitorList));
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}

	private static void messageTest() {
		
//		Message m1 = new Message(0, "0", "x", "x", "1");
//		Message m2 = new Message(0, "0", "x", "x", "12");
//		Message m3 = new Message(0, "0", "x", "x", "13");
//		Message m4 = new Message(0, "1", "x", "x", "14");
//		Message m5 = new Message(0, "X", "x", "x", "123");
//		Message m6 = new Message(0, "X", "x", "x", "124");
//		//Message m7 = new Message(0, "1", "x", "x", "125");
//		//Message m8 = new Message(0, "1", "x", "x", "126");
//		//Message m5 = new Message(0, "1", "x", "x", "02");
//		//Message m6 = new Message(0, "0", "x", "x", "031");
//		
//		// levels should start from 0
//		Node root = new Node();
//		root.saveMessage(m1, 1);
//		root.saveMessage(m2, 1);
//		root.saveMessage(m3, 1);
//		root.saveMessage(m4, 1);
//		root.saveMessage(m5, 1);
//		root.saveMessage(m6, 1);
//		//root.saveMessage(m7, 2);
//		//root.saveMessage(m8, 2);
//		//root.saveMessage(m5, 1);
//		//root.saveMessage(m6, 1);
		
		
		//Majority calculation function
//		root.majority(root);
		
		Message m1 = new Message("1", "x", "x", "1");
		Message m2 = new Message("1", "x", "x", "15");
		Message m3 = new Message("1", "x", "x", "12");
		Message m33 = new Message("1", "x", "x", "14");
		Message m34 = new Message("1", "x", "x", "152");
		Message m4 = new Message("1", "x", "x", "154");
//		Message m5 = new Message(1, "x", "x", "02");
//		Message m6 = new Message(1, "x", "x", "021");
//		Message m7 = new Message(1, "x", "x", "022");
		
		
		Node root = new Node(6);
		root.saveMessage(m1, 1);
		root.saveMessage(m2, 1);
		root.saveMessage(m3, 1);
		root.saveMessage(m33, 1);
//		root.saveMessage(m5, 1);
//		root.saveMessage(m6, 1);
//		root.saveMessage(m7, 1);
		
		root.saveMessage(m34, 1);
		root.saveMessage(m4, 1);
		root.fillLevelWithDefaultValues(1, 3);
		root.fillLevelWithDefaultValues(2, 3);
		List<Node> nodes = root.getNodesFromLevel(3);
//		root.majority(root);
	}
}