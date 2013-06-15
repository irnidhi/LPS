package main.java;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int count;
	private String sender;
	private String receiver;
	private int val;
	private List<Integer> path;
	
	public Message(int count, int order, String sender, String receiver){
		this.setCount(count);
		this.setSender(sender);
		this.setReceiver(receiver);
		this.setVal(order);
		this.setPath(new LinkedList<Integer>());
	}

	public List<Integer> getPath() {
		return path;
	}

	public void setPath(List<Integer> path) {
		this.path = path;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
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