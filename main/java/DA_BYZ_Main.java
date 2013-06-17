package main.java;


import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class DA_BYZ_Main{

	
	public static ArrayList<DA_BYZ> createByzantines(int noOfProcesses, int noOfTraitors) {
		String namePrefix = "//127.0.0.1/Process";		
		ArrayList<DA_BYZ> byzantineList = new ArrayList<DA_BYZ>();
		
		ArrayList<String> processNames = new ArrayList<String>();
		
		for (int i = 1; i <= noOfProcesses; i++) {
			String name = namePrefix + Integer.toString(i);
			processNames.add(name);
		}
		
		int i = 1;
		for (String name : processNames) {
			
			try {
			ArrayList<String> otherProcesses = new ArrayList<String>(processNames);
			otherProcesses.remove(name);
			DA_BYZ byzantineServer = new DA_BYZ(i++, name, otherProcesses, noOfProcesses, noOfTraitors);
			byzantineList.add(byzantineServer);
			
			bindComponentToTheName(name, byzantineServer);
			
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
			Thread thread = new Thread(byz, "Process " + byz.pId);
			thread.start();
		}
	}
	
	public static void goLocal() {
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		System.out.printf("registry started at ip %s and port number %d\n", "127.0.0.1", 1099);

		runAll(createByzantines(8, 2));

	}
	
	public static String getIp() {
		
		try {
			InetAddress IP = InetAddress.getLocalHost();
			String ipString = IP.getHostAddress();
			System.out.println("IP of my system is := " + ipString);
			return ipString;
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static String getPropertyByName(String name) {
		Properties prop = new Properties();

		try {
			// load a properties file
			prop.load(new FileInputStream("config.properties"));

			// get the property value and print it out

			String result = prop.getProperty(name);

			return result;

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	public static List<String> getProcessListFromProperties(String propertyName) {

		String neighboursString = getPropertyByName(propertyName);
		String[] neighbourArray = neighboursString.split(";");
		List<String> neighbourList = Arrays.asList(neighbourArray);
 
    	return neighbourList;
	}
	
	public static void goGlobal(int id, int portNum) {
		String ipAddress = getIp();
		String processName = getPropertyByName("processName");
		
		List<String> processesList = getProcessListFromProperties("processes");
		List<String> traitorList = getProcessListFromProperties("processFaulty");
		
		try {
			LocateRegistry.createRegistry(portNum);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		//System.out.printf("registry started at ip %s and port number %d\n", ipAddress, portNum);
			
		String bindName = "/" + processName + id;
		String currentProcessName = "//" + ipAddress + bindName;	
		System.out.println("Current process name is " + currentProcessName);
		
		ArrayList<String> otherProcesses = new ArrayList<String>(processesList);
		otherProcesses.remove(bindName);
		
		DA_BYZ byzantineServer = null;
		try {
			byzantineServer = new DA_BYZ(id, bindName, otherProcesses, processesList.size(), traitorList.size());
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			Naming.rebind(bindName, byzantineServer);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("bind to the name %s \n", bindName);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Thread thread = new Thread(byzantineServer);
		thread.run();
		
	}

	public static void main(String args[]){
		System.setSecurityManager(new RMISecurityManager());
		
		String mode = getPropertyByName("mode");
		
		String runMode = getPropertyByName("run");
		
		if (runMode.contains("STEP")) {
			OutputSettings.stepExecution = true;
		}
		else {
			OutputSettings.stepExecution = false;
		}
		
		
		if (mode.contains("LOCAL")) {
			
			System.out.println("Go local");
			goLocal();
		}
		else {
			int id = Integer.parseInt(args[0]);
			int portNum = Integer.parseInt(args[1]);
			goGlobal(id, portNum);
			
		}
		//messageTest();
		
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
		
		Message m1 = new Message("1", "x", "x", Arrays.asList(1));
		Message m2 = new Message("1", "x", "x", Arrays.asList(1, 5));
		Message m3 = new Message("1", "x", "x", Arrays.asList(1, 2));
		Message m33 = new Message("1", "x", "x", Arrays.asList(1, 4));
		Message m34 = new Message("1", "x", "x", Arrays.asList(1, 5, 2));
		Message m4 = new Message("1", "x", "x", Arrays.asList(1, 5, 4));
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
		root.majority(root);
	}
}