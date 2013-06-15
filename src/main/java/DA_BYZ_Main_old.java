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

public class DA_BYZ_Main_old{
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

	public static List<String> getProcessListFromProperties() {

		String neighboursString = getPropertyByName("processes");
		String[] neighbourArray = neighboursString.split(";");
		List<String> neighbourList = Arrays.asList(neighbourArray);
 
    	return neighbourList;
	}

	public static List<String> getFaultyProcessListFromProperties() {

		String faultyProcess = getPropertyByName("processFaulty");
		String[] faultyArray = faultyProcess.split(";");
		List<String> faultyList = Arrays.asList(faultyArray);
 
    	return faultyList;
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

	public static List<String> removeFromTheList(List<String> list, String element) {

		int indexToRemove = list.indexOf(element);
		ArrayList<String> filteredList = new ArrayList<String>();
	    for (int i = 0; i < list.size(); i++) {
	    	if (i != indexToRemove) {
	    		filteredList.add(list.get(i));
	        }  
		}

	    return filteredList;

	}
	public static boolean checkFaultyProcess(String currentProcessName, List<String> faultyProcess){
		for (String p : faultyProcess){
			if (p.equals(currentProcessName))
				System.out.println(currentProcessName+" Process is faulty");
				return true;
		}
		return false;
		
	}

	public static void main(String[] args) throws RemoteException {
		boolean isFaulty = false;
		System.setSecurityManager(new RMISecurityManager());
		try{
			int id = Integer.parseInt(args[0]);
			int portNum = Integer.parseInt(args[1]);
	
			try {
				LocateRegistry.createRegistry(portNum);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// IP address of the system
			String ipAddress = getIp();
			List<String> processList = getProcessListFromProperties();
	
			List<String> faultyProcesses = getFaultyProcessListFromProperties();
	
			String processName = getPropertyByName("processName");
			String bindName = "/" + processName + id;
			String currentProcessName = "//" + ipAddress + bindName;	
			System.out.println("Current process name is " + currentProcessName);
			// Returns true if current process is faulty
			isFaulty = checkFaultyProcess(currentProcessName, faultyProcesses);
	
			//DA_BYZ byz = new DA_BYZ(id, currentProcessName, processList, faultyProcesses, isFaulty);
			//Naming.rebind(bindName, byz);
			// Broadcast messages
			//byz.startAlgo();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
		
	}
}