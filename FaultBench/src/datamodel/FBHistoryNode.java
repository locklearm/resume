package datamodel;

import java.util.Map;

import org.garret.perst.FieldIndex;
import org.garret.perst.Persistent;
import org.garret.perst.Storage;

import analysis.FBJavaSourceAnalyzer;
import enums.FBNodeType;


public class FBHistoryNode extends Persistent {
	
	public transient String nodeID;
	public transient String branchID;
	public transient long revisionID;
	public long offset;
	public FBNodeType type;
	public boolean sourceAnalyzed;
	public String topLevelClassName;
	public String topLevelClassPackageName;
	public boolean bugsCollected;
	public FieldIndex<FBFindBugsAlertVersion> alerts;
	
	public FBHistoryNode(Storage db, String nodeID, String branchID, long revisionID, long offset, FBNodeType nodeType) {
		
		super(db);
		
		this.nodeID = nodeID;
		this.branchID = branchID;
		this.revisionID = revisionID;
		this.offset = offset;
		this.type = nodeType;
		this.sourceAnalyzed = false;
		this.bugsCollected = false;
		this.alerts = db.<FBFindBugsAlertVersion>createFieldIndex(FBFindBugsAlertVersion.class, "instanceHash", false);
		
		this.makePersistent(db);
		
	}
	
	public FBClassVersion getTopLevelClass(FBAnalysisProject aProj) {
		
		//If we haven't already analyzed this node
		if (!this.sourceAnalyzed) {
			
			//Then get analyzed
			if (!FBJavaSourceAnalyzer.analyzeFileNode(this, aProj.repo)) {
				//If we failed, return null
				return null;
			}
			
		}
		
		//Get the top level class from the project
		return aProj.getClassVersion(this.topLevelClassPackageName, this.topLevelClassName, this);
		
	}
	
	public Map<String, FBClassVersion> getClasses(FBAnalysisProject aProj) {
		
		return aProj.getNodeClasses(this);
		
	}
	
	
}
