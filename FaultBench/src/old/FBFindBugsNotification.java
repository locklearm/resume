package old;
import java.util.Scanner;

import org.garret.perst.Persistent;



public class FBFindBugsNotification extends Persistent {
	
	public FBHistoryNode historyNode;
	
	public String alertType;
	public String alertCategory;
	public int alertPriority;
	public String						primaryClassName;
	public int							primaryClassStartLine;
	public int							primaryClassEndLine;
	public boolean isClassSignificant;
	public String						primaryClassPackageName;
	public String						primaryMethodName;
	public int							primaryMethodStartLine;
	public int							primaryMethodEndLine;
	public boolean isMethodSignificant;
	public String						primaryFieldName;
	public int							primaryFieldStartLine;
	public int							primaryFieldEndLine;
	public boolean isFieldSignificant;
	public String						primaryLocalVariableName;
	public boolean isLocalVariableSignificant;
	public int							primarySourceLineStart;
	public int							primarySourceLineEnd;
	public String						userAnnotation;
	public String						userDesignation;
	public int							bugRank;
	public String						bugRankCategory;
	public String						instanceHash;
	public String						instanceKey;
	
	
	public FBFindBugsNotification(FBHistoryNode historyNode, String alertType, String alertCategory, String instanceHash, String instanceKey, int primarySourceLineStart, int primarySourceLineEnd) {
		this.historyNode = historyNode;
		this.alertType = alertType;
		this.alertCategory = alertCategory;
		this.instanceHash = instanceHash;
		this.instanceKey = instanceKey;
		this.primarySourceLineStart = primarySourceLineStart;
		this.primarySourceLineEnd = primarySourceLineEnd;
	}
	
	
	public String toString() {
		String out = "";
		return out;
	}
//	
//	public static FBFindBugsNotification fromString(String bugString) {
//		
//		FBFindBugsNotification b = new FBFindBugsNotification();
//		
//		boolean seenEnd = false;
//		
//		Scanner s = new Scanner(bugString);
//		while (s.hasNextLine()) {
//			
//			String line = s.nextLine();
//			
//			if (line.startsWith("BUG_START")) {
//				continue;
//			}
//			
//			if (line.startsWith("BUG_END")) {
//				seenEnd = true;
//				break;
//			}
//			
//		}
//		
//		
//		
//		if (!seenEnd) {
//			return null;
//		}
//		else {
//			return b;
//		}
//	}
	
}
