package sjmhrp.io.xmlLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class XMLNode {

	private String name;
	private HashMap<String,String> attributes = new HashMap<String,String>();
	private String data;
	private HashMap<String,ArrayList<XMLNode>> children = new HashMap<String,ArrayList<XMLNode>>();
	
	public XMLNode(String name) {
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getData() {
		return data;
	}
	
	public String getAttribute(String att) {
		return attributes.get(att);
	}
	
	public XMLNode getChild(String name) {
		ArrayList<XMLNode> c = children.get(name);
		if(c==null||c.isEmpty())return null;
		return c.get(0);
	}
	
	public XMLNode getChild(String name, String att, String data) {
		ArrayList<XMLNode> c = children.get(name);
		if(c==null||c.isEmpty())return null;
		for(XMLNode node : c) {
			if(data.equals(node.getAttribute(att)))return node;
		}
		return null;
	}
	
	public ArrayList<XMLNode> getChildren(String name) {
		ArrayList<XMLNode> c = children.get(name);
		return c==null?new ArrayList<XMLNode>():c;
	}
	
	public void addAttribute(String att, String data) {
		attributes.put(att,data);
	}
	
	public void addChild(XMLNode child) {
		ArrayList<XMLNode> nodes = children.get(child.getName());
		if(nodes==null) {
			nodes = new ArrayList<XMLNode>();
			children.put(child.getName(),nodes);
		}
		nodes.add(child);
	}
	
	public void setData(String data) {
		this.data = data;
	}
}