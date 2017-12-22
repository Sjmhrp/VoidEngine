package sjmhrp.io.xmlLoader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sjmhrp.io.Log;

public class XMLParser {

	private static final Pattern DATA = Pattern.compile(">(.+?)<");
	private static final Pattern START_TAG = Pattern.compile("<(.+?)>");
	private static final Pattern ATTR_NAME = Pattern.compile("(.+?)=");
	private static final Pattern ATTR_VAL = Pattern.compile("\"(.+?)\"");
	private static final Pattern CLOSED = Pattern.compile("(</|/>)");
	
	public static XMLNode loadXMLFile(String filePath) {
		BufferedReader reader = null;
		try {
			InputStreamReader in = new InputStreamReader(Class.class.getResourceAsStream(filePath));
			reader = new BufferedReader(in);
			XMLNode node = loadNode(reader);
			reader.close();
			return node;
		} catch(Exception e) {
			Log.printError(e);
		}
		return null;
	}
	
	static XMLNode loadNode(BufferedReader reader) throws Exception {
		String line = reader.readLine().trim();
		if(line.startsWith("<?"))return loadNode(reader);
		if(line.startsWith("</"))return null;
		String[] parts = getStartTag(line).split(" ");
		XMLNode node = new XMLNode(parts[0].replace("/", ""));
		addAttributes(parts,node);
		addData(line,node);
		if(CLOSED.matcher(line).find())return node;
		XMLNode child = null;
		while((child=loadNode(reader))!=null) {
			node.addChild(child);
		}
		return node;
	}
	
	static void addData(String line, XMLNode node) {
		Matcher match = DATA.matcher(line);
		if(match.find())node.setData(match.group(1));
	}
	
	static void addAttributes(String[] lines, XMLNode node) {
		for(int i = 1; i < lines.length; i++) {
			if(lines[i].contains("="))addAttribute(lines[i],node);
		}
	}
	
	static void addAttribute(String line, XMLNode node) {
		Matcher matchName = ATTR_NAME.matcher(line);
		Matcher matchValue = ATTR_VAL.matcher(line);
		matchName.find();
		matchValue.find();
		node.addAttribute(matchName.group(1),matchValue.group(1));
	}
	
	static String getStartTag(String line) {
		Matcher match = START_TAG.matcher(line);
		match.find();
		return match.group(1);
	}
}