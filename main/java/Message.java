package main.java;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sender;
	private String receiver;
	private String val;
	private List<Integer> path;
	

//	public Message(int count,String order, String sender, String receiver){
//		this.setCount(count);
//
//	public Message(int order, String sender, String receiver){
//
//		this.setSender(sender);
//		this.setReceiver(receiver);
//		this.setVal(order);
//		this.setPath(new LinkedList<Integer>());
//	}
	

//	public Message(int count, String order, String sender, String receiver, String path){
//		this.setCount(count);
//	}

	public Message(String order, String sender, String receiver, String path){

		this.setSender(sender);
		this.setReceiver(receiver);
		this.setVal(order);
		this.setPath(new LinkedList<Integer>());
		
		for (int i = 0; i < path.length(); i++) {
			char c = path.charAt(i);
			this.path.add(Character.getNumericValue(c));
		}
	}

	public static Message getDefaultMessage(int missingNode, List<Integer> pathAbove)
	{
		StringBuilder pathString = new StringBuilder();
		for (Integer pathInt : pathAbove) {
			pathString.append(pathInt);
		}
		pathString.append(missingNode);
		String pathStringReady = pathString.toString();
		Message defaultMessage = new  Message("1", "x", "x", pathStringReady);
		
		return defaultMessage;
	}
	public List<Integer> getPath() {
		return path;
	}

	public void setPath(List<Integer> path) {
		this.path = path;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String order) {
		this.val = order;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

}