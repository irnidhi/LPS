package main.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * This class has the contents for every node in the message tree.
 * Node has a parent, list of its children, and its own input and 
 * output values
 */
public class Node{
	
	private Integer id;
	private Integer level;
	private Node parent;
	private Map<Integer, Node> children;
	
	private int input;
	private int output;
	private List<Integer> path;
	
	boolean isSaved;
	
	public Node() {
		super();
		this.id = 0;
		this.level = 0;
		this.parent = null;
		this.children = new HashMap<Integer, Node>();
		this.input = 0;
		this.output = 0;
		this.path = new LinkedList<Integer>();
		
		this.isSaved = false;
	}
	
	public Node(Node parent) {
		this.level = parent.level + 1;
		this.parent = parent;
		this.children = new HashMap<Integer, Node>();

		this.isSaved = false;
	}
	
	public void saveMessageInCurrentNode(Message msg)
	{
//		this.id = this.path.get(this.path.size()-1);
		this.input = msg.getVal();
		this.path = msg.getPath();

		this.isSaved = true;
	}
	
	public void saveMessage(Message msg, int level)
	{
		 List<Integer> msgPath = msg.getPath();
		 
		 if (msgPath.size()-1 == this.level)
		 {
			 saveMessageInCurrentNode(msg);
		 }
		 else if (msgPath.size() - 2 == this.level)
		 {
			 int nextNodeIndex = msgPath.get(msgPath.size()-1);
			 if (!children.containsKey(nextNodeIndex))
			 {
				 children.put(nextNodeIndex, new Node(this));
			 }
			 Node newNode = children.get(nextNodeIndex);
			 newNode.saveMessage(msg, level+1);
			 
			 children.put(nextNodeIndex, newNode);
		 }
		 else
		 {
			 int nextNodeIndex = msgPath.get(level);
			 if (!children.containsKey(nextNodeIndex))
			 {
				 System.out.println("Error adding msg");
			 }
			 Node newNode = children.get(nextNodeIndex);
			 newNode.saveMessage(msg, level+1);
			 
			 children.put(nextNodeIndex, newNode);
		 }
			 
			 
	}
	
	public int getInput() {
		return input;
	}
	public void setInput(int input) {
		this.input = input;
	}
	public int getOutput() {
		return output;
	}
	public void setOutput(int output) {
		this.output = output;
	}
	public List<Integer> getPath() {
		return path;
	}
	public void setPath(List<Integer> path) {
		this.path = path;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public Map<Integer, Node> getChildren() {
		return children;
	}
	public void setChildren(Map<Integer, Node> children) {
		this.children = children;
	}
	
}