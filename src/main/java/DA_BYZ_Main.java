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
		
		
		processes = Integer.parseInt(args[0]);
		noOftraitors = Integer.parseInt(args[1]);
		String traitors = args[2];
		traitorList = Arrays.asList(traitors.split(","));
		
		try {
			LocateRegistry.createRegistry(1099);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			runAll(createByzantines(processes, noOftraitors, traitorList));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}