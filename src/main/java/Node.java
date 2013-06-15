package main.java;

import java.util.List;

/*
 * This class has the contents for every node in the message tree.
 * Node has a parent, list of its children, and its own input and 
 * output values
 */
public class Node{
	
	private Node parent;
	private List<Node> children;
	
	private int input;
	private int output;
	private String path;
	
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
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public Node getParent() {
		return parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public List<Node> getChildren() {
		return children;
	}
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	
}