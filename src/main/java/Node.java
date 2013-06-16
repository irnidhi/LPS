package main.java;

import java.util.ArrayList;
import java.util.Collection;
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
	boolean isDefault;
	private int numberOfProcesses;
	
	public String toString( ) {
        return "Node " + id.toString() + " level " + level.toString() + " path " + path.toString() + "\n";
    }
	
	public Node(int numberOfProcesses) {
		super();
		this.id = 0;
		this.level = 0;
		this.parent = null;
		this.children = new HashMap<Integer, Node>();
		this.input = 0;
		this.output = 0;
		this.path = new LinkedList<Integer>();
		
		this.isSaved = false;
		this.isDefault = false;
		
		this.numberOfProcesses = numberOfProcesses;
	}
	
	public Node(Node parent) {
		this.level = parent.level + 1;
		this.parent = parent;
		this.children = new HashMap<Integer, Node>();

		this.input = 0;
		this.output = 0;
		
		this.isSaved = false;
		this.isDefault = false;
		
		this.numberOfProcesses = parent.numberOfProcesses;
	}
	
	public void saveMessageInCurrentNode(Message msg)
	{
		this.input = msg.getVal();
		this.path = msg.getPath();
		
		this.id = this.path.get(this.path.size()-1);

		this.isSaved = true;
		this.isDefault = false;
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
	
	/**
	 * Returns all nodes from a given level. Used to fill in default values at the end of the round.
	 * @param levelNumber
	 * @return
	 */
	public List<Node> getNodesFromLevel(int levelNumber)
	{
		List<Node> nodesToVisit = new LinkedList<Node>();
		nodesToVisit.add(this);
		
		List<Node> levelNodes = new LinkedList<Node>();
		
		int currentLevel = 0;
		while (currentLevel <= levelNumber && nodesToVisit.size() != 0)
		{
			Node currentNode = nodesToVisit.remove(0);
			nodesToVisit.addAll(currentNode.children.values());
			
			currentLevel = currentNode.level;
			
			if (currentLevel == levelNumber)
			{
				levelNodes.add(currentNode);
			}

		}
		
		return levelNodes;
		
	}
	
	/**
	 * Filling up given level (starting from 0) for a currentNodeId with a default values
	 * in case some messages were not received.
	 * @param levelNumber
	 * @param currentNodeId
	 */
	public void fillLevelWithDefaultValues(int levelNumber, int currentNodeId)
	{
		List<Node> levelAboveNodes = this.getNodesFromLevel(levelNumber - 1);
		for (Node node : levelAboveNodes) {

			int parentId = node.id;
			int generalId = node.path.get(0);
			List<Integer> childIds = new LinkedList<Integer>();
			Collection<Node> children = node.children.values();
			for (Node child : children) {
				childIds.add(child.id);
			}
			
			List<Integer> idsToExclude = new LinkedList<Integer>();
			idsToExclude.add(currentNodeId);
			idsToExclude.add(parentId);
			idsToExclude.add(generalId);
			idsToExclude.addAll(childIds);
			
			for (int i = 1; i <= numberOfProcesses; i++) {
				if (!idsToExclude.contains(i)){
					Message defaultMessage = Message.getDefaultMessage(i, node.path);
					
					 if (!node.children.containsKey(i))
					 {
						 node.children.put(i, new Node(node));
					 }
					 Node newNode = node.children.get(i);
					 newNode.saveMessageInCurrentNode(defaultMessage);
					 newNode.isDefault = true;
					 
					 node.children.put(i, newNode);
					 
				}
			}
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