package old;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class FBBuildProcess {
	
	public String basePath;
	
	//Should be folders, not individual classes (but could include jars)
	public List<String> classPaths = new ArrayList<String>();
	//Will be run in the project directory
	public List<String> buildCommands = new ArrayList<String>();
	
	
	public String toString() {
		
		String out = this.basePath;
		out += "\n" + "Class paths:";
		for (String classPath : classPaths) {
			out += "\n\t" + classPath;
		}
		out += "\n" + "Build Commands";
		for (String cmd : buildCommands) {
			out += "\n\t" + cmd;
		}
		return out;
		
	}
	
}
