package datamodel;

import org.garret.perst.Persistent;


public class FBClassVersion extends Persistent {
	
	public transient FBHistoryNode historyNode;
	public transient String className;
	public transient String packageName;
	public int startLine;
	public int endLine;
	
	public FBClassVersion(FBHistoryNode node, String className, String packageName, int startLine, int endLine) {
		
		this.historyNode = node;
		this.className = className;
		this.packageName = packageName;
		this.startLine = startLine;
		this.endLine = endLine;
		
	}
	public FBClassVersion() {}
	
}
