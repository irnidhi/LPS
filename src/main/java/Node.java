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
	
	public static final String ONE = "1";
	public static final String ZERO = "0";
	public static final String UNKNOWN = "?";
	public static final String FAULTY = "X";
	public static final String DEFAULT = "1";
	
	private Integer level;
	private Node parent;
	private Map<Integer, Node> children;
	
	private String input;
	private String output;
	private List<Integer> path;
	
	boolean isSaved;
	
	public Node() {
		super();
		this.level = 0;
		this.parent = null;
		this.children = new HashMap<Integer, Node>();
		this.input = ZERO;
		this.output = UNKNOWN;
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

	public String findMajorityValue(List<String> outputList){
		int oneCounter = 0;
		int zeroCounter = 0;
		int faultyCounter = 0;
		for (String value: outputList){
			if (value.equals(ONE))
				oneCounter++;
			else if (value.equals(ZERO))
				zeroCounter++;
			else if (value.equals(FAULTY))
				faultyCounter++;
		}
		int max = Math.max(oneCounter, Math.max(zeroCounter, faultyCounter));
		// if there is a tie always ONE will be returned
		if (max == oneCounter)
			return ONE;
		if (max == zeroCounter)
			return ZERO;
		if (max == faultyCounter)
			return FAULTY;
		else 
			return ONE;
			
	}
	public void majority(Node root){
		List<String> childrenOutputValues = new LinkedList<String>();
		Node currNode = root;
		currNode.output = currNode.input;
		System.out.println("value at node"+currNode.getPath()+"\ninput"+ root.input+ "output:"+root.output);
		
		if (!currNode.getChildren().isEmpty()){
			for (Node value : currNode.getChildren().values()) {
					//value.output = value.input;
					majority(value);
			}
			for (Node value2 :currNode.getChildren().values()){
				childrenOutputValues.add(value2.getOutput());
			}
			currNode.setOutput(findMajorityValue(childrenOutputValues));
			System.out.println("decide output at "+currNode.getLevel()+"is "+currNode.getOutput());
		}
//		for (Node value : children.values()) {
//			
//		    System.out.println("level: "+value.getLevel()+"input "+value.getInput()+"output "+value.getOutput());
//		    if (!value.getChildren().isEmpty()){
//		    	for (Node value2 : value.getChildren().values()) {
//					
//				    System.out.println("level: "+value2.getLevel()+"input "+value2.getInput()+"output "+value2.getOutput());
//		    	}
//		    }
//		}
	}
	
	public String getInput() {
		return input;
	}
	public void setInput(String input) {
		this.input = input;
	}
	public String getOutput() {
		return output;
	}
	public void setOutput(String output) {
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
	public Integer getLevel() {
		return level;
	}
}